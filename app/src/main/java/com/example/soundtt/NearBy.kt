package com.example.soundtt

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy

class NearBy(private val context: Context, private val activity: RhythmEasy) {
    var SERVICE_ID = "atuo.nearby"
    var nickname: String
    val TAG = "myapp"
    private var connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)
    private var isConnected: Boolean = false
    lateinit var endpointId: String
    private var isDiscovering = false // Discovery status flag

    init {
        connectionsClient = Nearby.getConnectionsClient(context)
        nickname = generateUniqueNickname(context)
    }

    fun generateUniqueNickname(context: Context): String {
        return "atuo_${Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)}"
    }

    fun isEndpointIdInitialized() = ::endpointId.isInitialized

    fun initializeNearby() {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    // ID保存！
    private val connectedEndpoints = mutableListOf<String>()

    private var timeOffset: Long = 0 // オフセット値を保持する

    fun sendHitTime(time: String) {
        if (::endpointId.isInitialized) {
            // 送信前にクライアントのオフセットを適用して時刻を補正
            val correctedTime = (time.toLong() + timeOffset).toString()
            val payload = Payload.fromBytes("TIME:$correctedTime".toByteArray())
            Log.d(TAG, "送信する補正されたヒット時刻: $correctedTime")
            Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
                .addOnSuccessListener {
                    Log.d(TAG, "ヒット時刻が正常に送信されました: $correctedTime")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ヒット時刻の送信に失敗しました: ${e.message}")
                }
        } else {
            Log.d(TAG, "ヒット時刻：EndpointIDが初期化されていません")
        }
    }



    fun sendId(id: String) {
        if (::endpointId.isInitialized) {
            val payload = Payload.fromBytes("ID:$id".toByteArray())
            Log.d(TAG, "送信するID: $id")
            Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
                .addOnSuccessListener {
                    Log.d(TAG, "IDが正常に送信されました: $id")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "IDの送信に失敗しました: ${e.message}")
                }
        } else {
            Log.d(TAG, "スマホID：EndpointIDが初期化されていません ")
        }
    }


    fun sendPayload(payload: Payload) {
        if (::endpointId.isInitialized) {
            connectionsClient.sendPayload(endpointId, payload)
                .addOnSuccessListener {
                    Log.d(TAG, "ペイロードが正常に送信されました。")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ペイロードの送信に失敗しました: ${e.message}")
                }
        } else {
            Log.d(TAG, "エンドポイントIDが初期化されていませんよおおおおおお")
        }
    }

//    fun advertise() {
//        if (isConnected) {
//            Log.d(TAG, "既に接続済みです")
//            return
//        }
//        Log.d(TAG, "advertiseを開始します")
//        connectionsClient.startAdvertising(
//            nickname,
//            SERVICE_ID,
//            mConnectionLifecycleCallback,
//            AdvertisingOptions(Strategy.P2P_STAR)
//        ).addOnSuccessListener {
//            Log.d(TAG, "advertiseが開始されました")
//        }.addOnFailureListener { e ->
//            Log.d(TAG, "advertiseを開始できませんでした: ${e.localizedMessage}")
//        }
//    }

    fun discovery() {
        if (isConnected) {
            Log.d(TAG, "既に接続済みです")
            return
        }
        if (isDiscovering) {
            Log.d(TAG, "既にDiscoveryが開始されています")
            return
        }

        Log.d(TAG, "Discoveryをタップ")
        Nearby.getConnectionsClient(context)
            .startDiscovery(
                SERVICE_ID,
                mEndpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_STAR)
            )
            .addOnSuccessListener {
                Log.d(TAG, "Discovery開始した")
                isDiscovering = true // 成功時にフラグを更新
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Discovery開始できなかった: ${exception.message}")
                if (exception is ApiException && exception.statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
                    Log.d(TAG, "STATUS_ALREADY_DISCOVERING: 既にDiscoveryが実行中です")
                }
            }
    }

    fun resetEndpointId() {
        endpointId = null.toString()
    }

    private val mEndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
            if (isConnected) {
                Log.d(TAG, "既に接続済みのため新しいエンドポイントを無視します")
                return
            }
            Log.d(TAG, "Advertise側を発見した")
            Nearby.getConnectionsClient(context)
                .requestConnection(nickname, endpointId, mConnectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "見つけたエンドポイントを見失った")
        }
    }

    private val mConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            if (isConnected) {
                Log.d(TAG, "既に接続が確立されているため、新しいコネクションを拒否します")
                Nearby.getConnectionsClient(context).rejectConnection(endpointId)
                return
            }
            Log.d(TAG, "他の端末からコネクションのリクエストを受け取った")
            this@NearBy.endpointId = endpointId
            Nearby.getConnectionsClient(context)
                .acceptConnection(endpointId, mPayloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.d(TAG, "コネクションリクエストの結果を受け取った時")
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "コネクションが確立した。今後通信が可能。")
                    connectedEndpoints.add(endpointId)
                    Log.d(TAG, "通信成功")
                    Toast.makeText(context, "接続成功", Toast.LENGTH_SHORT).show()
                    isConnected = true

                    // RhythmEasyのメソッドを呼び出してtextconnectを表示
                    activity.showConnectionText()

                    // Stop discovery and advertising when connected
                    stopDiscoveryAndAdvertising()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(TAG, "コネクションが拒否された時。通信はできない。")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d(TAG, "エラーでコネクションが確立できない時。通信はできない。")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "コネクションが切断された")
            connectedEndpoints.remove(endpointId)
            isConnected = false
            // フラグをリセット
            (context as? RhythmEasy)?.resetStartSignalSent()
        }
    }

    private val mPayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            when (payload.type) {
                Payload.Type.BYTES -> {
                    val data = payload.asBytes() ?: return
                    val message = String(data)
                    Log.d(TAG, "Received message: $message")

                    // Determine type of message by prefix
                    when {
                        message.startsWith("TIME:") -> {
                            val hitTime = message.removePrefix("TIME:")
                            handleHitTime(hitTime)
                        }
                        message.startsWith("ID:") -> {
                            val id = message.removePrefix("ID:")
                            handleId(id)
                        }
                        else -> {
                            Log.d(TAG, "Unknown message format: $message")
                        }
                    }
                }
            }
        }

        private fun handleHitTime(hitTime: String) {
            Log.d(TAG, "Received hit time: $hitTime")
            val hitTimeLong = hitTime.toLongOrNull()
            if (hitTimeLong != null) {
                val currentTime = System.currentTimeMillis()
                // ホストとの時間差を計算してオフセットに保存
                timeOffset = currentTime - hitTimeLong
                Log.d(TAG, "Calculated time offset: $timeOffset")
            } else {
                Log.d(TAG, "Invalid hit time format: $hitTime")
            }
        }

        private fun handleId(id: String) {
            Log.d(TAG, "Received ID: $id")
            // Process the ID
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Handle payload transfer updates if needed
        }
    }

    private fun stopDiscoveryAndAdvertising() {
        // Stop discovery
        if (isDiscovering) {
            connectionsClient.stopDiscovery()
            isDiscovering = false
            Log.d(TAG, "Discoveryを停止しました")
        }

        // Stop advertising
        connectionsClient.stopAdvertising()
        Log.d(TAG, "アドバタイズを停止しました")
    }

    private fun processReceivedMessage(message: String) {
        Log.d(TAG, "Processing received message: $message")
    }
}
