package com.likethesalad.android.aaper.sample.test

import com.likethesalad.android.aaper.sample.test.testutils.BaseRobolectricTest
import com.likethesalad.android.aaper.sample.test.utils.CallMe
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.robolectric.Robolectric

class SimpleActivityTest : BaseRobolectricTest() {

    @Test
    fun `Check permissions granted for simple method`() {
        Robolectric.buildActivity(SimpleActivity::class.java).use {
            it.setup()
            val activity = it.get()

            val callMe = mockk<CallMe>()

            activity.simpleMethod(callMe)

            verify {
                callMe.call()
            }
        }
    }
}