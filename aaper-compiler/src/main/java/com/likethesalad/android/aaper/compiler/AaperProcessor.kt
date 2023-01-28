package com.likethesalad.android.aaper.compiler

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
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
                }
            }
        }

        return true
    }
}