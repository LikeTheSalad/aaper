package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import com.likethesalad.android.aaper.plugin.instrumentation.utils.AsmUtils.getMaxStackSize
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class TargetMethodVisitor(
    methodVisitor: MethodVisitor,
    private val cv: ClassVisitor,
    private val typeInternalName: String,
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

    override fun visitEnd() {
        if (originalMv != null) {
            replaceOriginalCode(originalMv!!)
        }

        super.visitEnd()
    }

    private fun replaceOriginalCode(originalMv: MethodVisitor) {
        val generatedInternalName = getGeneratedInternalName()
        val argTypes = mutableListOf<Type>(Type.getObjectType(typeInternalName))
        argTypes.addAll(Type.getArgumentTypes(methodDescriptor))

        originalMv.visitCode()
        originalMv.visitTypeInsn(Opcodes.NEW, generatedInternalName)
        originalMv.visitInsn(Opcodes.DUP)

        argTypes.forEachIndexed { index, type ->
            originalMv.visitVarInsn(getLoadOpCode(type), index)
        }

        originalMv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            generatedInternalName,
            "<init>",
            Type.getMethodDescriptor(Type.VOID_TYPE, *argTypes.toTypedArray()),
            false
        )

        originalMv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/likethesalad/android/aaper/api/PermissionManager",
            "temporary",
            Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Runnable::class.java)),
            false
        )

        originalMv.visitInsn(Opcodes.RETURN)
        val varsSize = getMaxStackSize(argTypes)
        originalMv.visitMaxs(2 + varsSize, varsSize)
        originalMv.visitEnd()
    }

    private fun getLoadOpCode(type: Type): Int {
        return type.getOpcode(Opcodes.ILOAD)
    }

    private fun getGeneratedInternalName(): String {
        val lastSlash = typeInternalName.indexOfLast { it == '/' }
        return if (lastSlash > 0) {
            val prefix = typeInternalName.substring(0, lastSlash)
            val simpleName = typeInternalName.substring(lastSlash + 1)
            "$prefix/${wrapSimpleName(simpleName)}"
        } else {
            wrapSimpleName(typeInternalName)
        }
    }

    private fun wrapSimpleName(simpleName: String): String {
        return "Aaper_${simpleName}_$methodName"
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