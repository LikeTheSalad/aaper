package com.likethesalad.android.aaper.plugin.appender.visitor

import com.likethesalad.android.aaper.internal.compiler.AaperRunnable
import com.likethesalad.android.aaper.plugin.appender.visitor.utils.ClassName
import com.likethesalad.android.aaper.plugin.appender.visitor.utils.FieldInfo
import kotlin.math.max
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

object WraaperClassCreator {
    internal var classVisitorInterceptor: ((ClassVisitor) -> ClassVisitor)? = null
    private const val SUPER_NAME = "java/lang/Object"

    fun create(
        name: ClassName,
        targetMethodName: String,
        target: Type,
        vararg params: Type
    ): ByteArray {
        val cw = ClassWriter(0)
        val cv = classVisitorInterceptor?.invoke(cw) ?: cw

        createType(cv, name.internalName)
        val fields = createFields(cv, target, params)
        createConstructor(cv, name, fields)
        createRunMethod(cv, name, targetMethodName, fields)

        cv.visitEnd()

        return cw.toByteArray()
    }

    private fun createType(cv: ClassVisitor, name: String) {
        cv.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
            name,
            null,
            SUPER_NAME,
            arrayOf(Type.getInternalName(AaperRunnable::class.java))
        )
    }

    private fun createFields(
        cv: ClassVisitor,
        target: Type,
        params: Array<out Type>
    ): List<FieldInfo> {
        val fields = mutableListOf<FieldInfo>()
        val targetParamName = "instance"
        createField(cv, targetParamName, target)
        fields.add(FieldInfo(targetParamName, target))

        params.forEachIndexed { index, type ->
            val paramName = "p$index"
            createField(cv, paramName, type)
            fields.add(FieldInfo(paramName, type))
        }

        return fields
    }

    private fun createField(
        cv: ClassVisitor,
        name: String,
        type: Type
    ) {
        cv.visitField(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL,
            name,
            type.descriptor,
            null,
            null
        ).visitEnd()
    }

    private fun createConstructor(cv: ClassVisitor, name: ClassName, fields: List<FieldInfo>) {
        var maxStack = 0
        val types = fields.map { it.type }
        val mv = cv.visitMethod(
            Opcodes.ACC_PUBLIC,
            "<init>",
            Type.getMethodDescriptor(Type.VOID_TYPE, *types.toTypedArray()),
            null,
            null
        )

        mv.visitCode()

        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, SUPER_NAME, "<init>", "()V", false)
        maxStack++

        // Setting fields from constructor params
        fields.forEachIndexed { index, field ->
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitVarInsn(field.type.getOpcode(Opcodes.ILOAD), index + 1)
            val fieldMaxStack = 1 + field.type.size
            if (fieldMaxStack > maxStack) {
                maxStack = fieldMaxStack
            }
            mv.visitFieldInsn(
                Opcodes.PUTFIELD,
                name.internalName,
                field.name,
                field.type.descriptor
            )
        }

        mv.visitInsn(Opcodes.RETURN)

        mv.visitMaxs(maxStack, types.size + 1)
        mv.visitEnd()
    }

    private fun createRunMethod(
        cv: ClassVisitor,
        name: ClassName,
        targetMethodName: String,
        fields: List<FieldInfo>
    ) {
        var maxStack = 0
        val mv =
            cv.visitMethod(Opcodes.ACC_PUBLIC, "run", "()V", null, null)

        mv.visitCode()

        fields.forEach { field ->
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(
                Opcodes.GETFIELD,
                name.internalName,
                field.name,
                field.type.descriptor
            )
            maxStack++
        }
        val instance = fields.first()
        val params = fields.minus(instance)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            instance.type.internalName,
            targetMethodName,
            Type.getMethodDescriptor(
                Type.VOID_TYPE, *params.map { it.type }.toTypedArray()
            ),
            false
        )

        mv.visitInsn(Opcodes.RETURN)

        mv.visitMaxs(maxStack, 1)
        mv.visitEnd()
    }
}