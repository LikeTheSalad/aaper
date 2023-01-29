package com.likethesalad.android.aaper.plugin.instrumentation.generated

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor.GeneratedClassVisitor
import org.objectweb.asm.ClassVisitor

abstract class GeneratedAsmClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return GeneratedClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.interfaces.contains("com.likethesalad.android.aaper.internal.compiler.AaperRunnable")
    }
}