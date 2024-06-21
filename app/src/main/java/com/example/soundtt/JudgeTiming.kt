package com.example.soundtt

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class JudgeTiming(private val accEstimation: AccEstimation, private val tvgreat: TextView,private  val nearBy: NearBy) : ViewModel() {

    private val _judgement = MutableLiveData<String>()
    val judgement: LiveData<String> get() = _judgement

    private var job: Job? = null
    private var lastHitTime = 0L

    private val hitObserver = Observer<Boolean> { isHit ->
        if (isHit) {
            recordHit()
        }
        Log.d("JudgeTiming", "isHit changed: $isHit")
    }

    private val lastHitTimeObserver = Observer<Long> { newLastHitTime ->
        lastHitTime = newLastHitTime
        Log.d("JudgeTiming", "Observed lastHitTime: $newLastHitTime")
    }

    init {
        Log.d("JudgeTiming", "JudgeTiming initialized")
        accEstimation.isHit.observeForever(hitObserver)
        accEstimation.lastHitTime.observeForever(lastHitTimeObserver)
    }

    fun startJudging() {
        job = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(2000)
                triggerJudging()
            }
        }
    }
    fun stopJudging() {
        job?.cancel()
        accEstimation.isHit.removeObserver(hitObserver)
        accEstimation.lastHitTime.removeObserver(lastHitTimeObserver)
    }

    fun triggerJudging() {
        val nowtime = System.currentTimeMillis()
        val timeDiff =
            if (lastHitTime != 0L)
                (nowtime - lastHitTime) - 1000
            else 2001L

        Log.d("JudgeTiming", "-------------------")
        Log.d("JudgeTiming", "ゲーム内判定時刻: $nowtime ms")
        Log.d("JudgeTiming", "ヒット時刻: $lastHitTime ms")
        Log.d("JudgeTiming", "Time difference(ゲーム内判定時刻-ヒット時刻): $timeDiff ms")

        when {
            timeDiff in -500..500 -> {
                tvgreat.text = "GREAT"
                Log.d("JudgeTiming", "GREATです")
            }
            timeDiff in -750..-501 || timeDiff in 501..750 -> {
                tvgreat.text = "GOOD"
                Log.d("JudgeTiming", "GOODです")
            }
            timeDiff in -999..-751 || timeDiff in 751..999 -> {
                tvgreat.text = "BAD"
                Log.d("JudgeTiming", "BADです")
            }
            else -> {
                tvgreat.text = "MISS"
                Log.d("JudgeTiming", "失敗(MISS)")
            }
        }

        // `timeDiff` を Nearby に送信
        nearBy.sendTimeDiff(timeDiff)

        if (lastHitTime != 0L && (timeDiff < -1000 || timeDiff > 1000 )) {
            lastHitTime = 0L
            Log.d("JudgeTiming", "判定リセットします！")
        }
        Log.d("JudgeTiming", "-------------------")
    }

    private fun postJudgement(judgement: String) {
        _judgement.postValue(judgement)
        Log.d("JudgeTiming", "Judgement: $judgement")
        Log.d("JudgeTiming", "-------------------")
    }

    private fun recordHit() {
        lastHitTime = System.currentTimeMillis()
    }

    override fun onCleared() {
        super.onCleared()
        stopJudging()
    }
}
