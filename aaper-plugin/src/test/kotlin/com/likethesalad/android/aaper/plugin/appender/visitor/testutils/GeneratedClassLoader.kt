package com.likethesalad.android.aaper.plugin.appender.visitor.testutils

class GeneratedClassLoader(parent: ClassLoader?) : ClassLoader(parent) {

    fun defineClass(name: String, bytes: ByteArray): Class<*> {
        return defineClass(name, bytes, 0, bytes.size)
    }
}