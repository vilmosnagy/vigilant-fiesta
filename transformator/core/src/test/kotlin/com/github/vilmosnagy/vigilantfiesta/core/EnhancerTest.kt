package com.github.vilmosnagy.vigilantfiesta.core

import com.github.vilmosnagy.vigilantfiesta.core.testutils.ClassLoaderFromMemory
import com.github.vilmosnagy.vigilantfiesta.core.testutils.VigilantClient
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FeatureSpec
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import java.nio.file.Files
import javax.tools.ToolProvider


internal class EnhancerTest : FeatureSpec() {

    override val oneInstancePerTest = true

    @InjectMocks
    private lateinit var testObj: Enhancer

    init {
        MockitoAnnotations.initMocks(this)
        feature("Should insert [METHOD CALLING - BEGIN] call into methods") {
            scenario("into public instance methods") {
                val classAsString = """
                public class Main {
                    public boolean fooCalled = false;
                    public void foo() {
                        fooCalled = true;
                    }
                }
                """

                val classBytes = getBytesFromClass(classAsString, "Main")
                val enhancedBytes = testObj.enhanceClass(classBytes)
                val clazz = loadClassIntoVM(enhancedBytes, "Main")
                val instance = getInstanceOf(clazz)
                getInstanceFieldValue(instance, "fooCalled") shouldBe false
                callInstanceMethod(instance, "foo")
                getInstanceFieldValue(instance, "fooCalled") shouldBe true
//                VigilantClient.methodName shouldBe "foo"
//                VigilantClient.arguments shouldBe emptyList<Any?>()

            }
        }
    }

    private fun callInstanceMethod(instance: Any, methodName: String, vararg params: Any?): Any? {
        val klass = instance.javaClass
        val method = klass.getDeclaredMethod(methodName)
        return method.invoke(instance, *params)
    }

    fun getInstanceFieldValue(instance: Any, fieldName: String): Any? {
        val klass = instance.javaClass
        val field = klass.getDeclaredField(fieldName)
        return field.get(instance)
    }

    private fun <T> getInstanceOf(clazz: Class<T>): T {
        return clazz.newInstance()
    }

    private fun loadClassIntoVM(enhancedBytes: ByteArray, canonicalName: String): Class<*> {
        val classLoader = ClassLoaderFromMemory(
                mapOf(canonicalName to enhancedBytes),
                javaClass.classLoader
        )
        return classLoader.loadClass(canonicalName)
    }

    private fun getBytesFromClass(classAsString: String, canonicalName: String): ByteArray {
        val tmpDir = Files.createTempDirectory("src")
        val sourceFile = tmpDir.resolve("${canonicalName}.java")
        Files.write(sourceFile, classAsString.toByteArray())

        val compiler = ToolProvider.getSystemJavaCompiler()
        compiler.run(null, null, null, sourceFile.normalize().toString())

        val compiledFile = tmpDir.resolve("${canonicalName}.class")
        return Files.readAllBytes(compiledFile)
    }

}

