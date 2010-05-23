package com.sabre.buildergenerator.signatureutils;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.JdtTestCase;

public class TypeResolverTest extends JdtTestCase {
    private TypeResolver typeResolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        typeResolver = new TypeResolver();
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

    public void testShouldSignatureToTypeNameResolveInt() throws Exception {
        // given
        String signature = "I";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "int", type);
    }

    public void testShouldSignatureToTypeNameResolveArray() throws Exception {
        // given
        String signature = "[Z";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "boolean[]", type);
    }

    public void testShouldSignatureToTypeNameResolveClass() throws Exception {
        // given
        String signature = "QString;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "String", type);
    }

    public void testShouldSignatureToTypeNameResolvePackage() throws Exception {
        // given
        String signature = "Qmypackage.MyClass;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "mypackage.MyClass", type);
    }

    public void testShouldSignatureToTypeNameResolveParametrizedType() throws Exception {
        // given
        String signature = "QList<QString;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<String>", type);
    }

    public void testShouldSignatureToTypeNameResolveWildcard() throws Exception {
        // given
        String signature = "QList<*>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<?>", type);
    }

    public void testShouldSignatureToTypeNameResolveExtendsWildcard() throws Exception {
        // given
        String signature = "QList<+QBase;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<? extends Base>", type);
    }

    public void testShouldSignatureToTypeNameResolveSuperWildcard() throws Exception {
        // given
        String signature = "QList<-QBase;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<? super Base>", type);
    }

    public void testShouldSignatureToTypeNameResolveCapture() throws Exception {
        // given
        String signature = "QList<!+QBase;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<capture of ? extends Base>", type);
    }

    public void testShouldSignatureToTypeNameResolveMultipleTypeParameters() throws Exception {
        // given
        String signature = "QMap<QInteger;QString;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "Map<Integer,String>", type);
    }

    public void testShouldSignatureToTypeNameResolveNestedTypeParameters() throws Exception {
        // given
        String signature = "QMap<QList<+QInteger;>;QMap<*QString;>;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "Map<List<? extends Integer>,Map<?,String>>", type);
    }

    public void testShouldSignatureToTypeNameResolveInnerType() throws Exception {
        // given
        String signature = "QMyClass<[QString;>.MyInnerClass<[QString;>;";
        // when
        String type = typeResolver.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "MyClass<String[]>.MyInnerClass<String[]>", type);
    }
}
