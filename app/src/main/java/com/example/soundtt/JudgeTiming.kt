package com.example.soundtt

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class JudgeTiming : ViewModel() {

    private val _judgement = MutableLiveData<String>()
    val judgement: LiveData<String> = _judgement

    private val handler = Handler(Looper.getMainLooper())
    private var startTime: LocalDateTime? = null
    private var lastHitTime: LocalDateTime? = null

    fun startJudging() {
        startTime = LocalDateTime.now()
        handler.post(judgingRunnable)
    }

    fun stopJudging() {
        handler.removeCallbacks(judgingRunnable)
    }

    private val judgingRunnable = object : Runnable {
        override fun run() {
            val currentTime = LocalDateTime.now()
            lastHitTime?.let {
                val timeDiff = ChronoUnit.MILLIS.between(it, currentTime)
                val judgementResult = when {
                    timeDiff <= 500 -> "GREAT"
                    timeDiff <= 750 -> "GOOD"
                    else -> "BAD"
                }
                _judgement.postValue(judgementResult)
            }
            handler.postDelayed(this, 2000)
        }
    }

    fun recordHit() {
        lastHitTime = LocalDateTime.now()
    }
}
