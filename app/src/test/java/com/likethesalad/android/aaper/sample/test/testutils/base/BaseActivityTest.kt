package com.likethesalad.android.aaper.sample.test.testutils.base

import android.app.Activity
import com.likethesalad.android.aaper.sample.test.utils.CallMe
import io.mockk.mockk
import io.mockk.verify
import org.robolectric.Robolectric

abstract class BaseActivityTest<T : Activity> : BaseRobolectricTest() {

    abstract fun getActivityClass(): Class<T>

    protected fun createCallMeMock(): CallMe {
        return mockk(relaxUnitFun = true)
    }

    protected fun verifyCalled(callMe: CallMe) {
        verify {
            callMe.call()
        }
    }


    protected fun run(function: (T) -> Unit) {
        Robolectric.buildActivity(getActivityClass()).use {
            it.setup()
            function.invoke(it.get())
        }
    }
}