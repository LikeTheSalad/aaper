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
                
                import com.likethesalad.android.aaper.internal.compiler.AaperRunnable;
                
                class Aaper_MyClass__someMethod implements AaperRunnable {
                    private final MyClass instance;
                    
                    public Aaper_MyClass__someMethod(MyClass instance) {
                        this.instance = instance;
                    }
                
                    public void run() {
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
                
                import com.likethesalad.android.aaper.internal.compiler.AaperRunnable;
                
                class Aaper_MyClass__someMethod implements AaperRunnable {
                    private final MyClass instance;
                    private final int value;
                    
                    public Aaper_MyClass__someMethod(MyClass instance, int value) {
                        this.instance = instance;
                        this.value = value;
                    }
                
                    public void run() {
                    }
                }
            """.trimIndent()
                )
            )
    }

    @Test
    fun `Generate runnable class for an annotated method with an object parameter`() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(
                JavaFileObjects.forSourceString(
                    "com.example.MyClass", """
                package com.example;
                        
                class MyClass {
                    ${getAnnotation("somePermission")}
                    public void someMethod(String value) {
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
                
                import com.likethesalad.android.aaper.internal.compiler.AaperRunnable;
                import java.lang.String;
                
                class Aaper_MyClass__someMethod implements AaperRunnable {
                    private final MyClass instance;
                    private final String value;
                    
                    public Aaper_MyClass__someMethod(MyClass instance, String value) {
                        this.instance = instance;
                        this.value = value;
                    }
                
                    public void run() {
                    }
                }
            """.trimIndent()
                )
            )
    }

    @Test
    fun `Generate runnable class for an annotated method with multiple parameters`() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(
                JavaFileObjects.forSourceString(
                    "com.example.MyClass", """
                package com.example;
                        
                class MyClass {
                    ${getAnnotation("somePermission")}
                    public void someMethod(String value, int someInt) {
                        System.out.println("Some text with value: " + value + " and some other: " + someInt);
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
                
                import com.likethesalad.android.aaper.internal.compiler.AaperRunnable;
                import java.lang.String;
                
                class Aaper_MyClass__someMethod implements AaperRunnable {
                    private final MyClass instance;
                    private final String value;
                    private final int someInt;
                    
                    public Aaper_MyClass__someMethod(MyClass instance, String value, int someInt) {
                        this.instance = instance;
                        this.value = value;
                        this.someInt = someInt;
                    }
                
                    public void run() {
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