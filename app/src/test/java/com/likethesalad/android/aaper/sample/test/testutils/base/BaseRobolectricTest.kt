package com.likethesalad.android.aaper.sample.test.testutils.base

import com.likethesalad.android.aaper.sample.test.testutils.TestApplication
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
abstract class BaseRobolectricTest