package com.likethesalad.android.aaper.plugin.instrumentation.visitor

import java.io.PrintStream
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class AaperClassVisitor(classVisitor: ClassVisitor) : ClassVisitor(Opcodes.ASM9, classVisitor) {

    override fun visitEnd() {
        createDummyMethod()

        super.visitEnd()
    }

    private fun createDummyMethod() {
        val mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "myAsmMethod", "()V", null, null)
        mv.visitCode()
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            Type.getInternalName(System::class.java),
            "out",
            Type.getDescriptor(PrintStream::class.java)
        )
        mv.visitLdcInsn("Hi!")
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            Type.getInternalName(PrintStream::class.java),
            "println",
            "(Ljava/lang/String;)V",
            false
        )
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(2, 1)
        mv.visitEnd()
    }
}