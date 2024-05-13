package com.example.soundtt

import java.util.Queue

var volume = 330

var sound = 0

class AudioEstimation() {

    fun changeAudio(changevolume:Int){
        volume = changevolume
    }

    fun dbEstimation(queue:Queue<Int>):Boolean{
        var sum = 0
        for (element in queue) {
            sum += element
        }

        if (sum >= volume){
            return true
        }else {
            return false
        }
    }
}