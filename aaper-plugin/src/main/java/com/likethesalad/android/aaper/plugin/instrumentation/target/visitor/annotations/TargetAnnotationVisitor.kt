package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor.annotations

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class TargetAnnotationVisitor(annotationVisitor: AnnotationVisitor) :
    AnnotationVisitor(Opcodes.ASM9, annotationVisitor) {
    private var permissionsAnnotationVisitor: PermissionsAnnotationVisitor? = null
    var strategy: Type? = null
        private set

    override fun visit(name: String, value: Any?) {
        super.visit(name, value)
        if (name == "strategy") {
            strategy = value as Type
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