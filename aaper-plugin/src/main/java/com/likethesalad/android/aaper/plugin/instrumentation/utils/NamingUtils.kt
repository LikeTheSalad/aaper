package com.likethesalad.android.aaper.plugin.instrumentation.utils

object NamingUtils {

    fun wrapMethodName(methodName: String): String {
        return "wraaper_${methodName}"
    }
}