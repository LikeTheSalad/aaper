package com.likethesalad.android.aaper.sample.test

import com.likethesalad.android.aaper.sample.test.testutils.base.BaseActivityTest
import org.junit.Test

class SimpleActivityTest : BaseActivityTest<SimpleActivity>() {

    @Test
    fun `Check permissions granted for simple method`() {
        run { activity ->
            val callMe = createCallMeMock()
            activity.callMe = callMe

            activity.simpleMethod()

            verifyCalled(callMe)
        }
    }

    @Test
    fun `Check permissions granted for method with single param`() {
        run { activity ->
            val callMe = createCallMeMock()

            activity.methodWithSingleParam(callMe)

            verifyCalled(callMe)
        }
    }

    @Test
    fun `Check permissions granted for method with multiple params`() {
        run { activity ->
            val callMe = createCallMeWithArgsMock<String>()

            activity.methodWithMultipleParams(5, "Something", callMe)

            verifyCalled(callMe, "First: 5, second: Something")
        }
    }

    override fun getActivityClass(): Class<SimpleActivity> {
        return SimpleActivity::class.java
    }
}