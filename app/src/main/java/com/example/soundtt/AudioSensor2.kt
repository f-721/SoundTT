import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.soundtt.R
import java.io.File
import org.jtransforms.fft.DoubleFFT_1D

class MainActivity : AppCompatActivity() {
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RECORD_AUDIOのパーミッションをリクエストする
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val file = File(filesDir, "recorded_audio.wav")
        val audioData = ByteArray(bufferSize)

        audioRecord?.startRecording()
        isRecording = true

        Thread {
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack.play()

            while (isRecording) {
                audioRecord?.read(audioData, 0, bufferSize)
                audioTrack.write(audioData, 0, bufferSize)

                 fun performFourierTransform(audioData: ShortArray, sampleRate: Int): DoubleArray {
                    val fft = DoubleFFT_1D(audioData.size.toLong())
                    val transformedData = DoubleArray(audioData.size)
                    val fftBuffer = DoubleArray(audioData.size * 2) // 複素数データを格納するために2倍のサイズが必要

                    // 実部と虚部にデータを設定
                    for (i in audioData.indices) {
                        fftBuffer[2 * i] = audioData[i].toDouble()
                        fftBuffer[2 * i + 1] = 0.0
                    }

                    // FFTを実行
                    fft.realForward(fftBuffer)

                    // FFT結果の振幅を計算
                    for (i in transformedData.indices) {
                        val re = fftBuffer[2 * i]
                        val im = fftBuffer[2 * i + 1]
                        transformedData[i] = Math.sqrt(re * re + im * im) / audioData.size // 振幅の平均
                    }

                    return transformedData
                }

                // ここで音声処理を行い、環境音から一部の音を抽出する
                // 例えば、フーリエ変換や音声認識などを行う
                 fun processAudioData(audioData: ShortArray, sampleRate: Int) {
                    // フーリエ変換を行い、特定の周波数帯域の音声を抽出する
                    val transformedData = performFourierTransform(audioData, sampleRate)
                    val targetFrequency = 1000 // 抽出したい周波数（例：1000Hz）

                    // 特定の周波数帯域を抽出する
                    val index = (targetFrequency / (sampleRate.toDouble() / audioData.size)).toInt()
                    val extractedSound = transformedData[index]

                    // ここで抽出した音声を再生したり、別の処理を行ったりする
                }


                // 環境音の一部の音を抽出したデータを再生する場合は、AudioTrackに書き込む
            }

            audioTrack.stop()
            audioTrack.release()
        }.start()
    }

    override fun onStop() {
        super.onStop()
        stopRecording()
    }

    private fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
    }
}
