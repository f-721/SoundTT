//package com.example.soundtt
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.media.*
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.soundtt.R
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.ByteArrayOutputStream
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
//class MainSound : AppCompatActivity() {
//    private lateinit var mediaPlayer: MediaPlayer
//    private var audioRecord: AudioRecord? = null
//    private var isRecording = false
//    private val bufferSize = AudioRecord.getMinBufferSize(
//        44100,
//        AudioFormat.CHANNEL_IN_MONO,
//        AudioFormat.ENCODING_PCM_16BIT
//    )
//    private val audioData = ByteArray(bufferSize)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.rhythmeazy)
//
//        // RECORD_AUDIOのパーミッションをリクエストする
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.RECORD_AUDIO),
//                1
//            )
//        } else {
//            start()
//        }
//
//        val btnPause: Button = findViewById(R.id.btnpause)
//        val btnBack: Button = findViewById(R.id.btnback)
//
//        btnPause.setOnClickListener {
//            showPauseDialog()
//        }
//
//        btnBack.setOnClickListener {
//            stop()
//            finish()
//        }
//    }
//
//    private fun start() {
//        // MediaPlayerで音源を再生
//        mediaPlayer = MediaPlayer.create(this, R.raw.rhythmrally1)
//        mediaPlayer.start()
//
//        // 録音を開始
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        audioRecord = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
//            44100,
//            AudioFormat.CHANNEL_IN_MONO,
//            AudioFormat.ENCODING_PCM_16BIT,
//            bufferSize
//        )
//        audioRecord?.startRecording()
//        isRecording = true
//
//        // マイクからの音声を録音し、音源の逆位相を合成して環境音を抽出
//        lifecycleScope.launch {
//            val outputStream = ByteArrayOutputStream()
//
//            while (isRecording) {
//                val bytesRead = withContext(Dispatchers.IO) {
//                    audioRecord?.read(audioData, 0, bufferSize) ?: 0
//                }
//                if (bytesRead > 0) {
//                    outputStream.write(audioData, 0, bytesRead)
//                    val audioBytes = outputStream.toByteArray()
//
//                    // 音源の逆位相を合成
//                    val inverseAudioBytes = createInverseAudioBytes(audioBytes)
//
//                    // 逆位相を合成したデータから環境音を抽出
//                    val environmentSound = extractEnvironmentSound(inverseAudioBytes)
//
//                    // 環境音の処理（ここではサンプルとしてログに出力）
//                    println("Detected environment sound: $environmentSound")
//                }
//            }
//            outputStream.close()
//        }
//    }
//
//    private fun createInverseAudioBytes(audioBytes: ByteArray): ByteArray {
//        val shorts = ShortArray(audioBytes.size / 2)
//        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
//        val inverseShorts = ShortArray(shorts.size)
//
//        // 音源の逆位相を合成
//        for (i in shorts.indices) {
//            inverseShorts[i] = (shorts[i] * -1).toShort()
//        }
//
//        val inverseBytes = ByteArray(audioBytes.size)
//        val buffer = ByteBuffer.wrap(inverseBytes)
//        for (s in inverseShorts) {
//            buffer.putShort(s)
//        }
//        return inverseBytes
//    }
//
//    private fun extractEnvironmentSound(audioBytes: ByteArray): ByteArray {
//        // 音源の音を除いた環境音を抽出
//        // ここで実装
//
//        // 今は音源そのものをそのまま返す
//        return audioBytes
//    }
//
//    private fun showPauseDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("PAUSE")
//            .setPositiveButton("再開") { dialog, which ->
//                mediaPlayer.start()
//                start()
//            }
//            .setNegativeButton("リトライ") { dialog, which ->
//                mediaPlayer.seekTo(0)
//                mediaPlayer.start()
//                stop()
//                start()
//            }
//            .show()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        stop()
//    }
//
//    private fun stop() {
//        isRecording = false
//        audioRecord?.let {
//            if (it.state == AudioRecord.STATE_INITIALIZED) {
//                it.stop()
//                it.release()
//            }
//        }
//        mediaPlayer.stop()
//        mediaPlayer.release()
//    }
//}
