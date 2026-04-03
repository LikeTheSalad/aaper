package com.likethesalad.android.aaper.sample.instrumentation

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.likethesalad.android.aaper.api.EnsurePermissions

class InstrumentedPermissionActivity : AppCompatActivity() {

    companion object {
        var invocationCount = 0
            private set

        fun resetForTest() {
            invocationCount = 0
        }
    }

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    fun requestCameraPermission() {
        invocationCount++
    }
}
