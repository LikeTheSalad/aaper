package com.likethesalad.android.aaper.plugin.appender.visitor.data

data class ClassName(val packageName: String, val simpleName: String) {
    val internalName by lazy {
        if (packageName.isEmpty()) {
            simpleName
        } else {
            packageName.replace(".", "/") + "/" + simpleName
        }
    }
    val fullName by lazy {
        if (packageName.isEmpty()) {
            simpleName
        } else {
            "$packageName.$simpleName"
        }
    }
    val fileName by lazy {
        "$internalName.class"
    }
}