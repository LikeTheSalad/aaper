package com.likethesalad.android.aaper.base.common.strategy

import android.app.Activity
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.activity.launcher.ActivityRequestLauncher
import com.likethesalad.android.aaper.base.activity.statusprovider.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.base.fragment.launcher.FragmentRequestLauncher
import com.likethesalad.android.aaper.base.fragment.statusprovider.FragmentPermissionStatusProvider
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 13/08/20.
 */

@ExtendWith(MockKExtension::class)
class AllRequestStrategyTest {

    @MockK
    lateinit var fragmentHost: Fragment

    @MockK
    lateinit var activityHost: Activity

    private lateinit var allRequestStrategy: AllRequestStrategy

    @BeforeEach
    fun setUp() {
        allRequestStrategy = object : AllRequestStrategy() {
            override fun onPermissionsRequestResults(
                host: Any,
                data: PermissionsResult
            ): Boolean {
                throw UnsupportedOperationException()
            }
        }
    }

    @Test
    fun `Verify request launcher type for fragment host`() {
        val result = allRequestStrategy.getRequestLauncher(fragmentHost)

        assertThat(result).isInstanceOf(FragmentRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type for fragment host`() {
        val result = allRequestStrategy.getPermissionStatusProvider(fragmentHost)

        assertThat(result).isInstanceOf(FragmentPermissionStatusProvider::class.java)
    }

    @Test
    fun `Verify request launcher type for activity host`() {
        val result = allRequestStrategy.getRequestLauncher(activityHost)

        assertThat(result).isInstanceOf(ActivityRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type for activity host`() {
        val result = allRequestStrategy.getPermissionStatusProvider(activityHost)

        assertThat(result).isInstanceOf(ActivityPermissionStatusProvider::class.java)
    }

    @Test
    fun `Verify request launcher exception for unknown host`() {
        assertThrows(UnsupportedOperationException::class.java) {
            allRequestStrategy.getRequestLauncher(mockk())
        }
    }

    @Test
    fun `Verify permission status provider exception for unknown host`() {
        assertThrows(UnsupportedOperationException::class.java) {
            allRequestStrategy.getPermissionStatusProvider(mockk())
        }
    }
}
