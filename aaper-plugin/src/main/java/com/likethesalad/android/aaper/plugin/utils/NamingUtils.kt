package com.likethesalad.android.aaper.plugin.utils

object NamingUtils {

    fun getGeneratedClassSimpleName(methodClassSimpleName: String, methodName: String): String {
        return "Aaper_${methodClassSimpleName}_$methodName"
    }

    fun wrapMethodName(methodName: String): String {
        return "wraaper_${methodName}"
    }
}