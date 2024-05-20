package com.example.soundtt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uk.me.berndporr.iirj.Butterworth
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.sqrt

class AccEstimation {

    val _isHit = MutableLiveData<Boolean>(false)
    val isHit: LiveData<Boolean> = _isHit

    val _isSwing = MutableLiveData<Boolean>(false)
    val isSwing: LiveData<Boolean> = _isSwing

    private val _accTest = MutableLiveData<String>("")
    val accTest: LiveData<String> = _accTest

    //フィルターかけるようの宣言
    var timeflag = true
    var difftime: Long = 1
    var starttime = LocalDateTime.now()
    var startendtime = LocalDateTime.now()
    var max_acc: Double = 0.0
    var min_acc: Double = 0.0

    var bl_hit_updown = true
    var bl_swing_updown = true
    var hit_hantei = false
    var swing_hantei = false
    var bl_onhit = false
    var bl_onswing = false
    var hit_keep_thirty = 0

    var hit = 0.2
    var swing = 1.0

    data class AccData(val x: Float, val y: Float, val z: Float)

    fun getAccData(): AccData {
        // センサーデータを取得するロジックを実装
        // ここではダミーデータを返します
        return AccData(1.0f, 1.0f, 1.0f)
    }

    fun filter(x: Float, y: Float, z: Float): Triple<Double, Double, Long> {
        val (butterworth_hx, butterworth_hy, butterworth_hz) = listOf(Butterworth(), Butterworth(), Butterworth())
        val (butterworth_lx, butterworth_ly, butterworth_lz) = listOf(Butterworth(), Butterworth(), Butterworth())

        butterworth_hx.highPass(10, 50.0, 15.0)
        butterworth_hy.highPass(10, 50.0, 15.0)
        butterworth_hz.highPass(10, 50.0, 15.0)
        butterworth_lx.lowPass(10, 50.0, 3.0)
        butterworth_ly.lowPass(10, 50.0, 3.0)
        butterworth_lz.lowPass(10, 50.0, 3.0)

        val (hx, hy, hz) = listOf(butterworth_hx.filter(x.toDouble()), butterworth_hy.filter(y.toDouble()), butterworth_hz.filter(z.toDouble()))
        val (lx, ly, lz) = listOf(butterworth_lx.filter(x.toDouble()), butterworth_ly.filter(y.toDouble()), butterworth_lz.filter(z.toDouble()))

        val highPassNorm = sqrt((hx * hx) + (hy * hy) + (hz * hz)) * 100
        val lowPassNorm = sqrt((lx * lx) + (ly * ly) + (lz * lz)) * 10000000

        val nowtime = LocalDateTime.now()
        difftime = if (timeflag) {
            ChronoUnit.MILLIS.between(starttime, startendtime).also { timeflag = false }
        } else {
            ChronoUnit.MILLIS.between(starttime, nowtime)
        }

        return Triple(highPassNorm, lowPassNorm, difftime)
    }

    fun estimationIsHit(acc_num: Double, nowtime: Long) {
        // ヒット判定のロジックを実装
        if (nowtime > 4000) {
            if (bl_hit_updown) {
                if (acc_num > hit) {
                    hit_hantei = true
                    bl_hit_updown = false
                    bl_onhit = true
                    Log.d("Estimation", "acc_num = $acc_num")
                    _isHit.postValue(true)
                }
            } else {
                hit_keep_thirty += 1
                if (hit_keep_thirty >= 30) {
                    bl_hit_updown = true
                    hit_keep_thirty = 0
                }
            }
        }
    }

    fun estimationIsSwing(acc_num: Double) {
        // スイング判定のロジックを実装
        if (acc_num > max_acc) {
            max_acc = acc_num
        } else if (acc_num < max_acc) {
            if (bl_swing_updown) {
                val diffnum = max_acc - min_acc
                if (diffnum > swing) {
                    swing_hantei = true
                    bl_onswing = true
                    _isSwing.postValue(true)
                    Log.d("Estimation", "diff_num = $diffnum")
                }
                min_acc = max_acc
                bl_swing_updown = false
            }
        }
        if (acc_num < min_acc) {
            min_acc = acc_num
        } else if (acc_num > min_acc) {
            if (!bl_swing_updown) {
                max_acc = min_acc
                bl_swing_updown = true
            }
        }
    }

    fun accTest(x: Float, y: Float, z: Float) {
        val maxNum = maxOf(x, y, z)
        val maxVar = when (maxNum) {
            x -> "x"
            y -> "y"
            z -> "z"
            else -> "Unknown"
        }
        _accTest.postValue(maxVar)
    }
}
