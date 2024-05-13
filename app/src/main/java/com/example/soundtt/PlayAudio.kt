package com.example.soundtt

import android.content.Context
import android.media.MediaPlayer

class PlayAudio{
    fun playAudio(fileName: String,context: Context) {
        val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        val mediaPlayer = MediaPlayer.create(context,resourceId)
        mediaPlayer.isLooping = false
        mediaPlayer.start()
        //Log.d("PlayAudio","ddd")
    }
}