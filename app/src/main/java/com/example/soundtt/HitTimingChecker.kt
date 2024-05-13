package com.example.soundtt

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object HitTimingChecker {

    private var intendedHitTime: Long = 0
    private var hitDetectionTime: Long = 0

    init {
        val starttime = LocalDateTime.now()
        intendedHitTime = starttime.plusSeconds(4).toEpochSecond() * 1000 // 開始後4秒後を予定のヒットタイミングとする
    }

    fun setHitDetectionTime(time: Long) {
        hitDetectionTime = time
    }

    fun checkHitTiming(): Boolean {
        val currentTime = LocalDateTime.now().toEpochSecond() * 1000
        val deviation = Math.abs(currentTime - intendedHitTime - hitDetectionTime)
        return deviation <= 1000 // ずれが1秒以内ならtrueを返す
    }
}

private fun LocalDateTime.toEpochSecond(): Long {

    return TODO("Provide the return value")
}
