package com.likethesalad.android.aaper.defaults.launchers

import androidx.fragment.app.Fragment
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyArray
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class FragmentRequestLauncherTest {

    @Mock
    lateinit var host: Fragment

    private val requestCode = 12345
    private lateinit var fragmentRequestLauncher: FragmentRequestLauncher

    @Before
    fun setUp() {
        fragmentRequestLauncher = FragmentRequestLauncher()
    }

    @Test
    fun `Delegate request to ActivityCompat`() {
        val permissions = listOf("one", "two")
        doNothing().whenever(host).requestPermissions(anyArray(), any())

        fragmentRequestLauncher.launchPermissionsRequest(host, permissions, requestCode)

        verify(host).requestPermissions(permissions.toTypedArray(), requestCode)
    }
}