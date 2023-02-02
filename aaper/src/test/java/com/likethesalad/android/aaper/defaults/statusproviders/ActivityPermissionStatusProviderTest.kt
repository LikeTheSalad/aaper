package com.likethesalad.android.aaper.defaults.statusproviders

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.testutils.BaseRobolectricTest
import com.likethesalad.android.aaper.testutils.RobolectricActivity
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.Shadows

/**
 * Created by César Muñoz on 13/08/20.
 */

class ActivityPermissionStatusProviderTest : BaseRobolectricTest() {

    private lateinit var activityPermissionStatusProvider: ActivityPermissionStatusProvider

    @Before
    fun setUp() {
        activityPermissionStatusProvider = ActivityPermissionStatusProvider()
    }

    @Test
    fun `Return permission grant status`() {
        val permissionName = "somePermission"
        Robolectric.buildActivity(RobolectricActivity::class.java).use { controller ->
            controller.setup()
            val activity = controller.get()

            Shadows.shadowOf(activity).grantPermissions(permissionName)

            val result =
                activityPermissionStatusProvider.isPermissionGranted(activity, permissionName)

            Truth.assertThat(result).isTrue()
        }
    }
}