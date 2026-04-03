package com.likethesalad.android.aaper.sample.test.testutils.base

import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.sample.test.testutils.TestApplication
import org.junit.After
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
abstract class BaseRobolectricTest {
    @After
    fun baseTearDown() {
        PermissionManager.resetForTest()
        Aaper.resetForTest()
    }
}