package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class TargetMethodVisitor(
    methodVisitor: MethodVisitor,
    private val cv: ClassVisitor,
    private val methodName: String,
    private val methodDescriptor: String
) : MethodVisitor(Opcodes.ASM9, methodVisitor) {
    private var isAnnotated = false
    private var originalMv: MethodVisitor? = null

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        val annotationType = Type.getType(descriptor)
        if (annotationType.className.equals("com.likethesalad.android.aaper.api.EnsurePermissions")) {
            isAnnotated = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitCode() {
        if (isAnnotated) {
            originalMv = mv
            mv = createWraaper()
        }
        super.visitCode()
    }

    private fun createWraaper(): MethodVisitor {
        return cv.visitMethod(
            Opcodes.ACC_SYNTHETIC,
            "wraaper_$methodName",
            methodDescriptor,
            null,
            null
        )
    }
}