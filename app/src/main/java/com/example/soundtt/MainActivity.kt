package com.example.soundtt

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.soundtt.ui.main.MainFragment
import pub.devrel.easypermissions.EasyPermissions
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace<MainFragment>(R.id.container)
            }
        }

        // todo 権限
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO ,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )

        //許可したいpermissionを許可できるように
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", 1, *permissions)
        }

    }
}