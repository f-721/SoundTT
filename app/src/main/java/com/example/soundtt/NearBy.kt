package com.example.soundtt

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
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

class NearBy(private val context: Context) {
    var SERVICE_ID = "atuo.nearby"
    var nickname: String
    val TAG = "myapp"
    private var connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)
    //private var startSignalReceived = mutableSetOf<String>()
    //private var connectionsClient: ConnectionsClient
    private var isConnected: Boolean = false
    lateinit var endpointId: String

    init {
        connectionsClient = Nearby.getConnectionsClient(context)
        nickname = generateUniqueNickname(context)
    }

    private fun generateUniqueNickname(context: Context): String {
        //適当な名前決められるかなぁ
        return "atuo_${Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)}"
    }

    fun isEndpointIdInitialized() = ::endpointId.isInitialized

    fun initializeNearby() {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    //private lateinit var playAudio: PlayAudio

    // ID保存！
    private val connectedEndpoints = mutableListOf<String>()


//    fun sendTimeDiff(timeDiff: Long) {
//        if (::endpointId.isInitialized) {
//            val payload = Payload.fromBytes(timeDiff.toString().toByteArray())
//            Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
//        } else {
//            Log.d(TAG, "Endpoint ID is not initialized")
//        }
//    }

    fun sendHitTime(time: String) {
        if (::endpointId.isInitialized) {
            val payload = Payload.fromBytes(time.toByteArray())
            Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
        } else {
            Log.d(TAG, "ヒット時刻：EndpointIDが初期化されていません")
        }
    }

    fun sendId(id: String) {
        if (::endpointId.isInitialized) {
            val payload = Payload.fromBytes(id.toByteArray())
            Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
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

    fun advertise() {
        if (isConnected) {
            Log.d(TAG, "既に接続済みです")
            return
        }
        Log.d(TAG, "アドバタイズを開始します")
        connectionsClient.startAdvertising(
            nickname,
            SERVICE_ID,
            mConnectionLifecycleCallback,
            AdvertisingOptions(Strategy.P2P_STAR)
        ).addOnSuccessListener {
            Log.d(TAG, "アドバタイズが開始されました")
        }.addOnFailureListener { e ->
            Log.d(TAG, "アドバタイズを開始できませんでした: ${e.localizedMessage}")
        }
    }

    private var isDiscovering = false // ディスカバリーの状態を追跡するフラグ

    fun discovery() {
        if (isConnected) {
            Log.d(TAG, "既に接続済みです")
            return
        }
        if (isDiscovering) {
            Log.d(TAG, "既にディスカバリーが開始されています")
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
                    Log.d(TAG, "STATUS_ALREADY_DISCOVERING: 既にディスカバリーが実行中です")
                }
            }
    }

    fun resetEndpointId() {
        endpointId = null.toString()
    }

    private val mEndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
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
            Log.d(TAG, "他の端末からコネクションのリクエストを受け取った")
            this@NearBy.endpointId = endpointId
            Nearby.getConnectionsClient(context)
                .acceptConnection(endpointId, mPayloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.d(TAG, "コネクションリクエストの結果を受け取った時")
            this@NearBy.endpointId = endpointId // ここで初期化
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "コネクションが確立した。今後通信が可能。")
                    connectedEndpoints.add(endpointId)
                    Log.d(TAG, "通信成功")
                    Toast.makeText(context, "接続成功", Toast.LENGTH_SHORT).show()
                    isConnected = true
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
            // Convert hitTime to Long and process it as needed
            val hitTimeLong = hitTime.toLongOrNull()
            if (hitTimeLong != null) {
                // Process the hit time
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


    private fun processReceivedMessage(message: String) {
        Log.d(TAG, "Processing received message: $message")
    }
}
