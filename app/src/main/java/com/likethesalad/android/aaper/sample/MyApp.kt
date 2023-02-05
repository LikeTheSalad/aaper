package com.likethesalad.android.aaper.sample

import android.app.Application
import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider
import com.likethesalad.android.aaper.sample.custom.AlertDialogStrategy

/**
 * Created by César Muñoz on 03/08/20.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val strategyProvider = Aaper.getRequestStrategyProvider<DefaultRequestStrategyProvider>()
        strategyProvider.register(AlertDialogStrategy())
//        strategyProvider.register(FinishActivityOnDeniedStrategy())
//        strategyProvider.setDefaultStrategyName("FinishActivityOnDenied")
    }
}