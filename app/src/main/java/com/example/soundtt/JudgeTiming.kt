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
                delay(1600)
                val timeDiff = System.currentTimeMillis() - lastHitTime
                Log.d("JudgeTiming", "Time difference: $timeDiff ms")

                lastHitTime = System.currentTimeMillis()

                when {
                    timeDiff <= 2000 -> _judgement.postValue("GREAT")
                    timeDiff <= 3000 -> _judgement.postValue("GOOD")
                    else -> _judgement.postValue("BAD")
                }
            }
        }
    }

    fun stopJudging() {
        job?.cancel()
        accEstimation.isHit.removeObserver(hitObserver)
    }

    private var lastHitTime = System.currentTimeMillis()

    private fun recordHit() {
        val currentTime = System.currentTimeMillis()
        Log.d("JudgeTiming", "Hit recorded at: $currentTime")
        lastHitTime = currentTime
    }

    override fun onCleared() {
        super.onCleared()
        stopJudging()
    }
}
