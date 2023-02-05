package com.likethesalad.android.aaper

import android.content.Context
import androidx.startup.Initializer

class AaperInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    Aaper.init()
  }

  override fun dependencies(): List<Class<out Initializer<*>>> {
    return emptyList()
  }
}
