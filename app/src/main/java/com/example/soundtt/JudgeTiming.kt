package com.example.soundtt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class JudgeTiming : ViewModel() {

    private val _judgement = MutableLiveData<String>()
    val judgement: LiveData<String> = _judgement

    private val accEstimation = AccEstimation()
    private var job: Job? = null

    private val hitObserver = Observer<Boolean> { isHit ->
        if (isHit) {
            recordHit()
        }
    }

    private val swingObserver = Observer<Boolean> { isSwing ->
        // Add any swing related logic if needed
    }

    init {
        accEstimation.isHit.observeForever(hitObserver)
        accEstimation.isSwing.observeForever(swingObserver)
    }

    fun startJudging() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(2000)  // 2秒ごとに実行
                val now = System.currentTimeMillis()
                accEstimation.let {
                    val accData = it.getAccData()  // ここでセンサーデータを取得する方法を実装
                    val (highPassNorm, lowPassNorm, difftime) = it.filter(accData.x, accData.y, accData.z)
                    it.estimationIsHit(highPassNorm, difftime)
                }
                // 判定結果の更新
                val timeDiff = System.currentTimeMillis() - lastHitTime
                when {
                    timeDiff <= 500 -> _judgement.postValue("GREAT")
                    timeDiff <= 750 -> _judgement.postValue("GOOD")
                    else -> _judgement.postValue("BAD")
                }
            }
        }
    }

    fun stopJudging() {
        job?.cancel()
        accEstimation.isHit.removeObserver(hitObserver)
        accEstimation.isSwing.removeObserver(swingObserver)
    }

    private var lastHitTime = 0L

    private fun recordHit() {
        lastHitTime = System.currentTimeMillis()
    }

    override fun onCleared() {
        super.onCleared()
        stopJudging()
    }
}
