package com.likethesalad.android.aaper.plugin.appender.visitor

import com.google.common.truth.Truth.assertThat
import com.likethesalad.android.aaper.internal.compiler.AaperRunnable
import com.likethesalad.android.aaper.plugin.appender.visitor.testutils.GeneratedClassLoader
import com.likethesalad.android.aaper.plugin.appender.visitor.utils.ClassName
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.Type
import org.objectweb.asm.util.CheckClassAdapter

class WraaperClassCreatorTest {

    @Before
    fun setUp() {
        WraaperClassCreator.classVisitorInterceptor = {
            CheckClassAdapter(it)
        }
    }

    @After
    fun tearDown() {
        WraaperClassCreator.classVisitorInterceptor = null
    }

    @Test
    fun `Verify zero param method`() {
        val className = ClassName("com.example", "SomeName")
        val target = ZeroParamType()
        val generated =
            WraaperClassCreator.create(className, "callMe", Type.getType(ZeroParamType::class.java))

        val instance = getGeneratedInstance(className, generated, target)

        instance.run()
        assertThat(target.callCount).isEqualTo(1)
    }

    @Test
    fun `Verify one param method`() {
        val className = ClassName("com.example", "SomeName")
        val target = OneParamType()
        val generated =
            WraaperClassCreator.create(
                className,
                "callMe",
                Type.getType(OneParamType::class.java),
                Type.getType(String::class.java)
            )

        val paramValue = "The param value"
        val instance = getGeneratedInstance(className, generated, target, paramValue)

        instance.run()
        assertThat(target.callCount).isEqualTo(1)
        assertThat(target.storedParam).isEqualTo(paramValue)
    }

    private fun getGeneratedInstance(
        name: ClassName,
        generated: ByteArray,
        vararg params: Any
    ): AaperRunnable {
        val clazz = GeneratedClassLoader(javaClass.classLoader).defineClass(
            name.fullName,
            generated
        )
        val classParams = params.map { it.javaClass }.toTypedArray()
        return clazz.getDeclaredConstructor(*classParams).newInstance(*params) as AaperRunnable
    }

    class ZeroParamType {
        var callCount = 0
            private set

        fun callMe() {
            callCount++
        }
    }

    class OneParamType {
        var storedParam = ""

        var callCount = 0
            private set

        fun callMe(param: String) {
            callCount++
            storedParam = param
        }
    }
}