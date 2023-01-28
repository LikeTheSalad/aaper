package com.likethesalad.android.aaper.compiler

import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class AaperProcessorTest {

    @Test
    fun `When annotated method doesn't return void then raise an error`() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(
                JavaFileObjects.forSourceString(
                    "com.example.MyClass", """
                class MyClass {
                    ${getAnnotation("somePermission")}
                    public String someMethod() {
                        return "Something";
                    }
                }
            """.trimIndent()
                )
            )
        assertThat(compile).failed()
        assertThat(compile).hadErrorContaining("EnsurePermissions annotated methods must return void")
    }


    @Test
    fun `Generate runnable class for an annotated method`() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(
                JavaFileObjects.forSourceString(
                    "com.example.MyClass", """
                package com.example;
                        
                class MyClass {
                    ${getAnnotation("somePermission")}
                    public void someMethod() {
                        System.out.println("Some text");
                    }
                }
            """.trimIndent()
                )
            )
        assertThat(compile).succeeded()
        assertThat(compile).generatedSourceFile("com.example.Aaper_MyClass__someMethod")
            .hasSourceEquivalentTo(
                JavaFileObjects.forSourceString(
                    "com.example.Aaper_MyClass__someMethod",
                    """
                package com.example;
                
                import java.lang.Runnable;
                
                class Aaper_MyClass__someMethod implements Runnable {
                    private final MyClass instance;
                    
                    public Aaper_MyClass__someMethod(MyClass instance) {
                        this.instance = instance;
                    }
                
                    public void run() {
                         instance.someMethod();
                    }
                }
            """.trimIndent()
                )
            )
    }

    @Test
    fun `Generate runnable class for an annotated method with a primitive parameter`() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(
                JavaFileObjects.forSourceString(
                    "com.example.MyClass", """
                package com.example;
                        
                class MyClass {
                    ${getAnnotation("somePermission")}
                    public void someMethod(int value) {
                        System.out.println("Some text with value: " + value);
                    }
                }
            """.trimIndent()
                )
            )
        assertThat(compile).succeeded()
        assertThat(compile).generatedSourceFile("com.example.Aaper_MyClass__someMethod")
            .hasSourceEquivalentTo(
                JavaFileObjects.forSourceString(
                    "com.example.Aaper_MyClass__someMethod",
                    """
                package com.example;
                
                import java.lang.Runnable;
                
                class Aaper_MyClass__someMethod implements Runnable {
                    private final MyClass instance;
                    private final int value;
                    
                    public Aaper_MyClass__someMethod(MyClass instance, int value) {
                        this.instance = instance;
                        this.value = value;
                    }
                
                    public void run() {
                         instance.someMethod(value);
                    }
                }
            """.trimIndent()
                )
            )
    }

    private fun getAnnotation(vararg permissions: String): String {
        return "@com.likethesalad.android.aaper.api.EnsurePermissions(permissions={${
            permissions.joinToString(
                ","
            ) { "\"$it\"" }
        }})"
    }
}