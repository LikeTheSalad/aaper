package com.likethesalad.android.aaper.sample.test

import android.Manifest
import androidx.fragment.app.testing.FragmentScenario
import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.sample.test.testutils.base.BaseRobolectricTest
import com.likethesalad.android.aaper.sample.test.testutils.flow.PermissionFlowState
import com.likethesalad.android.aaper.sample.test.testutils.strategies.ImmediateDenyingStrategy
import com.likethesalad.android.aaper.sample.test.utils.CallMe
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test

class PermissionRequestFlowFragmentTest : BaseRobolectricTest() {

    @After
    fun tearDownFlowState() {
        PermissionFlowState.reset()
    }

    @Test
    fun `Check denied fragment request does not execute original method`() {
        FragmentScenario.launch(SimpleFragment::class.java).use {
            it.onFragment { fragment ->
                Aaper.setDefaultStrategy(ImmediateDenyingStrategy::class.java)
                val callMe = mockk<CallMe>(relaxUnitFun = true)

                fragment.someMethod(callMe)

                verify(exactly = 0) { callMe.call() }
                assertThat(PermissionFlowState.launchCount).isEqualTo(1)
                assertThat(PermissionFlowState.responseCount).isEqualTo(1)
                assertThat(PermissionFlowState.lastPermissions)
                    .containsExactly(Manifest.permission.CAMERA)
            }
        }
    }
}
