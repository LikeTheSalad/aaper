package com.likethesalad.android.aaper.sample.test

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.sample.test.utils.CallMe

class ActivityWithOverriddenResultMethod : AppCompatActivity() {
    var callOnResult: CallMe? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        callOnResult?.call()
    }

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    private fun someMethod(callMe: CallMe) {
        callMe.call()
    }

    fun testCaller(callMe: CallMe) {
        someMethod(callMe)
    }
}