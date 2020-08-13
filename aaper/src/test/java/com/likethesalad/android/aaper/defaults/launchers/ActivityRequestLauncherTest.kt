package com.likethesalad.android.aaper.defaults.launchers

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(PowerMockRunner::class)
@PrepareForTest(ActivityCompat::class)
class ActivityRequestLauncherTest {


    private val requestCode = 12345
    private lateinit var host: Activity
    private lateinit var activityRequestLauncher: ActivityRequestLauncher

    @Before
    fun setUp() {
        host = mock()
        activityRequestLauncher = ActivityRequestLauncher()
    }

    @Test
    fun `Delegate request to ActivityCompat`() {
        PowerMockito.mockStatic(ActivityCompat::class.java)
        val permissions = listOf("one", "two")

        activityRequestLauncher.launchPermissionsRequest(host, permissions, requestCode)

        PowerMockito.verifyStatic(ActivityCompat::class.java)
        ActivityCompat.requestPermissions(host, permissions.toTypedArray(), requestCode)
    }
}