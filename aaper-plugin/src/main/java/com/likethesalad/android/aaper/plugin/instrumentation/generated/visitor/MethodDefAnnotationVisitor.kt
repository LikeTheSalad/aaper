package com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class MethodDefAnnotationVisitor(annotationVisitor: AnnotationVisitor?) :
    AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {

    lateinit var methodName: String
        private set

    override fun visit(name: String, value: Any) {
        super.visit(name, value)
        if (name == "name") {
            methodName = value.toString()
        }
    }
}