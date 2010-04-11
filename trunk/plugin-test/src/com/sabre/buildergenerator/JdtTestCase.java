package com.sabre.buildergenerator;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;


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


    protected static IType generateBuilder(BuilderGenerator generator, IType buildClass, String packageName, String builderName)
            throws Exception {
        String builderSource = generator.generateSource(buildClass, packageName, builderName, null, "with",
                "withAdded", "end", false);
        ICompilationUnit compilationUnit = createCompilationUnit(createJavaFile(buildClass.getJavaProject(),
                packageName, packageName, builderSource));
        return compilationUnit.getType(builderName);
    }

    protected static String getMethodSource(IType builderClass, String methodName, String[] parameterTypes)
            throws JavaModelException {
        String[] signatures = new String[parameterTypes.length];
        for (int i = 0; i < signatures.length; i++) {
            signatures[i] = Signature.createTypeSignature(parameterTypes[i], false);
        }
        return builderClass.getMethod(methodName, signatures).getSource();
    }

    JavaBuilder buildBuilderSource(String aPackage, String aBuilderName) throws Exception {
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
