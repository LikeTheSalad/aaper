package com.likethesalad.android.aaper.sample.test

import androidx.fragment.app.testing.FragmentScenario
import com.likethesalad.android.aaper.sample.test.testutils.base.BaseRobolectricTest
import com.likethesalad.android.aaper.sample.test.utils.CallMe
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class SimpleFragmentTest : BaseRobolectricTest() {

    @Test
    fun `Check permission granted for fragment method`() {
        FragmentScenario.launch(SimpleFragment::class.java).use {
            it.onFragment { fragment ->
                val callMe = mockk<CallMe>(relaxUnitFun = true)

                fragment.someMethod(callMe)

                verify {
                    callMe.call()
                }
            }
        }
    }
}