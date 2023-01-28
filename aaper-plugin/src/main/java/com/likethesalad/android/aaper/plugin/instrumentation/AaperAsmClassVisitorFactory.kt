package com.likethesalad.android.aaper.plugin.instrumentation

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class AaperAsmClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        TODO("Not yet implemented")
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val superClasses = classData.superClasses
        return superClasses.contains("androidx.fragment.app.Fragment") ||
                superClasses.contains("android.app.Activity")
    }
}