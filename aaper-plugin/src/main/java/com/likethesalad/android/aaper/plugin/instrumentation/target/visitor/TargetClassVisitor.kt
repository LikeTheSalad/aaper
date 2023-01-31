package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import com.likethesalad.android.aaper.plugin.instrumentation.target.visitor.utils.AnnotatedMethodNotifier
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TargetClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor), AnnotatedMethodNotifier {
    private lateinit var internalName: String
    private var hasAnnotatedMethod = false

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        internalName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return TargetMethodVisitor(
            super.visitMethod(
                access,
                name,
                descriptor,
                signature,
                exceptions
            ), cv, internalName, name, descriptor, this
        )
    }

    override fun foundAnnotatedMethod() {
        hasAnnotatedMethod = true
    }
}