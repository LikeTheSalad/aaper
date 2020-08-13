package com.likethesalad.android.aaper.defaults.statusproviders

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.common.truth.Truth
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
@PrepareForTest(ContextCompat::class)
class ActivityPermissionStatusProviderTest {

    private lateinit var host: Activity
    private lateinit var activityPermissionStatusProvider: ActivityPermissionStatusProvider

    @Before
    fun setUp() {
        host = mock()
        activityPermissionStatusProvider = ActivityPermissionStatusProvider()
    }

    @Test
    fun `Delegate query to ContextCompat`() {
        val permissionName = "somePermission"
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.checkSelfPermission(host, permissionName))
            .thenReturn(PackageManager.PERMISSION_GRANTED)

        val result = activityPermissionStatusProvider.isPermissionGranted(host, permissionName)

        Truth.assertThat(result).isTrue()
        PowerMockito.verifyStatic(ContextCompat::class.java)
        ContextCompat.checkSelfPermission(host, permissionName)
    }
}