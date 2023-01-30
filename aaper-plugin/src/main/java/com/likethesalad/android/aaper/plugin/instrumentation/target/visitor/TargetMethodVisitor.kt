package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import com.likethesalad.android.aaper.plugin.instrumentation.utils.AsmUtils.getCombinedSize
import com.likethesalad.android.aaper.plugin.instrumentation.utils.NamingUtils.wrapMethodName
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
    private lateinit var targetAnnotationVisitor: TargetAnnotationVisitor

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        val annotationVisitor = super.visitAnnotation(descriptor, visible)
        val annotationType = Type.getType(descriptor)
        if (annotationType.className.equals("com.likethesalad.android.aaper.api.EnsurePermissions")) {
            isAnnotated = true
            targetAnnotationVisitor = TargetAnnotationVisitor(annotationVisitor)
            return targetAnnotationVisitor
        }
        return annotationVisitor
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
        val strategyName = targetAnnotationVisitor.strategyName
        val permissions = targetAnnotationVisitor.getPermissions()
        val generatedInternalName = getGeneratedInternalName()
        val argTypes = mutableListOf<Type>(Type.getObjectType(typeInternalName))
        argTypes.addAll(Type.getArgumentTypes(methodDescriptor))

        originalMv.visitCode()
        originalMv.visitVarInsn(Opcodes.ALOAD, 0) // this (1)
        createRunnableObjectAndPushItToTheStack(
            originalMv,
            generatedInternalName,
            argTypes
        ) // Max 2+(argTypes.size), leaves 1
        createStringArrayAndLeaveItInStack(originalMv, permissions) // Max 4, leaves 1
        if (strategyName != null) {
            originalMv.visitLdcInsn(strategyName)// 1
        } else {
            originalMv.visitInsn(Opcodes.ACONST_NULL) // 1
        }

        originalMv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/likethesalad/android/aaper/api/PermissionManager",
            "processPermissionRequest",
            Type.getMethodDescriptor(
                Type.VOID_TYPE,
                Type.getType(Any::class.java),
                Type.getType(Runnable::class.java),
                Type.getType(Array::class.java),
                Type.getType(String::class.java)
            ),
            false
        )

        originalMv.visitInsn(Opcodes.RETURN)
        val varsSize = getCombinedSize(argTypes)
        originalMv.visitMaxs(Math.max(7, 3 + varsSize), varsSize)
        originalMv.visitEnd()
    }

    private fun createRunnableObjectAndPushItToTheStack(
        originalMv: MethodVisitor,
        generatedInternalName: String,
        argTypes: List<Type>
    ) {
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
    }

    private fun createStringArrayAndLeaveItInStack(
        originalMv: MethodVisitor,
        permissions: List<String>
    ) {
        originalMv.visitIntInsn(Opcodes.BIPUSH, permissions.size)
        originalMv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String")
        permissions.forEachIndexed { index, string ->
            originalMv.visitInsn(Opcodes.DUP)
            originalMv.visitIntInsn(Opcodes.BIPUSH, index)
            originalMv.visitLdcInsn(string)
            originalMv.visitInsn(Opcodes.AASTORE)
        }
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
            wrapMethodName(methodName),
            methodDescriptor,
            null,
            null
        )
    }
}