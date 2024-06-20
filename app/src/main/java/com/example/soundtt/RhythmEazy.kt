package com.example.soundtt

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

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


        logstart.setOnClickListener {
            // 音声を再生
            playSound()
            start(this)

            showToast("開始")
        }

        btnpause.setOnClickListener {
            // ポーズダイアログを表示
            showPauseDialog()
        }

        logback.setOnClickListener {
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
    }

    fun stop() {
        if (::accSensor.isInitialized) {
            accSensor.stop()
        } else {
            showToast("センサーが初期化されていません")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
