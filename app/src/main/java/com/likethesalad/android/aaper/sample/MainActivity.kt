package com.likethesalad.android.aaper.sample

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.sample.custom.AlertDialogStrategy

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn).setOnClickListener {
            takePhoto()
        }

        findViewById<Button>(R.id.btn_alert_dialog).setOnClickListener {
            showToast()
        }

        addFragment()
    }

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    private fun takePhoto() {
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
    }

    @EnsurePermissions(
        permissions = [Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE],
        strategy = AlertDialogStrategy::class
    )
    private fun showToast() {
        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_SHORT).show()
    }

    private fun addFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, MyFragment())
            .commit()
    }
}
