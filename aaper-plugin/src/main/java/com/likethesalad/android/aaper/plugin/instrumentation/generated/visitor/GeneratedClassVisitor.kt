package com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor

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
        fields.add(Field(name, descriptor))
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        if (isRunMethod(name, descriptor)) {
            return RunMethodVisitor(
                methodVisitor,
                internalClassName,
                GENERATED_RUNNABLE_METHOD_NAME
            )
        }

        return methodVisitor
    }

    private fun isRunMethod(name: String, descriptor: String): Boolean {
        return name == "run" && descriptor == "()V"
    }

    override fun visitEnd() {
        //todo create doRun method
        super.visitEnd()
    }

    private data class Field(val name: String, val descriptor: String)
}