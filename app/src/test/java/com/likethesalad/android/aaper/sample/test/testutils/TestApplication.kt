package com.likethesalad.android.aaper.sample.test.testutils

import android.app.Application
import com.likethesalad.android.aaper.Aaper

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Aaper.initialize(this)
    }
}