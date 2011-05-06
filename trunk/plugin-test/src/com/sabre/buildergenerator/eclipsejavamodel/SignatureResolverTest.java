package com.sabre.buildergenerator.eclipsejavamodel;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.JdtTestCase;
import com.sabre.buildergenerator.eclipsejavamodel.SignatureResolver;

public class SignatureResolverTest extends JdtTestCase {
    private SignatureResolver typeResolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        typeResolver = new SignatureResolver();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldResolveBinaryClass() throws Exception {
        // given
        IType mainType = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    public int field;")
            .withSourceLine("}")
            .buildType();
        String typeName = "java.util.List";
        String typeSignature = Signature.createTypeSignature(typeName, true);

        // when
        IType resolvedType = typeResolver.resolveType(mainType, typeSignature);

        // then
        assertNotNull("Resolved type should not be null", resolvedType);
        assertEquals("Resolved type doesn't match the given signature", typeName, resolvedType.getFullyQualifiedName('.'));
    }

    public void testShouldResolveProjectClass() throws Exception {
        // given
        IType mainType = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("otherpkg").forClassName("MyOtherClass")
            .withSourceLine("package otherpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyOtherClass {")
            .withSourceLine("}")
            .buildType();
        String typeName = "otherpkg.MyOtherClass";
        String typeSignature = Signature.createTypeSignature(typeName, true);

        // when
        IType resolvedType = typeResolver.resolveType(mainType, typeSignature);

        // then
        assertNotNull("Resolved type should not be null", resolvedType);
        assertEquals("Resolved type doesn't match the given signature", typeName, resolvedType.getFullyQualifiedName('.'));
    }

    public void testShouldResolveInnerClass() throws Exception {
        // given
        IType mainType = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    public static class MyInnerClass {")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        String typeName = "testpkg.MyClass.MyInnerClass";
        String typeSignature = Signature.createTypeSignature(typeName, true);

        // when
        IType resolvedType = typeResolver.resolveType(mainType, typeSignature);

        // then
        assertNotNull("Resolved type should not be null", resolvedType);
        assertEquals("Resolved type doesn't match the given signature", typeName, resolvedType.getFullyQualifiedName('.'));
    }
}
