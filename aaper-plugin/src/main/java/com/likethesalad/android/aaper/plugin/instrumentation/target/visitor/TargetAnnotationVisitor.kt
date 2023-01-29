package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class TargetAnnotationVisitor(annotationVisitor: AnnotationVisitor) :
    AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {
    private var permissionsAnnotationVisitor: PermissionsAnnotationVisitor? = null
    var strategyName: String? = null
        private set

    override fun visit(name: String, value: Any?) {
        super.visit(name, value)
        if (name == "strategyName") {
            strategyName = value?.toString()
        }
    }

    override fun visitArray(name: String): AnnotationVisitor {
        val visitor = super.visitArray(name)
        if (name == "permissions") {
            permissionsAnnotationVisitor = PermissionsAnnotationVisitor(visitor)
            return permissionsAnnotationVisitor!!
        }
        return visitor
    }

    fun getPermissions(): List<String> {
        return permissionsAnnotationVisitor?.permissions ?: emptyList()
    }
}