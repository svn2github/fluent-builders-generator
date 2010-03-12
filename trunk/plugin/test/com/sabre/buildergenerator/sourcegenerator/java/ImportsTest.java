package com.sabre.buildergenerator.sourcegenerator.java;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

public class ImportsTest extends TestCase {
    private Imports imports;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        imports = new Imports();
    }

    @Override
    protected void tearDown() throws Exception {
        imports = null;
        super.tearDown();
    }

    public void _testTemplate() {
        // given
        HashSet<String> typeParamNames = new HashSet<String>(Arrays.asList("T", "C"));
        String fullType = "";

        // when
        String unqualified = imports.getUnqualified(fullType, typeParamNames);

        // then
        assertEquals("", unqualified);
        assertEquals(3, imports.getImports().size());
        assertTrue(imports.getImports().contains(""));
        assertTrue(imports.getImports().contains(""));
        assertTrue(imports.getImports().contains(""));
    }

    public void testSimpleImport() {
        // given
        String fullType = "my.package.MyClass";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyClass", unqualified);
        assertEquals(1, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
    }

    public void testDontImportJavaLang() {
        // given
        String fullType = "java.lang.String";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("String", unqualified);
        assertEquals(0, imports.getImports().size());
    }

    public void testDontImportSimpleType() {
        // given
        String fullType = "long";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("long", unqualified);
        assertEquals(0, imports.getImports().size());
    }

    public void testArray() {
        // given
        String fullType = "my.package.MyClass[]";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyClass[]", unqualified);
        assertEquals(1, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
    }

    public void testParametrizedType() {
        // given
        String fullType = "my.package.MyClass<other.package.OtherClass>";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyClass<OtherClass>", unqualified);
        assertEquals(2, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
        assertTrue(imports.getImports().contains("other.package.OtherClass"));
    }

    public void testSkipTypeParam() {
        // given
        HashSet<String> typeParamNames = new HashSet<String>(Arrays.asList("T", "C"));
        String fullType = "my.package.MyClass<T, other.package.OtherClass, C>";

        // when
        String unqualified = imports.getUnqualified(fullType, typeParamNames);

        // then
        assertEquals("MyClass<T, OtherClass, C>", unqualified);
        assertEquals(2, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
        assertTrue(imports.getImports().contains("other.package.OtherClass"));
    }

    public void testBounds() {
        // given
        String fullType = "my.package.MyClass<? extends other.package.OtherClass>";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyClass<? extends OtherClass>", unqualified);
        assertEquals(2, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
        assertTrue(imports.getImports().contains("other.package.OtherClass"));
    }

    public void testAnyTypeBounds() {
        // given
        String fullType = "my.package.MyClass<?>";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyClass<?>", unqualified);
        assertEquals(1, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
    }

    public void testInnerTypeSimple() {
        // given
        String fullType = "my.package.MyClass.MyInnerClass";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyInnerClass", unqualified);
        assertEquals(1, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass.MyInnerClass"));
    }

    public void testInnerTypeTwisted() {
        // given
        String fullType = "my.package.MyClass<?>.MyInnerClass";

        // when
        String unqualified = imports.getUnqualified(fullType, null);

        // then
        assertEquals("MyClass<?>.MyInnerClass", unqualified);
        assertEquals(1, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
    }

    public void testComplexType() {
        // given
        HashSet<String> typeParamNames = new HashSet<String>(Arrays.asList("T", "C"));
        String fullType = "my.package.MyClass <T[] , other.package.OtherClass < ? super C> , T < C []>[]>";

        // when
        String unqualified = imports.getUnqualified(fullType, typeParamNames);

        // then
        assertEquals("MyClass<T[], OtherClass<? super C>, T<C[]>[]>", unqualified);
        for (String imp : imports.getImports()) {
            System.out.println(imp);
        }
        assertEquals(2, imports.getImports().size());
        assertTrue(imports.getImports().contains("my.package.MyClass"));
        assertTrue(imports.getImports().contains("other.package.OtherClass"));
    }
}
