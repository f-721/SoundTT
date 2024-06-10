package com.example.soundtt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class JudgeTiming(accEstimation: AccEstimation) : ViewModel() {

    private val _judgement = MutableLiveData<String>()
    val judgement: LiveData<String> = _judgement

    private val accEstimation = AccEstimation()
    //private var isHitJudge = MutableLiveData<Boolean>(accEstimation.isHit.value)
    private var job: Job? = null

    private var lastHitTime = 0L

    private val hitObserver = Observer<Boolean> { isHit ->
        if (isHit) {
            recordHit()
        }
        Log.d("JudgeTiming","isHitに変更あり + " + "$isHit")
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
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {

                delay(2000)

                val nowtime = System.currentTimeMillis()

                // Check the time difference if there's a recorded hit
                val timeDiff = if (lastHitTime != 0L) {
                    abs(nowtime - lastHitTime)
                } else {
                    2001L
                }
                Log.d("JudgeTiming", "-------------------")
//                Log.d("JudgeTiming", "${accEstimation.GetIsHit()}")
                Log.d("JudgeTiming", "nowtime(判定時刻): $nowtime ms")
                Log.d("JudgeTiming", "ヒット時刻: $lastHitTime ms")
                Log.d("JudgeTiming", "Time difference(判定時刻-ヒット時刻): $timeDiff ms")
                Log.d("JudgeTiming", "-------------------")

                when {
                    timeDiff in 1..1000 -> {
                        _judgement.postValue("GREAT")
                        Log.d("JudgeTiming", "Judgement: GREAT")
                    }
                    timeDiff in 1001..1200 -> {
                        _judgement.postValue("GOOD")
                        Log.d("JudgeTiming", "Judgement: GOOD")
                    }
                    timeDiff > 1200 && lastHitTime != 0L -> {
                        _judgement.postValue("BAD")
                        Log.d("JudgeTiming", "Judgement: BAD")
                    }
                    //else -> _judgement.postValue("NO HIT")
                }

                // Reset lastHitTime if more than 2 seconds have passed since last hit
                if (lastHitTime != 0L && timeDiff > 2000) {
                    lastHitTime = 0L
                    Log.d("JudgeTiming", "Last hit time reset to 0")
                }
            }
        }
    }

    fun stopJudging() {
        job?.cancel()
        accEstimation.isHit.removeObserver(hitObserver)
        accEstimation.lastHitTime.removeObserver(lastHitTimeObserver)
    }

    private fun recordHit() {
        lastHitTime = System.currentTimeMillis()
        Log.d("JudgeTiming", "Hit recorded at: $lastHitTime")
    }

    override fun onCleared() {
        super.onCleared()
        stopJudging()
    }
}
