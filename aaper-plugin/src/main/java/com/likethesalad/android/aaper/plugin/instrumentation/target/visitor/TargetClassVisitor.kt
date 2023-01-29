package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TargetClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

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
            ), cv, name, descriptor
        )
    }
}