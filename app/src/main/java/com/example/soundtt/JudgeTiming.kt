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
import kotlin.math.abs

class JudgeTiming(private val accEstimation: AccEstimation,private val tvgreat: TextView) : ViewModel() {

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
            delay(2000)
            while (isActive) {
                delay(2000)
                judgeHitTiming()
            }
        }
    }

    fun stopJudging() {
        job?.cancel()
        accEstimation.isHit.removeObserver(hitObserver)
        accEstimation.lastHitTime.removeObserver(lastHitTimeObserver)
    }

    private fun judgeHitTiming() {
        val nowtime = System.currentTimeMillis()
        val timeDiff = if (lastHitTime != 0L) abs(nowtime - lastHitTime) else 2001L

        Log.d("JudgeTiming", "-------------------")
        Log.d("JudgeTiming", "nowtime(判定時刻): $nowtime ms")
        Log.d("JudgeTiming", "ヒット時刻: $lastHitTime ms")
        Log.d("JudgeTiming", "Time difference(判定時刻-ヒット時刻): $timeDiff ms")

        when {
            timeDiff in 1..1000 ->{
                //postJudgement("GREAT")
                tvgreat.text = "GREAT"
                Log.d("JudgeTiming","GREATです")
            }
            timeDiff in 1001..1700 ->{
                //postJudgement("GOOD")
                tvgreat.text = "GOOD"
                Log.d("JudgeTiming","GOODです")
            }
            timeDiff > 1700 && lastHitTime != 0L -> {
                //postJudgement("BAD")
                tvgreat.text = "BAD"
                Log.d("JudgeTiming","BADです")
            }
        }

        if (lastHitTime != 0L && timeDiff > 2000) {
            lastHitTime = 0L
            Log.d("JudgeTiming", "Last hit time reset to 0")
        }
    }

    private fun postJudgement(judgement: String) {
        _judgement.postValue(judgement)
        Log.d("JudgeTiming", "Judgement: $judgement")
        Log.d("JudgeTiming", "-------------------")
    }

    private fun recordHit() {
        lastHitTime = System.currentTimeMillis()
//        Log.d("JudgeTiming", "Hit recorded at: $lastHitTime")
    }

    override fun onCleared() {
        super.onCleared()
        stopJudging()
    }
}
