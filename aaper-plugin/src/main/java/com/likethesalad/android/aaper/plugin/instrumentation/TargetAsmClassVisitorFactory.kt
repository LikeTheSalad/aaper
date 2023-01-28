package com.likethesalad.android.aaper.plugin.instrumentation

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.likethesalad.android.aaper.plugin.instrumentation.visitor.AaperClassVisitor
import org.objectweb.asm.ClassVisitor

abstract class TargetAsmClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return AaperClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val superClasses = classData.superClasses
        return superClasses.contains("androidx.fragment.app.Fragment") ||
                superClasses.contains("android.app.Activity")
    }
}