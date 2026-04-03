package com.likethesalad.android.aaper.sample.test

import android.Manifest
import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.sample.test.testutils.base.BaseActivityTest
import com.likethesalad.android.aaper.sample.test.testutils.flow.PermissionFlowState
import com.likethesalad.android.aaper.sample.test.testutils.strategies.DeferredGrantingStrategy
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategy
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.robolectric.Shadows

class PermissionRequestFlowActivityTest : BaseActivityTest<SimpleActivity>() {

    @After
    fun tearDownFlowState() {
        PermissionFlowState.reset()
    }

    @Test
    fun `Check default strategy runs method immediately when permission is already granted`() {
        run { activity ->
            Aaper.setDefaultStrategy(DefaultRequestStrategy::class.java)
            val callMe = createCallMeMock()
            activity.callMe = callMe
            Shadows.shadowOf(activity).grantPermissions(Manifest.permission.CAMERA)

            activity.simpleMethod()

            verifyCalled(callMe)
            assertThat(Shadows.shadowOf(activity).lastRequestedPermission).isNull()
        }
    }

    @Test
    fun `Check default strategy waits for granted response before running original method`() {
        run { activity ->
            Aaper.setDefaultStrategy(DefaultRequestStrategy::class.java)
            val callMe = createCallMeMock()
            activity.callMe = callMe

            activity.simpleMethod()

            verify(exactly = 0) { callMe.call() }
            val lastRequestedPermission = Shadows.shadowOf(activity).lastRequestedPermission
            assertThat(lastRequestedPermission.requestedPermissions.asList())
                .containsExactly(Manifest.permission.CAMERA)
            assertThat(lastRequestedPermission.requestCode).isEqualTo(21293)

            Shadows.shadowOf(activity).grantPermissions(Manifest.permission.CAMERA)
            activity.onRequestPermissionsResult(
                21293,
                arrayOf(Manifest.permission.CAMERA),
                intArrayOf(0)
            )

            verifyCalled(callMe)
        }
    }

    @Test
    fun `Check default strategy does not run original method when permission remains denied`() {
        run { activity ->
            Aaper.setDefaultStrategy(DefaultRequestStrategy::class.java)
            val callMe = createCallMeMock()
            activity.callMe = callMe

            activity.simpleMethod()

            val lastRequestedPermission = Shadows.shadowOf(activity).lastRequestedPermission
            assertThat(lastRequestedPermission.requestedPermissions.asList())
                .containsExactly(Manifest.permission.CAMERA)

            activity.onRequestPermissionsResult(
                21293,
                arrayOf(Manifest.permission.CAMERA),
                intArrayOf(0)
            )

            verify(exactly = 0) { callMe.call() }
        }
    }

    @Test
    fun `Check deferred strategy does not launch until runner is triggered`() {
        run { activity ->
            Aaper.setDefaultStrategy(DeferredGrantingStrategy::class.java)
            val callMe = createCallMeMock()
            activity.callMe = callMe

            activity.simpleMethod()

            verify(exactly = 0) { callMe.call() }
            assertThat(PermissionFlowState.beforeLaunchCount).isEqualTo(1)
            assertThat(PermissionFlowState.launchCount).isZero()
            assertThat(PermissionFlowState.deferredRunner).isNotNull()

            PermissionFlowState.deferredRunner!!.run()

            verifyCalled(callMe)
            assertThat(PermissionFlowState.launchCount).isEqualTo(1)
            assertThat(PermissionFlowState.responseCount).isEqualTo(1)
            assertThat(PermissionFlowState.lastPermissions)
                .containsExactly(Manifest.permission.CAMERA)
        }
    }

    override fun getActivityClass(): Class<SimpleActivity> {
        return SimpleActivity::class.java
    }
}
