package com.likethesalad.android.aaper.sample.test

import android.Manifest
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.sample.test.utils.CallMe

class SimpleFragment : Fragment() {

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    fun someMethod(callMe: CallMe) {
        callMe.call()
    }
}