package com.example.soundtt.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.soundtt.AudioEstimation
import com.example.soundtt.NearBy
import com.example.soundtt.PlayAudio
import com.example.soundtt.R
import com.example.soundtt.RhythmEazy
import com.example.soundtt.RhythmHard
import com.example.soundtt.RhythmNormal
import java.util.ArrayDeque

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var audioEstimation: AudioEstimation
    private lateinit var nearBy: NearBy
    private lateinit var playAudio: PlayAudio
    private lateinit var logHit: TextView
    private lateinit var logSwing: TextView
    //    private lateinit var logCount:TextView
    private lateinit var logDb:TextView
    private lateinit var logEazy:Button
    private lateinit var logNormal:Button
    private lateinit var logHard:Button
    private lateinit var logAdvertise:Button
    private lateinit var logDiscovery:Button
    private lateinit var logEditSwing: SeekBar
    private lateinit var logEditHit: SeekBar
    private lateinit var logEditAudio: SeekBar

    val maxSize = 5
    val queue = ArrayDeque<Int>(maxSize)
    //val accEstimation = AccEstimation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)


        logHit = rootView.findViewById<TextView>(R.id.textView_hit)
        logSwing = rootView.findViewById<TextView>(R.id.textView_swing)
//        logCount = rootView.findViewById<TextView>(R.id.textView_count)
//        logDb = rootView.findViewById<TextView>(R.id.textView_db)
        logEazy = rootView.findViewById<Button>(R.id.btnEazy)
        logNormal = rootView.findViewById<Button>(R.id.btnNormal)
        logHard = rootView.findViewById<Button>(R.id.btnHard)
        logHard = rootView.findViewById<Button>(R.id.btnHard)
        logAdvertise = rootView.findViewById<Button>(R.id.button_advertise)
        logDiscovery = rootView.findViewById<Button>(R.id.button_discovery)
        logEditAudio = rootView.findViewById<SeekBar>(R.id.SeekBar_Audio)
        logEditSwing = rootView.findViewById<SeekBar>(R.id.SeekBar_Swing)
        logEditHit = rootView.findViewById<SeekBar>(R.id.SeekBar_Hit)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // これ絶対最初
        viewModel.start(requireContext())


        audioEstimation = AudioEstimation()
        //EstimationのaccTestの値が変わった瞬間テキスト表示
        viewModel.accSensor.accEstimation.accTest.observe(viewLifecycleOwner){
            Log.d("MainFragment" ,it)
            logHit.text = it
        }

        // applicationのLiveDataを監視してヒットした瞬間
        viewModel.accSensor.accEstimation.isHit.observe(viewLifecycleOwner){
            Log.d("MainFragment", "hit? = $it")

        }

        viewModel.accSensor.accEstimation.isSwing.observe(viewLifecycleOwner){
            Log.d("MainFragment", "swing? = $it")

            if (it){
                //logSwing.text = "Swing"

                val hit = viewModel.accSensor.accEstimation._isHit.value
                Log.d("MainFragment","Fragment_Hit? = $hit")
                if (hit == true){

//                    if (viewModel.nearBy.count == 1){
//                        var volume = viewModel.audioSensor.getVolume()
//
//                        playAudio = PlayAudio()
//                        if (audioEstimation.dbEstimation(queue)) {
//                            logDb.text = volume.toString()
//                            playAudio.playAudio("issenn",requireContext())
//                            playAudio.playAudio("bgm",requireContext())
//                            Log.d("MainFragment","成功")
//                        }else{
//                            logDb.text = volume.toString()
//                            playAudio.playAudio("pahu",requireContext())
//                            playAudio.playAudio("sippai",requireContext())
//                            Log.d("MainFragment","失敗")
//                        }
//                    }


                    if (viewModel.nearBy.rally_flag == 0 && viewModel.nearBy.connectionflag == 1){
                        if (viewModel.nearBy.count != 0){
                            viewModel.nearBy.date_push()
                        }
                    }
//                    logCount.text = viewModel.nearBy.count.toString()
                    viewModel.accSensor.accEstimation._isSwing.postValue(false)
                    viewModel.accSensor.accEstimation._isHit.postValue(false)
                    viewModel.accSensor.accEstimation.bl_onhit = false
                    viewModel.accSensor.accEstimation.bl_onswing = false

                }

            }else{
                //logSwing.text = "No Swing"
            }

        }
        //常に音量を表示する用
        viewModel.audioSensor.isVolume.observe(viewLifecycleOwner){
            if (queue.size >= maxSize) {
                queue.poll()
            }
            queue.offer(it)
//            logCount.text = viewModel.nearBy.count.toString()
        }

        logAdvertise.setOnClickListener {
            viewModel.nearBy.advertise()
        }
        logDiscovery.setOnClickListener {
            viewModel.nearBy.discovery()
        }
        logEazy.setOnClickListener {
            // Eazyボタンがクリックされたときの処理をここに記述します
            val intent = Intent(context,RhythmEazy::class.java)
            startActivity(intent)
        }
        logNormal.setOnClickListener {
            // Normalボタンがクリックされたときの処理をここに記述します
            val intent = Intent(context,RhythmNormal::class.java)
            startActivity(intent)
        }
        logHard.setOnClickListener {
            // Hardボタンがクリックされたときの処理をここに記述します
            val intent = Intent(context,RhythmHard::class.java)
            startActivity(intent)
        }



        logEditHit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // スライドバーの値が変更された時の処理
                val selectedValue = progress*0.1
                // ここで選択された値を使用して他の処理を行うことができます
                viewModel.accSensor.accEstimation.hit = selectedValue
                Log.d("MainFragment","EditHit?= ${viewModel.accSensor.accEstimation.hit}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // スライドバーのタッチが開始された時の処理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // スライドバーのタッチが終了した時の処理
            }
        })
        logEditSwing.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // スライドバーの値が変更された時の処理
                val selectedValue = progress*0.1
                // ここで選択された値を使用して他の処理を行うことができます
                viewModel.accSensor.accEstimation.swing = selectedValue
                Log.d("MainFragment","EditSwing?= ${viewModel.accSensor.accEstimation.swing}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // スライドバーのタッチが開始された時の処理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // スライドバーのタッチが終了した時の処理
            }
        })
        logEditAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // スライドバーの値が変更された時の処理
                val selectedValue = progress
                // ここで選択された値を使用して他の処理を行うことができます
                audioEstimation.changeAudio(selectedValue)
                Log.d("MainFragment","EditAudio?= $selectedValue")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // スライドバーのタッチが開始された時の処理
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // スライドバーのタッチが終了した時の処理
            }
        })
    }

//    private fun Intent(mainFragment: MainFragment, java: Class<RhythmEazy>): Intent? {
//
//        return TODO("Provide the return value")
//    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.stop()

    }

}