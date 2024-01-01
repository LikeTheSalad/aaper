package com.likethesalad.android.aaper.plugin.instrumentation.target.visitor

import com.likethesalad.android.aaper.plugin.instrumentation.target.visitor.annotations.TargetAnnotationVisitor
import com.likethesalad.android.aaper.plugin.instrumentation.target.visitor.utils.AnnotatedMethodNotifier
import com.likethesalad.android.aaper.plugin.utils.AsmUtils.getCombinedSize
import com.likethesalad.android.aaper.plugin.utils.NamingUtils.getGeneratedClassSimpleName
import com.likethesalad.android.aaper.plugin.utils.NamingUtils.getWraapMethodName
import java.lang.Integer.max
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
    private val methodDescriptor: String,
    private val annotatedMethodNotifier: AnnotatedMethodNotifier
) : MethodVisitor(Opcodes.ASM9, methodVisitor) {
    private var isAnnotated = false
    private var originalMv: MethodVisitor? = null
    private lateinit var targetAnnotationVisitor: TargetAnnotationVisitor

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        val annotationVisitor = super.visitAnnotation(descriptor, visible)
        val annotationType = Type.getType(descriptor)
        if (annotationType.className.equals("com.likethesalad.android.aaper.api.EnsurePermissions")) {
            isAnnotated = true
            annotatedMethodNotifier.foundAnnotatedMethod()
            targetAnnotationVisitor = TargetAnnotationVisitor(annotationVisitor)
            return targetAnnotationVisitor
        }
        return annotationVisitor
    }

    override fun visitCode() {
        if (isAnnotated) {
            originalMv = mv
            mv = createWraaperMethod()
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
        val strategyType = targetAnnotationVisitor.strategyType
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
        if (strategyType != null) {
            originalMv.visitLdcInsn(strategyType)// 1
        } else {
            originalMv.visitInsn(Opcodes.ACONST_NULL) // 1
        }

        originalMv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/likethesalad/android/aaper/internal/PermissionRequestHandler",
            "processPermissionRequest",
            "(Ljava/lang/Object;Ljava/lang/Runnable;[Ljava/lang/String;Ljava/lang/Class;)V",
            false
        )

        originalMv.visitInsn(Opcodes.RETURN)
        val varsSize = getCombinedSize(argTypes)
        originalMv.visitMaxs(max(6, 3 + varsSize), varsSize)
        originalMv.visitEnd()
    }

    private fun createRunnableObjectAndPushItToTheStack(
        originalMv: MethodVisitor,
        generatedInternalName: String,
        argTypes: List<Type>
    ) {
        originalMv.visitTypeInsn(Opcodes.NEW, generatedInternalName)
        originalMv.visitInsn(Opcodes.DUP)

        var index = 0
        argTypes.forEach { type ->
            originalMv.visitVarInsn(getLoadOpCode(type), index)
            index += type.size
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
            "$prefix/${getGeneratedClassSimpleName(simpleName, methodName)}"
        } else {
            getGeneratedClassSimpleName(typeInternalName, methodName)
        }
    }

    private fun createWraaperMethod(): MethodVisitor {
        return cv.visitMethod(
            Opcodes.ACC_SYNTHETIC,
            getWraapMethodName(methodName),
            methodDescriptor,
            null,
            null
        )
    }
}