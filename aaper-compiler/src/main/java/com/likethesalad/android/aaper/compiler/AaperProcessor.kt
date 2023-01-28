package com.likethesalad.android.aaper.compiler

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic

@Suppress("UNCHECKED_CAST")
@SupportedAnnotationTypes("com.likethesalad.android.aaper.api.EnsurePermissions")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class AaperProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {

        annotations.forEach { typeElement ->
            val annotatedMethods: Set<ExecutableElement> =
                roundEnv.getElementsAnnotatedWith(typeElement) as Set<ExecutableElement>

            annotatedMethods.forEach { method ->
                if (TypeKind.VOID != method.returnType.kind) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "EnsurePermissions annotated methods must return void", method
                    )
                } else {
                    createClassForMethod(method)
                }
            }
        }

        return true
    }

    private fun createClassForMethod(method: ExecutableElement) {
        val containerClass = method.enclosingElement as TypeElement
        val containerTypeName = TypeName.get(containerClass.asType())

        val constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(containerTypeName, "instance")
            .addStatement("this.\$N = \$N", "instance", "instance")
            .build()

        var packageName = containerClass.qualifiedName.toString()
        val lastDot = packageName.indexOfLast { it == '.' }
        if (lastDot > 0) {
            packageName = packageName.substring(0, lastDot)
        }

        val generatedSimpleName = "Aaper_${containerClass.simpleName}__${method.simpleName}"
        val typeClass =
            TypeSpec.classBuilder(generatedSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addField(containerTypeName, "instance", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(constructor)
                .build()

        val javaFile = JavaFile.builder(packageName, typeClass).build()
        val javaWriter = processingEnv.filer.createSourceFile("${packageName}.$generatedSimpleName")
        val writer = javaWriter.openWriter()

        javaFile.writeTo(writer)

        writer.close()
    }
}