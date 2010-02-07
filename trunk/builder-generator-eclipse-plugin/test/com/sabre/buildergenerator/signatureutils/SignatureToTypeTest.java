package com.sabre.buildergenerator.signatureutils;

import com.sabre.buildergenerator.signatureutils.SignatureToType;

import junit.framework.TestCase;

public class SignatureToTypeTest extends TestCase {

    public void testShouldResolveInt() throws Exception {
        // given
        String signature = "I";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "int", type);
    }

    public void testShouldResolveArray() throws Exception {
        // given
        String signature = "[Z";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "boolean[]", type);
    }

    public void testShouldResolveClass() throws Exception {
        // given
        String signature = "QString;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "String", type);
    }

    public void testShouldResolvePackage() throws Exception {
        // given
        String signature = "Qmypackage.MyClass;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "mypackage.MyClass", type);
    }

    public void testShouldResolveParametrizedType() throws Exception {
        // given
        String signature = "QList<QString;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "List<String>", type);
    }

    public void testShouldResolveWildcard() throws Exception {
        // given
        String signature = "QList<*>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "List<?>", type);
    }

    public void testShouldResolveExtendsWildcard() throws Exception {
        // given
        String signature = "QList<+QBase;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "List<? extends Base>", type);
    }

    public void testShouldResolveSuperWildcard() throws Exception {
        // given
        String signature = "QList<-QBase;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "List<? super Base>", type);
    }

    public void testShouldResolveCapture() throws Exception {
        // given
        String signature = "QList<!+QBase;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "List<capture of ? extends Base>", type);
    }

    public void testShouldResolveMultipleTypeParameters() throws Exception {
        // given
        String signature = "QMap<QInteger;QString;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "Map<Integer,String>", type);
    }

    public void testShouldResolveNestedTypeParameters() throws Exception {
        // given
        String signature = "QMap<QList<+QInteger;>;QMap<*QString;>;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "Map<List<? extends Integer>,Map<?,String>>", type);
    }

    public void testShouldResolveInnerType() throws Exception {
        // given
        String signature = "QMyClass<[QString;>.MyInnerClass<[QString;>;";
        // when
        String type = SignatureToType.resolveSignature(signature);
        // then
        assertEquals("Wrong type source", "MyClass<String[]>.MyInnerClass<String[]>", type);
    }

}
