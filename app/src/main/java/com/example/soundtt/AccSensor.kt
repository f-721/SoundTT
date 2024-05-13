package com.example.soundtt

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

public class AccSensor(private val context: Context): SensorEventListener {

    //加速度センサーを始める時のおまじない
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    val accEstimation = AccEstimation()

    //取得開始
    fun start(){
        sensorManager.registerListener(this, AccSensor, SensorManager.SENSOR_DELAY_UI)
    }
    //取得終了
    fun stop(){
        sensorManager.unregisterListener(this)
    }

    //これが動き続けてデータを取得する
    override fun onSensorChanged(event: SensorEvent) {

        //センサーが加速度センサーなら
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val accX = event.values[0]
            val accY = event.values[1]
            val accZ = event.values[2]

            val (HighPassNorm,LowPassNorm,difftime) = accEstimation.filter(accX,accY,accZ)
            accEstimation.estimationIsHit(HighPassNorm,difftime)
            accEstimation.estimationIsSwing(LowPassNorm)
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}