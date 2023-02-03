package com.likethesalad.android.aaper.sample.test

import com.likethesalad.android.aaper.sample.test.testutils.base.BaseActivityTest
import org.junit.Test

class ActivityWithOverriddenResultMethodTest :
    BaseActivityTest<ActivityWithOverriddenResultMethod>() {

    @Test
    fun `Check original result and generated code are called`() {
        run { activity ->
            val callMe = createCallMeMock()
            val callOnResult = createCallMeMock()
            activity.callOnResult = callOnResult

            activity.testCaller(callMe)

            verifyCalled(callMe)
            verifyCalled(callOnResult)
        }
    }

    override fun getActivityClass(): Class<ActivityWithOverriddenResultMethod> {
        return ActivityWithOverriddenResultMethod::class.java
    }
}