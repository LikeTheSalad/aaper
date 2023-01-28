package com.likethesalad.android.aaper.plugin.instrumentation.generated.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes

class AaperGeneratedVisitor(classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        println("Visiting FIELD: '$name' with descriptor: '$descriptor'")
        return super.visitField(access, name, descriptor, signature, value)
    }
}