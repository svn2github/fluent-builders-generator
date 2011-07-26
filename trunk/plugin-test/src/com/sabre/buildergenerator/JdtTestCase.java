package com.sabre.buildergenerator;

import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.eclipsejavamodel.EclipseBuilderGenerator;
import com.sabre.buildergenerator.functionaltest.TestBase;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;


import static com.sabre.buildergenerator.TestHelper.*;

public abstract class JdtTestCase extends TestBase<IType, ITypeParameter, IMethod, JavaModelException, ICompilationUnit, IFile> {
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

    @Override
    protected JavaBuilder buildJavaSource() {
        return new JavaBuilder();
    }


    @Override
    protected IType generateBuilder(BuilderGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, IType buildClass, String packageName, String builderName)
            throws Exception {
        String builderSource = generator.generateSource(buildClass, packageName, builderName, null, "with",
                "withAdded", "end");
        ICompilationUnit compilationUnit = createCompilationUnit(createJavaFile(buildClass.getJavaProject(),
                packageName, packageName, builderSource));
        return compilationUnit.getType(builderName);
    }

    @Override
    protected String getMethodSource(IType builderClass, String methodName, String[] parameterTypes)
            throws JavaModelException {
        String[] signatures = new String[parameterTypes.length];
        for (int i = 0; i < signatures.length; i++) {
            signatures[i] = Signature.createTypeSignature(parameterTypes[i], false);
        }
        return builderClass.getMethod(methodName, signatures).getSource();
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
    public BuilderGenerator<IType, ITypeParameter, IMethod, JavaModelException> createGenerator() {
        return new EclipseBuilderGenerator();
    }

    @Override
    public int runJavaFile(IType mainClass) throws CoreException, InterruptedException {
        int runJavaFile = TestHelper.runJavaFile(getJavaProject(), mainClass.getFullyQualifiedName('.'),
                null, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
        return runJavaFile;
    }

    @Override
    protected IMethod findMethod(IType classType, String methodName, String[] parameterTypeSignatures) {
        return classType.getMethod(methodName, parameterTypeSignatures);
    }

    protected class JavaBuilder extends JavaBuilderBase<JavaBuilder> {
        @Override
        public IFile buildFile() throws Exception {
            return createJavaFile(javaProject, packageName, className, buildSource());
        }

        @Override
        public ICompilationUnit buildCompilationUnit() throws Exception {
            return createCompilationUnit(buildFile());
        }

        @Override
        public IType buildType() throws Exception {
            return buildCompilationUnit().getTypes()[0];
        }
    }
}
