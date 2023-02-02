package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import com.likethesalad.android.aaper.plugin.instrumentation.target.visitor.utils.AnnotatedMethodNotifier
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TargetClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor), AnnotatedMethodNotifier {
    private lateinit var internalName: String
    private lateinit var superInternalName: String
    private var hasAnnotatedMethod = false
    private var hasFoundResultMethod = false

    companion object {
        private const val RESULT_METHOD_NAME = "onRequestPermissionsResult"
        private const val RESULT_METHOD_DESCRIPTOR = "(I[Ljava/lang/String;[I)V"
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        internalName = name
        superInternalName = superName!!
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        val originalMv = super.visitMethod(
            access,
            name,
            descriptor,
            signature,
            exceptions
        )

        if (isResultMethod(name, descriptor)) {
            hasFoundResultMethod = true
            return ResultMethodWrapper(originalMv)
        }

        return TargetMethodVisitor(
            originalMv, cv, internalName, name, descriptor, this
        )
    }

    override fun visitEnd() {
        if (hasAnnotatedMethod && !hasFoundResultMethod) {
            overrideResultMethod()
        }
        super.visitEnd()
    }

    private fun overrideResultMethod() {
        val mv = visitMethod(
            Opcodes.ACC_PUBLIC,
            RESULT_METHOD_NAME,
            RESULT_METHOD_DESCRIPTOR,
            null,
            null
        )
        mv.visitCode()

        // Super call
        mv.visitVarInsn(Opcodes.ALOAD, 0) // this
        mv.visitVarInsn(Opcodes.ILOAD, 1) // requestCode
        mv.visitVarInsn(Opcodes.ALOAD, 2) // permissions
        mv.visitVarInsn(Opcodes.ALOAD, 3) // grantResults
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            superInternalName,
            RESULT_METHOD_NAME,
            RESULT_METHOD_DESCRIPTOR,
            false
        )

        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(4, 4)
        mv.visitEnd()
    }

    private fun isResultMethod(name: String, descriptor: String): Boolean {
        return name == RESULT_METHOD_NAME && descriptor == RESULT_METHOD_DESCRIPTOR
    }

    override fun foundAnnotatedMethod() {
        hasAnnotatedMethod = true
    }
}