package com.likethesalad.android.aaper.sample.test

import com.likethesalad.android.aaper.sample.test.testutils.base.BaseActivityTest
import org.junit.Test

class JavaActivityTest : BaseActivityTest<JavaActivity>() {

    @Test
    fun `Check it instruments java classes`() {
        run { activity ->
            val callMe = createCallMeMock()

            activity.someMethod(callMe)

            verifyCalled(callMe)
        }
    }

    override fun getActivityClass(): Class<JavaActivity> {
        return JavaActivity::class.java
    }
}