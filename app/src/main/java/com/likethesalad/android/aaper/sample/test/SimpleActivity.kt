package com.likethesalad.android.aaper.sample.test

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.sample.test.utils.CallMe

class SimpleActivity : AppCompatActivity() {
    var callMe: CallMe? = null

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    fun simpleMethod() {
        callMe?.call()
    }
}