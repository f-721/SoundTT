package com.example.soundtt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.log10
import kotlin.math.sqrt

class AudioSensor {
    val sampleRate = 44100
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private var buffer:ShortArray = ShortArray(bufferSize)

    lateinit var audioRecord: AudioRecord
    private var isRecording = false

    private var volume:Double = 0.0

    val _isVolume = MutableLiveData<Int>(0)
    val isVolume: LiveData<Int> = _isVolume



    fun calculateVolume(buffer: ShortArray, readSize: Int): Double {
        // 最大音量を解析
        val sum = buffer.sumOf { it.toDouble() * it.toDouble() }
        val amplitude = sqrt(sum / bufferSize)
        // デシベル変換
        val db = (20.0 * log10(amplitude))
        return db
    }

    fun start(context: Context) {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            audioRecord.startRecording()
            isRecording = true

            Thread {
                //val buffer = ShortArray(bufferSize)
                while (isRecording) {
                    val readSize = audioRecord.read(buffer, 0, bufferSize)
                    if (readSize > 0) {
                        volume = calculateVolume(buffer, readSize)
                        _isVolume.postValue(volume.toInt())
                    }
                }
            }.start()
        }

    }

    fun stop(){
        audioRecord.stop()
    }

    fun getVolume():Int{
        return volume.toInt()
    }

}