package com.example.soundtt

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.soundtt.ui.main.MainFragment

class RhythmNormal : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rhythmeazy)
        //テストだよーん
        val tvex: TextView = findViewById(R.id.tvex)
        val tvgreat: TextView = findViewById(R.id.tvgreat)
        val tvgood: TextView = findViewById(R.id.tvgood)
        val tvbad: TextView = findViewById(R.id.tvbad)
        val btnpause: Button = findViewById(R.id.btnpause)
        val logEazyStart: Button = findViewById(R.id.btnHardStart)
        val btnback: Button = findViewById(R.id.btnback)

        logEazyStart.setOnClickListener {
            // MediaPlayerのインスタンスを作成
            mediaPlayer = MediaPlayer.create(this, R.raw.rhythmrally1)
            // 音声を再生
            playSound()
        }
        btnpause.setOnClickListener {
            // ポーズダイアログを表示
            showPauseDialog()
        }

        btnback.setOnClickListener {
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


    // ポーズダイアログを表示する関数
    private fun showPauseDialog() {
        AlertDialog.Builder(this)
            .setTitle("PAUSE")
            .setPositiveButton("再開") { dialog, which ->
                // 再開ボタンがクリックされたときの処理
                mediaPlayer.start() // 音声を再開する
            }
            .setNegativeButton("リトライ") { dialog, which ->
                // リトライボタンがクリックされたときの処理
                // ここにリトライ時の処理を記述する
                mediaPlayer.seekTo(0) // 音源を最初から再生する
                mediaPlayer.start() // 音源を再生する
            }
            .show()
    }



    override fun onDestroy() {
        super.onDestroy()
        // アクティビティが破棄されるときにMediaPlayerを解放
        mediaPlayer.release()
    }
}
