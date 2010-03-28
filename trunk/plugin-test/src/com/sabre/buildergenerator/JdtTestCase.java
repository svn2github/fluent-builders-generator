package com.sabre.buildergenerator;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;


import junit.framework.TestCase;

import static com.sabre.buildergenerator.TestHelper.*;

public abstract class JdtTestCase extends TestCase {
    private IJavaProject javaProject;

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    @Override
    protected void setUp() throws Exception {
        javaProject = createJavaProject("test-project", "src", null, JavaCore.VERSION_1_5);
        javaProject.open(null);
    }

    @Override
    protected void tearDown() throws Exception {
        javaProject.close();
        TestHelper.deleteJavaProject(javaProject);
    }

    protected JavaBuilder buildJavaSource() {
        return new JavaBuilder();
    }

    protected class JavaBuilder {
        String packageName;
        String className;
        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);

        public JavaBuilder forPackage(String aPackage) {
            packageName = aPackage;
            return this;
        }

        public JavaBuilder forClassName(String aClassName) {
            className = aClassName;
            return this;
        }

        public JavaBuilder withSourceLine(String aLine) {
            out.println(aLine);
            return this;
        }

        public JavaBuilder withSourceText(String aText) {
            out.print(aText);
            return this;
        }

        public IFile buildFile() throws Exception {
            return createJavaFile(javaProject, packageName, className, buildSource());
        }

        public String buildSource() throws Exception {
            out.flush();
            return buf.toString();
        }

        public ICompilationUnit buildCompilationUnit() throws Exception {
            return createCompilationUnit(buildFile());
        }

        public IType buildType() throws Exception {
            return buildCompilationUnit().getTypes()[0];
        }
    }
}
