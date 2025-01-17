package com.example.soundtt.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.soundtt.AccSensor
import com.example.soundtt.AudioSensor
import com.example.soundtt.NearBy

class MainViewModel : ViewModel() {


    // 加速度センサ
    // 推定クラスはこいつが持ってる
    lateinit var accSensor: AccSensor
    // todo AudioSensor
    lateinit var audioSensor: AudioSensor
    // todo SoundPlayer
    lateinit var nearBy: NearBy
    // todo SoundPlayer


    // アプリ起動時にやっておきたい処理やインスタンス化
    fun start(context: Context) {
        accSensor = AccSensor(context)
        accSensor.start()
        audioSensor = AudioSensor()
        audioSensor.start(context)
        nearBy = NearBy(context)


        Log.d("MainViewModel","うわああああ")


    }

    // アプリ終了時に止めたい処理
    fun stop() {
        accSensor.stop()
        audioSensor.stop()
    }


}