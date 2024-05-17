package com.example.soundtt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.soundtt.ui.main.MainFragment
import java.io.File

class RhythmEazy : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaRecorder: MediaRecorder
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var bufferSize: Int = 0

    private lateinit var judgeTiming: JudgeTiming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rhythmeazy)

        mediaPlayer = MediaPlayer.create(this, R.raw.rhythmrally1)

        val tvgreat: TextView = findViewById(R.id.tvgreat)
        val tvgood: TextView = findViewById(R.id.tvgood)
        val tvbad: TextView = findViewById(R.id.tvbad)
        val btnpause: Button = findViewById(R.id.btnpause)
        val logEazyStart: Button = findViewById(R.id.btnHardStart)
        val btnback: Button = findViewById(R.id.btnback)

        judgeTiming = ViewModelProvider(this).get(JudgeTiming::class.java)

        logEazyStart.setOnClickListener {
            // 音声を再生
            playSound()

            // 録音を開始
            startRecording()

            // 判定を開始
            judgeTiming.startJudging()

            // ヒット判定の結果を観察
            judgeTiming.judgement.observe(this, Observer { judgement ->
                when (judgement) {
                    "GREAT" -> {
                        tvgreat.text = "GREAT"
                        tvgood.text = ""
                        tvbad.text = ""
                    }
                    "GOOD" -> {
                        tvgreat.text = ""
                        tvgood.text = "GOOD"
                        tvbad.text = ""
                    }
                    "BAD" -> {
                        tvgreat.text = ""
                        tvgood.text = ""
                        tvbad.text = "BAD"
                    }
                }
            })

            showToast("録音開始")
        }

        btnpause.setOnClickListener {
            // ポーズダイアログを表示
            showPauseDialog()
        }

        btnback.setOnClickListener {
            if (isRecording) {
                // 録音を停止
                stopRecording()
                // 判定を停止
                judgeTiming.stopJudging()
                // Toastメッセージを表示
                showToast("録音終了")
            }

            // メインフラグメントに戻る
            val intent = Intent(this, MainFragment::class.java)
            startActivity(intent)
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

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder.stop()
            mediaRecorder.release()
            isRecording = false
        }
    }

    private fun showPauseDialog() {
        AlertDialog.Builder(this)
            .setTitle("PAUSE")
            .setPositiveButton("再開") { dialog, which ->
                mediaPlayer.start()
                startRecording()
            }
            .setNegativeButton("リトライ") { dialog, which ->
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
                startRecording()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopRecording()
    }
}
