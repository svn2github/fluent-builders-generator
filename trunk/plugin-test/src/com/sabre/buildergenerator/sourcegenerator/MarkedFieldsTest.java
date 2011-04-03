package com.sabre.buildergenerator.sourcegenerator;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.JdtTestCase;

public class MarkedFieldsTest  extends JdtTestCase {
    private MarkedFields<IType, ITypeParameter, IMethod, JavaModelException> markedFields;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        markedFields = new MarkedFields<IType, ITypeParameter, IMethod, JavaModelException>();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldAllBeSelectedByDefault() {
        // given
        String className = "SomeType";
        String fieldName = "someField";

        // when

        //then
        assertEquals("", true, markedFields.isBuilderRequestedForType(className));
        assertEquals("", true, markedFields.isSetterRequestedForField(className, fieldName));
    }

    public void testShouldMarkClassAndField() throws Exception {
        // given
        final IType type = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private int field;")
            .withSourceLine("")
            .withSourceLine("    public int getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(int aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        final IMethod method = type.getMethod("setField", new String[]{"i"});

        // when
        markedFields.retrieveTypesAndFieldsToGenerate(new MethodProvider<IType, IMethod>() {

            public void process(MethodConsumer<IType, IMethod> consumer) {
                consumer.nextMethod(type, method);
            }

        });

        // then
        assertEquals("", true, markedFields.isBuilderRequestedForType("Qtestpkg.MyClass;"));
        assertEquals("", true, markedFields.isSetterRequestedForField("Qtestpkg.MyClass;", "field"));
    }
}
