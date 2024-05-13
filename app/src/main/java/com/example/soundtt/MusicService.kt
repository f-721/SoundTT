package com.example.soundtt

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class MusicService : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer // MediaPlayerオブジェクトを宣言
    private lateinit var btnStartEazy: Button
    private lateinit var btnPause: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.rhythmeazy, container, false)

        btnStartEazy = rootView.findViewById<Button>(R.id.btnHardStart)
        btnPause = rootView.findViewById<Button>(R.id.btnpause)

        // MediaPlayerオブジェクトを初期化
        mediaPlayer = MediaPlayer.create(context, R.raw.rhythmrally1)

        // btnEazyStartボタンのクリックリスナーを設定
        btnStartEazy.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.prepare()
            }
            mediaPlayer.start()
        }


        // btnPauseボタンのクリックリスナーを設定
        btnPause.setOnClickListener {
            // 音楽を一時停止する
            mediaPlayer.pause()
        }


        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        // アクティビティが破棄されるときにMediaPlayerを解放する
        mediaPlayer.release()
    }
}
