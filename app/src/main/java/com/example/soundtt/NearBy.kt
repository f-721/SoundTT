package com.example.soundtt

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.Strategy
import com.google.android.gms.nearby.connection.*

class NearBy(private val context: Context) {
    //NearByShare用の変数
    var SERVICE_ID = "atuo.nearby"
    var nickname = "atuo"
    var mRemoteEndpointId:String? = ""
    val TAG = "myapp"
    var rally_flag = 0

    var count = 5
    var startflag = 0
    var connectionflag = 0

    private lateinit var playAudio: PlayAudio

    fun advertise() {
        Log.d(TAG,"advertiseをタップ")
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                nickname,
                SERVICE_ID,
                mConnectionLifecycleCallback,
                AdvertisingOptions(Strategy.P2P_STAR)
            )
            .addOnSuccessListener {
                // Advertise開始した
                Log.d(TAG,"Advertise開始した")
            }
            .addOnFailureListener {
                // Advertiseできなかった
                Log.d(TAG,"Advertiseできなかった")

            }
    }

    fun discovery(){
        Log.d(TAG,"Discoveryをタップ")

        Nearby.getConnectionsClient(context)
            .startDiscovery(
                SERVICE_ID,
                mEndpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_STAR)
            )
            .addOnSuccessListener {
                // Discovery開始した
                Log.d(TAG,"Discovery開始した")
            }
            .addOnFailureListener {
                // Discovery開始できなかった
                Log.d(TAG,"Discovery開始できなかった")
            }
    }

    private val mEndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
            // Advertise側を発見した
            Log.d(TAG,"Advertise側を発見した")

            // とりあえず問答無用でコネクション要求してみる
            Nearby.getConnectionsClient(context)
                .requestConnection(nickname, endpointId, mConnectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {
            // 見つけたエンドポイントを見失った
            Log.d(TAG,"見つけたエンドポイントを見失った")
        }
    }
    private val mConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            // 他の端末からコネクションのリクエストを受け取った時
            Log.d(TAG,"他の端末からコネクションのリクエストを受け取った")

            // とりあえず来る者は拒まず即承認
            Nearby.getConnectionsClient(context)
                .acceptConnection(endpointId, mPayloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {

            // コネクションリクエストの結果を受け取った時
            Log.d(TAG,"コネクションリクエストの結果を受け取った時")

            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    // コネクションが確立した。今後通信が可能。
                    Log.d(TAG,"コネクションが確立した。今後通信が可能。")
                    // 通信時にはendpointIdが必要になるので、フィールドに保持する。
                    mRemoteEndpointId = endpointId

                    Log.d(TAG,"通信成功")

                    connectionflag = 1
                }

                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    // コネクションが拒否された時。通信はできない。
                    Log.d(TAG,"コネクションが拒否された時。通信はできない。")
                    mRemoteEndpointId = null
                }

                ConnectionsStatusCodes.STATUS_ERROR -> {
                    // エラーでコネクションが確立できない時。通信はできない。
                    Log.d(TAG,"エラーでコネクションが確立できない時。通信はできない。")
                    mRemoteEndpointId = null
                }
            }
        }

        // コネクションが切断された時
        override fun onDisconnected(endpointId: String) {
            Log.d(TAG,"コネクションが切断された")
            mRemoteEndpointId = null
        }
    }

    private val mPayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            when (payload.type) {
                Payload.Type.BYTES -> {
                    // バイト配列を受け取った時
                    val data = payload.asBytes()!!
                    val countString= String(data)
                    count = countString.toInt()
                    rally_flag = 0
                    startflag = 1
                    Log.d(TAG,data.toString())


                    Log.d(TAG,"バイト配列を受け取った")
                    // 処理
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // 転送状態が更新された時詳細は省略
        }
    }

    fun date_push(){
        Log.d(TAG,"date_pushをタップ")

        if (startflag == 1){
            count = count-1
        }else{
            startflag = 1
        }

        if (count != 0){
            playAudio = PlayAudio()
            playAudio.playAudio("count"+count.toString(),context)
        }

        val data = count.toString().toByteArray()
        val payload = Payload.fromBytes(data)

        Nearby.getConnectionsClient(context)
            .sendPayload(mRemoteEndpointId.toString(), payload)
        Log.d(TAG,"データを送った")

        rally_flag = 1
    }


}