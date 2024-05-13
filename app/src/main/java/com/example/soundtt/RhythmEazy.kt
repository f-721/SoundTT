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
import com.example.soundtt.ui.main.MainFragment
import java.io.File

class RhythmEazy : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaRecorder: MediaRecorder
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var bufferSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rhythmeazy)

        mediaPlayer = MediaPlayer.create(this,R.raw.rhythmrally1)


        val tvex: TextView = findViewById(R.id.tvex)
        val tvgreat: TextView = findViewById(R.id.tvgreat)
        val tvgood: TextView = findViewById(R.id.tvgood)
        val tvbad: TextView = findViewById(R.id.tvbad)
        val btnpause: Button = findViewById(R.id.btnpause)
        val logEazyStart: Button = findViewById(R.id.btnHardStart)
        val btnback: Button = findViewById(R.id.btnback)


        logEazyStart.setOnClickListener {

            // 音声を再生
            playSound()

            // 録音を開始
            startRecording()

            showToast("録音開始")
        }

        btnpause.setOnClickListener {
            // ポーズダイアログを表示
            showPauseDialog()
        }


        btnback.setOnClickListener {
            // 録音を停止
            stopRecording()
            // Toastメッセージを表示
            showToast("録音終了")

            // メインフラグメントに戻る
            val intent = Intent(this, MainFragment::class.java)
            startActivity(intent)
        }

    }

    // 音声を再生する関数
    private fun playSound() {
        // MediaPlayerを初期化し、音声ファイルを再生
        mediaPlayer.apply {
            // 再生が終了したら、再度再生できるようにリセット
            if (isPlaying) {
                stop()
                prepare()
            }
            start()
        }
    }

    // 録音を開始する関数
    private fun startRecording() {
        // 録音の設定
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
            // TODO: Consider calling ActivityCompat#requestPermissions here
            return
        }

        // MediaRecorderの設定
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

    // 録音を停止する関数
    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder.stop()
            mediaRecorder.release()
            isRecording = false
        }
    }

    // ポーズダイアログを表示する関数
    private fun showPauseDialog() {
        AlertDialog.Builder(this)
            .setTitle("PAUSE")
            .setPositiveButton("再開") { dialog, which ->
                // 再開ボタンがクリックされたときの処理
                mediaPlayer.start() // 音声を再開する
                startRecording() // 録音を再開する
            }
            .setNegativeButton("リトライ") { dialog, which ->
                // リトライボタンがクリックされたときの処理
                // ここにリトライ時の処理を記述する
                mediaPlayer.seekTo(0) // 音源を最初から再生する
                mediaPlayer.start() // 音源を再生する
                startRecording() // 録音を再開する
            }
            .show()
    }

    // Toastでメッセージを表示する関数
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        // アクティビティが破棄されるときにMediaPlayerとMediaRecorderを解放
        mediaPlayer.release()
        stopRecording()
        showToast("録音終了")
    }
}
