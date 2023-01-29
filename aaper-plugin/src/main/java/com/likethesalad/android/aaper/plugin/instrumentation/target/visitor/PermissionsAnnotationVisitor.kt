package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class PermissionsAnnotationVisitor(annotationVisitor: AnnotationVisitor) :
    AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {
    val permissions = mutableListOf<String>()

    override fun visit(name: String?, value: Any) {
        super.visit(name, value)
        permissions.add(value.toString())
    }
}