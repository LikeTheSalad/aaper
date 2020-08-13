package com.likethesalad.android.aaper.defaults.statusproviders

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
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
@PrepareForTest(Fragment::class, ContextCompat::class)
class FragmentPermissionStatusProviderTest {

    private lateinit var host: Fragment
    private lateinit var fragmentPermissionStatusProvider: FragmentPermissionStatusProvider

    @Before
    fun setUp() {
        host = PowerMockito.mock(Fragment::class.java)
        fragmentPermissionStatusProvider = FragmentPermissionStatusProvider()
    }

    @Test
    fun `Delegate query to ContextCompat`() {
        val permissionName = "somePermission"
        val context = mock<Context>()
        doReturn(context).whenever(host).requireContext()
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.checkSelfPermission(context, permissionName))
            .thenReturn(PackageManager.PERMISSION_GRANTED)

        val result = fragmentPermissionStatusProvider.isPermissionGranted(host, permissionName)

        Truth.assertThat(result).isTrue()
        PowerMockito.verifyStatic(ContextCompat::class.java)
        ContextCompat.checkSelfPermission(context, permissionName)
    }
}