package com.sabre.buildergenerator.signatureutils;

import junit.framework.TestCase;

public class SignatureUtilTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldSignatureToTypeNameResolveInt() throws Exception {
        // given
        String signature = "I";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "int", type);
    }

    public void testShouldSignatureToTypeNameResolveArray() throws Exception {
        // given
        String signature = "[Z";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "boolean[]", type);
    }

    public void testShouldSignatureToTypeNameResolveClass() throws Exception {
        // given
        String signature = "QString;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "String", type);
    }

    public void testShouldSignatureToTypeNameResolvePackage() throws Exception {
        // given
        String signature = "Qmypackage.MyClass;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "mypackage.MyClass", type);
    }

    public void testShouldSignatureToTypeNameResolveParametrizedType() throws Exception {
        // given
        String signature = "QList<QString;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<String>", type);
    }

    public void testShouldSignatureToTypeNameResolveWildcard() throws Exception {
        // given
        String signature = "QList<*>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<?>", type);
    }

    public void testShouldSignatureToTypeNameResolveExtendsWildcard() throws Exception {
        // given
        String signature = "QList<+QBase;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<? extends Base>", type);
    }

    public void testShouldSignatureToTypeNameResolveSuperWildcard() throws Exception {
        // given
        String signature = "QList<-QBase;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<? super Base>", type);
    }

    public void testShouldSignatureToTypeNameResolveCapture() throws Exception {
        // given
        String signature = "QList<!+QBase;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "List<capture of ? extends Base>", type);
    }

    public void testShouldSignatureToTypeNameResolveMultipleTypeParameters() throws Exception {
        // given
        String signature = "QMap<QInteger;QString;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "Map<Integer,String>", type);
    }

    public void testShouldSignatureToTypeNameResolveNestedTypeParameters() throws Exception {
        // given
        String signature = "QMap<QList<+QInteger;>;QMap<*QString;>;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "Map<List<? extends Integer>,Map<?,String>>", type);
    }

    public void testShouldSignatureToTypeNameResolveInnerType() throws Exception {
        // given
        String signature = "QMyClass<[QString;>.MyInnerClass<[QString;>;";
        // when
        String type = SignatureUtil.signatureToTypeName(signature);
        // then
        assertEquals("Wrong type source", "MyClass<String[]>.MyInnerClass<String[]>", type);
    }

    public void testShouldTypeNameToSignatureResolveInt() throws Exception {
        // given
        String type = "int";
        // when
        String signature = SignatureUtil.typeNameToSignature(type);
        // then
        assertEquals("Wrong type source", "I", signature);
    }

    public void testShouldTypeNameToSignatureResolveMultipleTypeParameters() throws Exception {
        // given
        String type = "Map<Integer,String>";
        // when
        String signature = SignatureUtil.typeNameToSignature(type);
        // then
        assertEquals("Wrong type source", "LMap<LInteger;LString;>;", signature);
    }
}
