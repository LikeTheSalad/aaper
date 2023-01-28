package com.likethesalad.android.aaper.compiler

import com.google.auto.service.AutoService
import com.likethesalad.android.aaper.internal.compiler.AaperRunnable
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic

@AutoService(Processor::class)
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
        val methodName = method.simpleName
        val parameters = method.parameters

        val constructor = createConstructor(containerTypeName, parameters)
        val runMethod = createRunMethod(methodName, parameters)

        val packageName = getPackageName(containerClass)
        val generatedSimpleName = "Aaper_${containerClass.simpleName}__$methodName"

        val typeClass = createGeneratedType(
            generatedSimpleName,
            containerTypeName,
            parameters,
            constructor,
            runMethod
        )

        val javaFile = JavaFile.builder(packageName, typeClass).build()
        val javaWriter = processingEnv.filer.createSourceFile("${packageName}.$generatedSimpleName")
        val writer = javaWriter.openWriter()

        javaFile.writeTo(writer)

        writer.close()
    }

    private fun createRunMethod(
        methodName: Name,
        parameters: List<VariableElement>
    ): MethodSpec {
        return MethodSpec.methodBuilder("run")
            .addModifiers(Modifier.PUBLIC)
            .addStatement(
                "\$N.$methodName(${
                    parameters.joinToString(", ") { it.simpleName }
                })", "instance"
            ).build()
    }

    private fun createGeneratedType(
        generatedSimpleName: String,
        containerTypeName: TypeName?,
        parameters: List<VariableElement>,
        vararg methods: MethodSpec
    ): TypeSpec {
        val typeClass = TypeSpec.classBuilder(generatedSimpleName)
            .addSuperinterface(AaperRunnable::class.java)
            .addField(containerTypeName, "instance", Modifier.PRIVATE, Modifier.FINAL)
            .addMethods(methods.asList())

        parameters.forEach {
            typeClass.addField(
                TypeName.get(it.asType()),
                it.simpleName.toString(),
                Modifier.PRIVATE,
                Modifier.FINAL
            )
        }
        return typeClass.build()
    }

    private fun createConstructor(
        containerTypeName: TypeName,
        parameters: List<VariableElement>
    ): MethodSpec {
        val builder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(containerTypeName, "instance")
            .addStatement("this.\$N = \$N", "instance", "instance")

        parameters.forEach {
            builder.addParameter(ParameterSpec.get(it))
            builder.addStatement("this.\$N = \$N", it.simpleName, it.simpleName)
        }

        return builder.build()
    }

    private fun getPackageName(containerClass: TypeElement): String {
        var packageName = containerClass.qualifiedName.toString()
        val lastDot = packageName.indexOfLast { it == '.' }
        if (lastDot > 0) {
            packageName = packageName.substring(0, lastDot)
        }
        return packageName
    }
}