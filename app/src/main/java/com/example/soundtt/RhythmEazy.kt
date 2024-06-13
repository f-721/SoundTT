package com.example.soundtt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File

class RhythmEazy : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaRecorder: MediaRecorder
    private var bufferSize: Int = 0
    //private lateinit var judgeTiming: JudgeTiming

    lateinit var accSensor: AccSensor
    lateinit var nearBy: NearBy

    lateinit var tvgreat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rhythmeazy)

        mediaPlayer = MediaPlayer.create(this, R.raw.rhythmrally1)

        tvgreat = findViewById(R.id.tvgreat)
        val btnpause: Button = findViewById(R.id.btnpause)
        val logstart: Button = findViewById(R.id.btnstart)
        val logback: Button = findViewById(R.id.btnback)
        val btnadvertise: Button = findViewById(R.id.btn_advertise)
        val btndiscovery: Button = findViewById(R.id.btn_discovery)

        // ViewModelProviderを使ってViewModelのインスタンスを作成
        //judgeTiming = ViewModelProvider(this, JudgeTimingFactory(AccEstimation())).get(JudgeTiming::class.java)


        logstart.setOnClickListener {
            // 音声を再生
            playSound()
            start(this)

            // 判定を開始
            //judgeTiming.startJudging()

            // judgementの変更を観察
//            judgeTiming.judgement.observe(this, Observer { judgement ->
//                Log.d("RhythmEazy", "Judgement observed: $judgement")
//                when (judgement) {
//                    "GREAT" -> {
//                        tvgreat.isVisible = true
//                        tvgood.isVisible = false
//                        tvbad.isVisible = false
//                        Log.d("RhythmEazy", "GREAT")
//                    }
//                    "GOOD" -> {
//                        tvgreat.isVisible = false
//                        tvgood.isVisible = true
//                        tvbad.isVisible = false
//                        Log.d("RhythmEazy", "GOOD")
//                    }
//                    "BAD" -> {
//                        tvgreat.isVisible = false
//                        tvgood.isVisible = false
//                        tvbad.isVisible = true
//                        Log.d("RhythmEazy", "BAD")
//                    }
//                }
//            })

            showToast("開始")
        }

        btnpause.setOnClickListener {
            // ポーズダイアログを表示
            showPauseDialog()
        }

        logback.setOnClickListener {
            //judgeTiming.stopJudging()
            stop()
            finish()
        }

        btnadvertise.setOnClickListener {
            //音声用端末との接続アルゴリズム
        }

        btndiscovery.setOnClickListener {
            //音声用端末との切断アルゴリズム
        }
    }

    private fun playSound() {
        mediaPlayer.apply {
            if (isPlaying) {
                stop()
                prepare()
            }
            start()
        }
    }

    private fun startRecording() {
        bufferSize = AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val file = File(applicationContext.filesDir, "recorded_audio.3gp")
        mediaRecorder = MediaRecorder()
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)

            try {
                prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showPauseDialog() {
        AlertDialog.Builder(this)
            .setTitle("PAUSE")
            .setPositiveButton("再開") { dialog, which ->
                mediaPlayer.start()
            }
            .setNegativeButton("リトライ") { dialog, which ->
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun start(context: Context) {
        accSensor = AccSensor(context,tvgreat)
        accSensor.start()
        nearBy = NearBy(context)
        Log.d("MainViewModel", "うわああああ")
    }

    fun stop() {
        accSensor.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
