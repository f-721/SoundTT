package com.example.soundtt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uk.me.berndporr.iirj.Butterworth
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class AccEstimation() {

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
    var max_acc:Double = 0.0
    var min_acc:Double = 0.0

    var bl_hit_updown = true
    var bl_swing_updown = true
    var hit_hantei = false
    var swing_hantei = false
    var bl_onhit = false
    var bl_onswing = false
    var hit_keep_thirty = 0

    var hit = 0.2
    var swing = 1.0


    // ヒットしたかどうかを推定する
    // 結果をMainApplicationのsetIsHitに投げる
    fun filter(x:Float, y: Float, z:Float): Triple<Double, Double, Long>{
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

        val HighPassNorm = kotlin.math.sqrt(kotlin.math.abs((hx * hx) + (hy * hy) + (hz * hz))) * 100
        val LowPassNorm = kotlin.math.sqrt(kotlin.math.abs((lx * lx) + (ly * ly) + (lz * lz))) * 10000000

        val nowtime: LocalDateTime
        nowtime = LocalDateTime.now()
        if (timeflag) {
            difftime = ChronoUnit.MILLIS.between(starttime, startendtime)
            timeflag = false
        } else {
            difftime = ChronoUnit.MILLIS.between(starttime, nowtime)
        }

        return Triple(HighPassNorm,LowPassNorm,difftime)
    }

    fun estimationIsHit(acc_num: Double, Nowtime: Long){

        // なんかいい感じの処理
        //持ってくる値はハイパスかけた後のノルムデータと時間
        if (Nowtime > 4000) {

            //開始4秒はカウントしない
            if (bl_hit_updown) {
                ///5秒（hit_out ３００回データ）経過するとカウントを０にする
                    //trueの場合、ハイパス後ノルムが1.0くらいを越えると１回カウント。
                    //1.0>0.2


                    if (acc_num > hit) {
                        hit_hantei = true
                        bl_hit_updown = false
                        bl_onhit = true //スイングヒットに使用
                        Log.d("Estimation", "acc_num? = $acc_num")
                        //Log.d("Estimation","editTextHit? = $editTextHit")
                        _isHit.postValue(true)
                    }/*else{
                        _isHit.postValue(false)
                    }
                    */
            } else if (!bl_hit_updown) {
                //falseの場合、30回データが送り込まれる(0.6秒)までヒット回数を数えないように
                hit_keep_thirty += 1
                if (hit_keep_thirty >= 30) {
                    bl_hit_updown = true
                    hit_keep_thirty = 0
                }
            }
        }
        }

    // スイングしたか推定する
    fun estimationIsSwing(acc_num: Double) {

        //持ってくる値はローパスかけた後のノルムデータ
        if (acc_num > max_acc) {
            //maxより大きかった場合置き換え
            max_acc = acc_num
        } else if (acc_num < max_acc) {
            //maxから加速度が下がった最初のタイミングでスイング推定を行う。
            //極大値と極小値の差を求め、diffnumが10以上だった場合スイングと推定。極小値のリセットとして極大値を入れる
            if (bl_swing_updown) {
                val diffnum = max_acc - min_acc

                //カウント処理に向かう（スイングfalseケース)
                //10.0>7.0

                //2.0>1.0
                if (diffnum > swing) {
                    swing_hantei = true
                    bl_onswing = true //スイングヒットのカウントに使用。
                    _isSwing.postValue(true)
                    Log.d("Estimation", "diff_num? = $diffnum")
                    //Log.d("Estimation","editTextHit? = $editTextSwing")
                }/*else{
                    _isSwing.postValue(false)
                }
                */
                //SwingHitCount() //カウント処理に向かう（スイングtrueケース）
                min_acc = max_acc
                bl_swing_updown = false
            }
        }
        if (acc_num < min_acc) {
            //minより小さかった場合置き換え　
            min_acc = acc_num
        } else if (acc_num > min_acc) {
            //minから加速度が上がった最初のタイミングで極大値をリセット。極大値に極小値を入れる。
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
        //_accTest.postValue("x = $x, y = $y, z = $z")
    }

}