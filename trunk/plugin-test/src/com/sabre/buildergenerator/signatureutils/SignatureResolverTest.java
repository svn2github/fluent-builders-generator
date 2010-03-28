package com.sabre.buildergenerator.signatureutils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.JdtTestCase;

public class SignatureResolverTest extends JdtTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldResolveLibraryClass() throws Exception {
        // given
        IType owningType = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    public int field;")
            .withSourceLine("}")
            .buildType();
        String typeName = "java.util.List";
        String signature = Signature.createTypeSignature(typeName, true);

        // when
        IType resolvedType = SignatureResolver.resolveType(owningType, signature);

        // then
        assertNotNull("Resolved type is null", resolvedType);
        assertEquals("Wrong resolved type", typeName, resolvedType.getFullyQualifiedName());
    }

    public void testShouldResolveSimpleType() throws Exception {
        // given
        IType owningType = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    public int field;")
            .withSourceLine("}")
            .buildType();
        String typeName = "int";
        String signature = Signature.createTypeSignature(typeName, true);

        // when
        IType resolvedType = SignatureResolver.resolveType(owningType, signature);

        // then
        assertNotNull("Resolved type is null", resolvedType);
        assertEquals("Wrong resolved type", typeName, resolvedType.getFullyQualifiedName());
    }

    public void testShouldFindSimpleType() throws JavaModelException {
        IType type = getJavaProject().findType("int", (IProgressMonitor)null);
        assertNotNull(type);
    }

    public void testSimpleType() throws Exception {
        IType owningType = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    public int method(int value);")
            .withSourceLine("}")
            .buildType();

        IMethod method = owningType.getMethods()[0];
        String returnType = method.getReturnType();
        String parameterType = method.getParameterTypes()[0];
        assertEquals("I", returnType);
        assertEquals("I", parameterType);
    }
}
