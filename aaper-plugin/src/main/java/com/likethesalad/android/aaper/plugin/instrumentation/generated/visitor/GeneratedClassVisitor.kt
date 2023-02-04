package com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor

import com.likethesalad.android.aaper.plugin.instrumentation.utils.AsmUtils
import com.likethesalad.android.aaper.plugin.instrumentation.utils.NamingUtils.wrapMethodName
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class GeneratedClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

    private lateinit var methodDefAnnotationVisitor: MethodDefAnnotationVisitor
    private lateinit var internalClassName: String
    private val fields = mutableListOf<Field>()

    companion object {
        private const val GENERATED_RUNNABLE_METHOD_NAME = "doRun"
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        internalClassName = name
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        val annotationType = Type.getType(descriptor)
        val originalVisitor = super.visitAnnotation(descriptor, visible)

        if (annotationType.className.equals("com.likethesalad.android.aaper.internal.compiler.AaperMethodDef")) {
            methodDefAnnotationVisitor = MethodDefAnnotationVisitor(originalVisitor)
            return methodDefAnnotationVisitor
        }

        return originalVisitor
    }

    override fun visitField(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        fields.add(Field(name, Type.getType(descriptor)))
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {

        if (isRunMethod(name, descriptor)) {
            createRunMethod(name, descriptor)
            return null
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    private fun createRunMethod(name: String, descriptor: String) {
        val mv = cv.visitMethod(Opcodes.ACC_PUBLIC, name, descriptor, null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            internalClassName,
            GENERATED_RUNNABLE_METHOD_NAME,
            "()V",
            false
        )
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    private fun isRunMethod(name: String, descriptor: String): Boolean {
        return name == "run" && descriptor == "()V"
    }

    override fun visitEnd() {
        createDoRunMethod()
        super.visitEnd()
    }

    private fun createDoRunMethod() {
        val doRunMv = cv.visitMethod(
            Opcodes.ACC_SYNTHETIC or Opcodes.ACC_PRIVATE,
            GENERATED_RUNNABLE_METHOD_NAME,
            "()V",
            null,
            null
        )
        val instanceField = fields.first()
        doRunMv.visitCode()
        fields.forEach {
            doRunMv.visitVarInsn(Opcodes.ALOAD, 0)
            doRunMv.visitFieldInsn(
                Opcodes.GETFIELD,
                internalClassName,
                it.name,
                it.type.descriptor
            )
        }
        doRunMv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            instanceField.type.internalName,
            wrapMethodName(methodDefAnnotationVisitor.methodName),
            getTargetMethodDescriptor(fields.minus(instanceField)),
            false
        )
        doRunMv.visitInsn(Opcodes.RETURN)
        doRunMv.visitMaxs(AsmUtils.getCombinedSize(fields.map { it.type }), 1)
        doRunMv.visitEnd()
    }

    private fun getTargetMethodDescriptor(parameters: List<Field>): String {
        if (parameters.isEmpty()) {
            return "()V"
        }
        val parameterTypes = parameters.map { it.type }.toTypedArray()
        return Type.getMethodDescriptor(Type.VOID_TYPE, *parameterTypes)
    }

    private data class Field(val name: String, val type: Type)
}