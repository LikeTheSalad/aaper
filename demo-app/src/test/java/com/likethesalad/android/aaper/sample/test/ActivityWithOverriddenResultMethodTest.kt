package com.likethesalad.android.aaper.sample.test

import android.Manifest
import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.sample.test.testutils.base.BaseActivityTest
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategy
import io.mockk.verify
import org.junit.Test
import org.robolectric.Shadows

class ActivityWithOverriddenResultMethodTest :
    BaseActivityTest<ActivityWithOverriddenResultMethod>() {

    @Test
    fun `Check original result and generated code are called`() {
        run { activity ->
            val callMe = createCallMeMock()
            val callOnResult = createCallMeMock()
            activity.callOnResult = callOnResult

            activity.testCaller(callMe)

            verifyCalled(callMe)
            verifyCalled(callOnResult)
        }
    }

    @Test
    fun `Check overridden result method is called even when permissions stay denied`() {
        run { activity ->
            Aaper.setDefaultStrategy(DefaultRequestStrategy::class.java)
            val callMe = createCallMeMock()
            val callOnResult = createCallMeMock()
            activity.callOnResult = callOnResult

            activity.testCaller(callMe)

            verify(exactly = 0) { callMe.call() }
            val lastRequestedPermission = Shadows.shadowOf(activity).lastRequestedPermission
            org.assertj.core.api.Assertions.assertThat(lastRequestedPermission.requestedPermissions.asList())
                .containsExactly(Manifest.permission.CAMERA)
            org.assertj.core.api.Assertions.assertThat(lastRequestedPermission.requestCode)
                .isEqualTo(21293)

            activity.onRequestPermissionsResult(
                21293,
                arrayOf(Manifest.permission.CAMERA),
                intArrayOf(0)
            )

            verify(exactly = 0) { callMe.call() }
            verifyCalled(callOnResult)
        }
    }

    override fun getActivityClass(): Class<ActivityWithOverriddenResultMethod> {
        return ActivityWithOverriddenResultMethod::class.java
    }
}
