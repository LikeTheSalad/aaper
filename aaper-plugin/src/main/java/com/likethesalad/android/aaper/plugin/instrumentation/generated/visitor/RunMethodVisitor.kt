package com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RunMethodVisitor(
    methodVisitor: MethodVisitor,
    private val ownerInternalName: String,
    private val generatedMethodName: String
) :
    MethodVisitor(Opcodes.ASM9, methodVisitor) {

    override fun visitCode() {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            ownerInternalName,
            generatedMethodName,
            "()V",
            false
        )
        mv.visitInsn(Opcodes.RETURN)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        println("Visiting generated run max with original stack: $maxStack and locals: $maxLocals")//todo delete
        super.visitMaxs(1, 1)
    }
}