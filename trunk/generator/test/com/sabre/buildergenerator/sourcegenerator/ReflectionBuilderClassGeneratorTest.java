package com.sabre.buildergenerator.sourcegenerator;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.sabre.buildergenerator.functionaltest.BuilderClassGeneratorTestBase;
import com.sabre.buildergenerator.javamodel.reflection.JavaRunner;
import com.sabre.buildergenerator.javamodel.reflection.ReflectionBuilderGenerator;

public class ReflectionBuilderClassGeneratorTest extends
        BuilderClassGeneratorTestBase<Type, TypeVariable<?>, Method, Exception, Class<?>, File> {
    private JavaRunner javaRunner;

    @Override
    protected void setUp() throws Exception {
        javaRunner = new JavaRunner();
        super.setUp();
    }

    @Override
    protected JavaBuilder buildJavaSource() {
        return new JavaBuilder();
    }

    @Override
    protected Type generateBuilder(BuilderGenerator<Type, TypeVariable<?>, Method, Exception> generator,
            Type buildClass, String packageName, String builderName) throws Exception {
        String builderSource = generator.generateSource(buildClass, packageName, builderName, null, "with",
                "withAdded", "end");
        String builderClass = packageName + "." + builderName;
        javaRunner.addSource(builderClass, builderSource);
        javaRunner.compileClasses(builderClass);
        return javaRunner.loadClass(builderClass);
    }

    @Override
    protected String getMethodSource(Type builderClass, String methodName, String[] parameterTypes) throws Exception {
//        Class<?> c = (Class<?>) builderClass;
//        Class<?>[] t = new Class<?>[parameterTypes.length];
//        int i = 0;
//        for (String p : parameterTypes) {
//            Class<?> q = Class.forName(p);
//            if (q == null) {
//                q = javaRunner.loadClass(p);
//            }
//            t[i++] = q;
//        }
//        return c.getMethod(methodName, t);
        throw new UnsupportedOperationException("getMethodSource");
    }

    @Override
    protected JavaBuilder buildBuilderSource(String aPackage, String aBuilderName) throws Exception {
        return new JavaBuilder() {
            @Override
            public String buildSource() throws Exception {
                withSourceLine("}");
                return super.buildSource();
            }
        }
        .withSourceLine("package " + aPackage + ";")
        .withSourceLine("")
        .withSourceLine("@SuppressWarnings(\"unchecked\")")
        .withSourceLine("public class " + aBuilderName + " {")
        .withSourceLine("    private testpkg.MyClass instance;")
        .withSourceLine("")
        .withSourceLine("    public static " + aBuilderName + " myClass() {")
        .withSourceLine("        return new " + aBuilderName + "();")
        .withSourceLine("    }")
        .withSourceLine("")
        .withSourceLine("    public " + aBuilderName + "() {")
        .withSourceLine("        instance = new testpkg.MyClass();")
        .withSourceLine("    }")
        .withSourceLine("")
        .withSourceLine("    public testpkg.MyClass build() {")
        .withSourceLine("        return instance;")
        .withSourceLine("    }")
        .withSourceLine("");
    }

    @Override
    protected int runJavaFile(Type mainClass) throws Exception, InterruptedException {
        try {
            javaRunner.runMain((Class<?>) mainClass);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    @Override
    protected BuilderGenerator<Type, TypeVariable<?>, Method, Exception> createGenerator() {
        return new ReflectionBuilderGenerator();
    }

    @Override
    protected Method findMethod(Type classType, String methodName, String[] parameterTypeSignatures) {
        throw new UnsupportedOperationException("findMethod");
    }

    protected class JavaBuilder extends JavaBuilderBase<JavaBuilder> {

        @Override
        public File buildFile() throws Exception {
            throw new UnsupportedOperationException("buildFile");
        }

        @Override
        public Class<?> buildCompilationUnit() throws Exception {
            String cn = packageName + "." + className;
            String src = buildSource();
            javaRunner.addSource(cn, src);
            javaRunner.compileClasses(cn);
            return javaRunner.loadClass(cn);
        }

        @Override
        public Type buildType() throws Exception {
            return buildCompilationUnit();
        }

    }
}
