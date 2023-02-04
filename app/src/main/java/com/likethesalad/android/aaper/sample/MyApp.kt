package com.likethesalad.android.aaper.sample

import android.app.Application

/**
 * Created by César Muñoz on 03/08/20.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
//        Aaper.init()

//        val strategyProvider = Aaper.getRequestStrategyProvider() as DefaultRequestStrategyProvider
//        strategyProvider.register(AlertDialogStrategy())
//        strategyProvider.register(FinishActivityOnDeniedStrategy())
//        strategyProvider.setDefaultStrategyName("FinishActivityOnDenied")
    }
}