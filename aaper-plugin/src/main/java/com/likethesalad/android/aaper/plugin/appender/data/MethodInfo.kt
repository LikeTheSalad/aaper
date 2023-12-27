package com.likethesalad.android.aaper.plugin.appender.data

import com.likethesalad.android.aaper.plugin.appender.visitor.data.ClassName

data class MethodInfo(val name: String, val descriptor: String, val className: ClassName)