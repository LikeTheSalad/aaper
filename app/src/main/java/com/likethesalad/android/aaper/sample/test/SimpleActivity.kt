package com.likethesalad.android.aaper.sample.test

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.sample.test.utils.CallMe
import com.likethesalad.android.aaper.sample.test.utils.CallMeWithArg

class SimpleActivity : AppCompatActivity() {
    var callMe: CallMe? = null

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    fun simpleMethod() {
        callMe?.call()
    }

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION])
    fun methodWithSingleParam(callMe: CallMe) {
        callMe.call()
    }

    @EnsurePermissions(permissions = [Manifest.permission.ACCESS_NETWORK_STATE])
    fun methodWithMultipleParams(first: Int, second: String, callMeWithArg: CallMeWithArg<String>) {
        callMeWithArg.callMe("First: $first, second: $second")
    }

    @EnsurePermissions(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun methodWithLongAndDoubleParams(
        long: Long,
        double: Double,
        callMeWithArg: CallMeWithArg<String>
    ) {
        callMeWithArg.callMe("Sum result: ${long + double}")
    }

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    fun <T : Any> methodWithSignature(callMe: CallMeWithArg<T>, someObject: T) {
        callMe.callMe(someObject)
    }

    @EnsurePermissions(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun <T : Any, R> methodWithMultipleGenerics(
        callMe: CallMeWithArg<String>,
        someObject: T,
        someOther: R
    ) {
        callMe.callMe("First: $someObject, second: $someOther")
    }

    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])
    @kotlin.jvm.Throws(java.lang.IllegalArgumentException::class)
    fun someMethodThatThrowsException(callMe: CallMe) {
        callMe.call()
    }
}