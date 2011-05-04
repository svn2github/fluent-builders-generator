package com.sabre.buildergenerator.javamodel.reflection;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sabre.buildergenerator.sourcegenerator.MethodConsumer;
import com.sabre.buildergenerator.sourcegenerator.MethodProvider;

public class ReflectionBuilderGeneratorTest {
    private ReflectionBuilderGenerator generator;
    private JavaRunner javaRunner;

    @Before
    public void setUp() throws Exception {
        generator = new ReflectionBuilderGenerator();
        javaRunner = new JavaRunner();
    }

    @After
    public void tearDown() throws Exception {
        javaRunner.close();
    }

    @Test(expected = AssertionError.class)
    public void shouldAssertBeEnabled() throws Throwable {
        try {
            // @formatter:off
            javaRunner.addSource("Test",
                    "public class Test {\n" +
                    "    public void runTest() {\n" +
                    "        assert false;\n" +
                    "    }\n" +
                    "}");
            // @formatter:on
            javaRunner.compileClasses("Test");
            javaRunner.run("Test", "runTest");
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void shouldGenerateSimpleBulder() throws Exception {
        // @formatter:off
        javaRunner.addSource("MyClass",
                "public class MyClass {\n" +
                "    private String field;\n" +
                "\n" +
                "    public String getField() {\n" +
                "        return field;\n" +
                "    }\n" +
                "\n" +
                "    public void setField(String field) {\n" +
                "        this.field = field;\n" +
                "    }\n" +
                "}");
        javaRunner.compileClasses("MyClass");
        // @formatter:on
        final Class<?> type = javaRunner.loadClass("MyClass");
        String packageName = "";
        String builderName = "MyClassBuilder";
        MethodProvider<Type, Method> methodProvider = new MethodProvider<Type, Method>() {
            public void process(MethodConsumer<Type, Method> consumer) {
                consumer.nextMethod(type, type.getMethods()[0]);
            }
        };
        String setterPrefix = "with";
        String collectionSetterPrefix = "withAdded";
        String endPrefix = "end";

        // when
        String builderSource = generator.generateSource(type, packageName, builderName, methodProvider, setterPrefix,
                collectionSetterPrefix, endPrefix);

        // then
        assertNotNull(builderSource);

        javaRunner.addSource("MyClassBuilder", builderSource);
        // @formatter:off
        javaRunner.addSource("Test",
                "public class Test {\n" +
                "    public void runTest() {\n" +
                "        MyClass obj = MyClassBuilder.myClass().withField(\"abc123\").build();\n" +
                "        assert \"abc123\".equals(obj.getField());\n" +
                "    }\n" +
                "}");
        // @formatter:on
        javaRunner.compileClasses("MyClassBuilder", "Test");
        javaRunner.run("Test", "runTest");
    }
}
