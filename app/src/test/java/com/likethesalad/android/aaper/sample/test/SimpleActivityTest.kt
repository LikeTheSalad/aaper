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

    @Test
    fun `Check permissions granted for method with long and double params`() {
        run { activity ->
            val callMe = createCallMeWithArgsMock<String>()

            activity.methodWithLongAndDoubleParams(2, 5.0, callMe)

            verifyCalled(callMe, "Sum result: 7.0")
        }
    }

    override fun getActivityClass(): Class<SimpleActivity> {
        return SimpleActivity::class.java
    }
}