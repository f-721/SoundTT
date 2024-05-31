package com.example.soundtt

import android.content.Context
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

class JudgeTiming(context: Context) : ViewModel() {

    private val _judgement = MutableLiveData<String>()
    val judgement: LiveData<String> = _judgement

    private val accEstimation = AccEstimation()
    private var job: Job? = null

    private val hitObserver = Observer<Boolean> { isHit ->
        if (isHit) {
            recordHit()
        }
    }

    init {
        accEstimation.isHit.observeForever(hitObserver)
    }

    fun startJudging() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(2000)

                 var nowtime = System.currentTimeMillis()

                // Check the time difference if there's a recorded hit
                val timeDiff = if (lastHitTime != 0L) {
                    nowtime - lastHitTime
                } else {
                    2001L
                }
                Log.d("JudgeTiming", "Time difference: $timeDiff ms")

                when {
                    timeDiff in 1..1000 -> _judgement.postValue("GREAT")
                    timeDiff in 1001..1200 -> _judgement.postValue("GOOD")
                    timeDiff > 1200 -> _judgement.postValue("BAD")
                    else -> _judgement.postValue("NO HIT")
                }

                // Reset lastHitTime if more than 2 seconds have passed
                if (timeDiff > 2000) {
                    lastHitTime = 0L
                }
            }
        }
    }

    fun stopJudging() {
        job?.cancel()
        accEstimation.isHit.removeObserver(hitObserver)
    }

    private var lastHitTime = 0L

    private fun recordHit() {
        lastHitTime = System.currentTimeMillis()
        Log.d("JudgeTiming", "Hit recorded at: $lastHitTime")
    }

    override fun onCleared() {
        super.onCleared()
        stopJudging()
    }
}
