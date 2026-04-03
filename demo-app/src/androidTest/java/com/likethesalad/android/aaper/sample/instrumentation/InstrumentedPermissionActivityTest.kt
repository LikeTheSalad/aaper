package com.likethesalad.android.aaper.sample.instrumentation

import android.Manifest
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentedPermissionActivityTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    private lateinit var activity: InstrumentedPermissionActivity

    @Before
    fun setUp() {
        InstrumentedPermissionActivity.resetForTest()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val intent = Intent(
            instrumentation.targetContext,
            InstrumentedPermissionActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        activity = instrumentation.startActivitySync(intent) as InstrumentedPermissionActivity
        instrumentation.waitForIdleSync()
    }

    @After
    fun tearDown() {
        activity.finish()
        InstrumentedPermissionActivity.resetForTest()
    }

    @Test
    fun transformedAnnotatedMethodRunsOnDeviceWhenPermissionIsGranted() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            activity.requestCameraPermission()
        }

        assertEquals(1, InstrumentedPermissionActivity.invocationCount)
    }
}
