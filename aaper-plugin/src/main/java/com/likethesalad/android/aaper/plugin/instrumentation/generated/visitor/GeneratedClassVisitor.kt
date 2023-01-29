package com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class GeneratedClassVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

    private lateinit var methodDefAnnotationVisitor: MethodDefAnnotationVisitor
    private val fields = mutableListOf<Field>()

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

    override fun visitEnd() {
        //todo create doRun method
        super.visitEnd()
    }

    private data class Field(val name: String, val descriptor: String)
}