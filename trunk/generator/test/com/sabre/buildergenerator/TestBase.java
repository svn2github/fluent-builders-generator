package com.sabre.buildergenerator;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.CoreException;

import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;

import junit.framework.TestCase;

public abstract class TestBase<Type, TypeParameter, Method, JavaModelException extends Exception, CompilationUnit, File>
        extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected abstract JavaBuilderBase<?> buildJavaSource();

    protected abstract Type generateBuilder(
            BuilderGenerator<Type, TypeParameter, Method, JavaModelException> generator, Type buildClass,
            String packageName, String builderName) throws Exception;

    protected abstract String getMethodSource(Type builderClass, String methodName, String[] parameterTypes)
            throws JavaModelException;

    protected abstract JavaBuilderBase<?> buildBuilderSource(String aPackage, String aBuilderName) throws Exception;

    protected abstract int runJavaFile(Type mainClass) throws CoreException, InterruptedException;

    protected abstract BuilderGenerator<Type, TypeParameter, Method, JavaModelException> createGenerator();

    protected abstract Method findMethod(final Type classType, String methodName, String[] parameterTypeSignatures);

    protected abstract class JavaBuilderBase<T extends JavaBuilderBase<?>> {
        protected String packageName;
        protected String className;
        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);

        @SuppressWarnings("unchecked")
        public T forPackage(String aPackage) {
            packageName = aPackage;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T forClassName(String aClassName) {
            className = aClassName;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withSourceLine(String aLine) {
            out.println(aLine);
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withSourceText(String aText) {
            out.print(aText);
            return (T) this;
        }

        public String buildSource() throws Exception {
            out.flush();
            return buf.toString();
        }

        public abstract File buildFile() throws Exception;

        public abstract CompilationUnit buildCompilationUnit() throws Exception;

        public abstract Type buildType() throws Exception;
    }

}
