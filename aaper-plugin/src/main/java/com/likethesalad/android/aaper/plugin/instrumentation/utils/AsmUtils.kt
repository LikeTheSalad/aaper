package com.likethesalad.android.aaper.plugin.instrumentation.utils

import org.objectweb.asm.Type

object AsmUtils {

    fun getCombinedSize(types: List<Type>): Int {
        var size = 0

        types.forEach {
            size += it.size
        }

        return size
    }
}