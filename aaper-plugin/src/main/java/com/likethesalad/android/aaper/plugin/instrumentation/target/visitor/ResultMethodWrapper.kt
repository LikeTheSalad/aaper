package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import java.lang.Integer.max
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ResultMethodWrapper(methodVisitor: MethodVisitor) :
    MethodVisitor(Opcodes.ASM9, methodVisitor) {

    override fun visitCode() {
        super.visitCode()

        mv.visitVarInsn(Opcodes.ALOAD, 0) // this
        mv.visitVarInsn(Opcodes.ALOAD, 2) // permissions
        val requestCodeMetadataType =
            "com/likethesalad/android/aaper/data/RequestCodeLaunchMetadata"
        mv.visitTypeInsn(Opcodes.NEW, requestCodeMetadataType)
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ILOAD, 1) // requestCode
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, requestCodeMetadataType, "<init>", "(I)V", false)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/likethesalad/android/aaper/api/PermissionManager",
            "processPermissionResponse",
            "(Ljava/lang/Object;[Ljava/lang/String;Lcom/likethesalad/android/aaper/api/base/LaunchMetadata;)V",
            false
        )
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(max(maxStack, 5), maxLocals)
    }
}