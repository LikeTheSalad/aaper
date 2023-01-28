package com.likethesalad.android.aaper.plugin.instrumentation.target

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.likethesalad.android.aaper.plugin.instrumentation.target.visitor.AaperTargetClassVisitor
import org.objectweb.asm.ClassVisitor

abstract class TargetAsmClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return AaperTargetClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val superClasses = classData.superClasses
        return superClasses.contains("androidx.fragment.app.Fragment") ||
                superClasses.contains("android.app.Activity")
    }
}