package com.likethesalad.android.aaper.sample

import android.app.Application
import com.likethesalad.android.aaper.Aaper

/**
 * Created by César Muñoz on 03/08/20.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Aaper.init()
    }
}