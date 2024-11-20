package com.example.soundtt

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload

class RhythmEasy : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 1001
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.NEARBY_WIFI_DEVICES,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var tvgreat: TextView
    private lateinit var accSensor: AccSensor
    private lateinit var accEstimation: AccEstimation
    private lateinit var judgeTiming: JudgeTiming
    private lateinit var nearBy: NearBy
    private lateinit var textconnect: TextView

    private lateinit var connectionsClient: ConnectionsClient
    private var startSignalSent = false // Flag to check if the start signal has been sent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rhythmeasy)


    // Initialize
        tvgreat = findViewById(R.id.tvgreat)
        nearBy = NearBy(this, this)
        accEstimation = AccEstimation(nearBy)
        judgeTiming = JudgeTiming(accEstimation, tvgreat, nearBy)
        accSensor = AccSensor(this, tvgreat, accEstimation, nearBy, judgeTiming)

        connectionsClient = Nearby.getConnectionsClient(this)
        textconnect = findViewById(R.id.textconnect) // ここで lateinit プロパティとして初期化

        textconnect.visibility = View.GONE // 既存のローカル変数宣言を削除

        if (allPermissionsGranted()) {
            initializeNearbyFunctionality()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.rhythmrally1)

//        val btnpause: Button = findViewById(R.id.btnpause)
        val logstart: Button = findViewById(R.id.btnstart)
        val logback: Button = findViewById(R.id.btnback)
//        val btnadvertise: Button = findViewById(R.id.btn_advertise)
        val btndiscovery: Button = findViewById(R.id.btn_discovery)
        val btndisconnect: Button = findViewById(R.id.btndisconnect)
        val textconnect: TextView = findViewById(R.id.textconnect)
            textconnect.visibility = View.GONE

        // デバイスIDを取得
        val deviceId = nearBy.generateUniqueNickname(this)

        // テキストビューにデバイスIDを設定
        val idTextView: TextView = findViewById(R.id.text_id)
        idTextView.text = deviceId

        logstart.setOnClickListener {
            if (!startSignalSent) {
                start(this)
                showToast("開始の信号を送信")
                sendStartSignal()
                startSignalSent = true // Update the flag after sending the signal
            } else {
                showToast("既に開始の信号を送信しています")
                Log.d(TAG, "開始の信号を既に送信済みです")
            }
        }

//        btnpause.setOnClickListener {
//            showPauseDialog()
//        }

        logback.setOnClickListener {
            stop()
            finish()
        }

//        btnadvertise.setOnClickListener {
//            nearBy.advertise()
//        }

        btndiscovery.setOnClickListener {
            nearBy.discovery()
        }

        btndisconnect.setOnClickListener {
            disconnect()
        }
    }

    private fun onConnectionEstablished() {
        textconnect.visibility = View.VISIBLE
        showToast("接続が確立されました")
    }

    // 接続成功時にtextconnectを表示するメソッド
    fun showConnectionText() {
        if (::textconnect.isInitialized) {
            textconnect.visibility = View.VISIBLE
            Toast.makeText(this, "接続が確立されました", Toast.LENGTH_SHORT).show()
        } else {
            Log.e(TAG, "textconnect が初期化されていません")
        }
    }


    private fun initializeNearbyFunctionality() {
        nearBy.initializeNearby()
    }

    private fun sendStartSignal() {
        if (nearBy.isEndpointIdInitialized()) {
            val payload = Payload.fromBytes("start".toByteArray())
            connectionsClient.sendPayload(nearBy.endpointId, payload)
            Log.d(TAG, "スタート信号を送信しました")
        } else {
            Log.d(TAG, "スタート信号を送信できませんでした")
        }
    }

    private fun disconnect() {
        if (nearBy.isEndpointIdInitialized()) {
            connectionsClient.disconnectFromEndpoint(nearBy.endpointId)
            showToast("接続を切断しました")
            resetStartSignalSent()
            Log.d(TAG, "接続を切断しました")
            textconnect.visibility = View.GONE
            nearBy.resetEndpointId() // Reset endpoint ID after disconnecting
            restartNearby() // Restart discovery or advertising
        } else {
            showToast("切断する接続がありません")
            Log.d(TAG, "切断する接続がありません")
        }
    }

    fun resetStartSignalSent() {
        startSignalSent = false
        Log.d(TAG, "startSignalSentがfalseにリセットされました")
    }

    private fun restartNearby() {
        // Example of restarting discovery
        nearBy.discovery()
        // If you want to advertise instead, use:
        // nearBy.advertise()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initializeNearbyFunctionality()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
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

//    private fun showPauseDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("PAUSE")
//            .setPositiveButton("再開") { dialog, which ->
//                mediaPlayer.start()
//            }
//            .setNegativeButton("リトライ") { dialog, which ->
//                mediaPlayer.seekTo(0)
//                mediaPlayer.start()
//            }
//            .show()
//    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun start(context: Context) {
        accSensor.start()
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
