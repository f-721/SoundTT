package com.example.soundtt

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class JudgeTimingFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JudgeTiming::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JudgeTiming(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
