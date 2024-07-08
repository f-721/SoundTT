package com.example.soundtt

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.TextView

public class AccSensor(private val context: Context,
                       private val tvgreat: TextView,
private val accEstimation: AccEstimation,
                       private val nearbyManager: NearBy,
                       private val judgeTiming: JudgeTiming
): SensorEventListener {

    //加速度センサーを始める時のおまじない
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)


    fun start() {
        Log.d("AccSensor", "Music Start")
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI)
        judgeTiming.startJudging()
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        judgeTiming.stopJudging()
    }

    //これが動き続けてデータを取得する
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val accX = event.values[0]
            val accY = event.values[1]
            val accZ = event.values[2]

            val (HighPassNorm, LowPassNorm, difftime) = accEstimation.filter(accX, accY, accZ)
            accEstimation.estimationIsHit(HighPassNorm, difftime)
            accEstimation.estimationIsSwing(LowPassNorm)
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}