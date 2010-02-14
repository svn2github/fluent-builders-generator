/**
 * Copyright (c) 2009-2010 fluent-builder-generator for Eclipse commiters.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sabre Polska sp. z o.o. - initial implementation during Hackday
 */

package com.sabre.buildergenerator.sourcegenerator;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static com.sabre.buildergenerator.sourcegenerator.TestHelper.*;

public class BuilderClassGeneratorTest extends TestCase {
    private IJavaProject javaProject;
    private BuilderGenerator generator;

    @Override
    protected void setUp() throws Exception {
        javaProject = createJavaProject("test-project", "src", null, JavaCore.VERSION_1_5);
        javaProject.open(null);

        generator = new BuilderGenerator();
    }

    @Override
    protected void tearDown() throws Exception {
        javaProject.close();
        TestHelper.deleteJavaProject(javaProject);
    }

    public void testGenerateEmptyBuilder() throws Exception {
        // given
        IType buildClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("}")
            .buildType();

        // when
        String builderSource = generator.generateSource(buildClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        buildJavaSource().forPackage("builderpkg").forClassName("GeneratedBuilder")
            .withSourceLine(builderSource)
            .buildType();

        IType mainClass = buildJavaSource().forPackage("test").forClassName("MainClass")
            .withSourceLine("package test;")
            .withSourceLine("")
            .withSourceLine("import testpkg.MyClass;")
            .withSourceLine("import builderpkg.GeneratedBuilder;")
            .withSourceLine("")
            .withSourceLine("public class MainClass {")
            .withSourceLine("    public static void main(String[] args) {")
            .withSourceLine("        MyClass obj = GeneratedBuilder.myClass().build();")
            .withSourceLine("        assert obj != null;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        assertEquals("Internal test failed", 0, TestHelper.runJavaFile(javaProject, mainClass.getFullyQualifiedName(), null, null));
    }

    public void testGenerateSimplePrimitiveSetter() throws Exception {
        // given
        IType buildClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
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

        // when
        String builderSource = generator.generateSource(buildClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        buildJavaSource().forPackage("builderpkg").forClassName("GeneratedBuilder")
            .withSourceLine(builderSource)
            .buildType();

        IType mainClass = buildJavaSource().forPackage("test").forClassName("MainClass")
            .withSourceLine("package test;")
            .withSourceLine("")
            .withSourceLine("import testpkg.MyClass;")
            .withSourceLine("import builderpkg.GeneratedBuilder;")
            .withSourceLine("")
            .withSourceLine("public class MainClass {")
            .withSourceLine("    public static void main(String[] args) {")
            .withSourceLine("        MyClass obj = GeneratedBuilder.myClass().withField(5).build();")
            .withSourceLine("        assert obj.getField() == 5;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        assertEquals("Internal test failed", 0, TestHelper.runJavaFile(javaProject, mainClass.getFullyQualifiedName(), null, null));
    }

    public void testGenerateSimpleObjectSetter() throws Exception {
        // given
        IType buildClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private String field;")
            .withSourceLine("")
            .withSourceLine("    public String getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(String aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        // when
        String builderSource = generator.generateSource(buildClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        buildJavaSource().forPackage("builderpkg").forClassName("GeneratedBuilder")
            .withSourceLine(builderSource)
            .buildType();

        IType mainClass = buildJavaSource().forPackage("test").forClassName("MainClass")
            .withSourceLine("package test;")
            .withSourceLine("")
            .withSourceLine("import testpkg.MyClass;")
            .withSourceLine("import builderpkg.GeneratedBuilder;")
            .withSourceLine("")
            .withSourceLine("public class MainClass {")
            .withSourceLine("    public static void main(String[] args) {")
            .withSourceLine("        MyClass obj = GeneratedBuilder.myClass().withField(\"string\").build();")
            .withSourceLine("        assert obj.getField().equals(\"string\");")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        assertEquals("Internal test failed", 0, TestHelper.runJavaFile(javaProject, mainClass.getFullyQualifiedName(), null, null));
    }

    public void testGenerateSimpleObjectSetterWithForeignPackage() throws Exception {
        // given
        IType buildClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private testpkg2.MyData field;")
            .withSourceLine("")
            .withSourceLine("    public testpkg2.MyData getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(testpkg2.MyData aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg2").forClassName("MyData")
            .withSourceLine("package testpkg2;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("    private String dataField;")
            .withSourceLine("")
            .withSourceLine("    public String getDataField() {")
            .withSourceLine("        return dataField;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setDataField(String aField) {")
            .withSourceLine("        dataField = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(buildClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        buildJavaSource().forPackage("builderpkg").forClassName("GeneratedBuilder")
            .withSourceLine(builderSource)
            .buildType();

        IType mainClass = buildJavaSource().forPackage("test").forClassName("MainClass")
            .withSourceLine("package test;")
            .withSourceLine("")
            .withSourceLine("import testpkg.MyClass;")
            .withSourceLine("import testpkg2.MyData;")
            .withSourceLine("import builderpkg.GeneratedBuilder;")
            .withSourceLine("")
            .withSourceLine("public class MainClass {")
            .withSourceLine("    public static void main(String[] args) {")
            .withSourceLine("        MyData data = new MyData();")
            .withSourceLine("        data.setDataField(\"dataField\");")
            .withSourceLine("        MyClass obj = GeneratedBuilder.myClass().withField(data).build();")
            .withSourceLine("        assert obj.getField().getDataField().equals(\"dataField\");")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        assertEquals("Internal test failed", 0, TestHelper.runJavaFile(javaProject, mainClass.getFullyQualifiedName(), null, null));
    }

    public void testGenerateCollectionSetter() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("import java.util.List;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private List<String> fields;")
            .withSourceLine("")
            .withSourceLine("    public List<String> getFields() {")
            .withSourceLine("        return fields;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setFields(List<String> aFields) {")
            .withSourceLine("        fields = aFields;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        buildJavaSource().forPackage("builderpkg").forClassName("GeneratedBuilder")
            .withSourceLine(builderSource)
            .buildType();

        IType mainClass = buildJavaSource().forPackage("test").forClassName("MainClass")
            .withSourceLine("package test;")
            .withSourceLine("")
            .withSourceLine("import testpkg.MyClass;")
            .withSourceLine("import builderpkg.GeneratedBuilder;")
            .withSourceLine("import java.util.Arrays;")
            .withSourceLine("")
            .withSourceLine("public class MainClass {")
            .withSourceLine("    public static void main(String[] args) {")
            .withSourceLine("        MyClass obj1 = GeneratedBuilder.myClass().withFields(Arrays.asList(\"field1\", \"field2\")).build();")
            .withSourceLine("        MyClass obj2 = GeneratedBuilder.myClass().withAddedField(\"field1\").withAddedField(\"field2\").build();")
            .withSourceLine("        assert obj1.getFields().size() == 2;")
            .withSourceLine("        assert obj1.getFields().get(0).equals(\"field1\");")
            .withSourceLine("        assert obj1.getFields().get(1).equals(\"field2\");")
            .withSourceLine("        assert obj2.getFields().size() == 2;")
            .withSourceLine("        assert obj2.getFields().get(0).equals(\"field1\");")
            .withSourceLine("        assert obj2.getFields().get(1).equals(\"field2\");")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        Arrays.asList("");
        assertEquals("Internal test failed", 0, TestHelper.runJavaFile(javaProject, mainClass.getFullyQualifiedName(), null, null));
    }

    public void testGenerateFieldBuilder() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private MyData field;")
            .withSourceLine("")
            .withSourceLine("    public MyData getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(MyData aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg").forClassName("MyData")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(testpkg.MyData aValue) {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldMyDataBuilder withField() {")
            .withSourceLine("        testpkg.MyData field = new testpkg.MyData();")
            .withSourceLine("")
            .withSourceLine("        return withField(field).new FieldMyDataBuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldMyDataBuilder extends MyDataBuilder<FieldMyDataBuilder> {")
            .withSourceLine("        public FieldMyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyDataBuilder<T extends MyDataBuilder> {")
            .withSourceLine("        private testpkg.MyData instance;")
            .withSourceLine("")
            .withSourceLine("        private MyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateCollectionFieldBuilder() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("import java.util.List;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private List<MyData> fields;")
            .withSourceLine("")
            .withSourceLine("    public List<MyData> getFields() {")
            .withSourceLine("        return fields;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setFields(List<MyData> aFields) {")
            .withSourceLine("        fields = aFields;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg").forClassName("MyData")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withFields(java.util.List<testpkg.MyData> aValue) {")
            .withSourceLine("        instance.setFields(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public GeneratedBuilder withAddedField(testpkg.MyData aValue) {")
            .withSourceLine("        if (instance.getFields() == null) {")
            .withSourceLine("            instance.setFields(new java.util.ArrayList<testpkg.MyData>());")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        ((java.util.ArrayList<testpkg.MyData>)instance.getFields()).add(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldMyDataBuilder withAddedField() {")
            .withSourceLine("        testpkg.MyData field = new testpkg.MyData();")
            .withSourceLine("")
            .withSourceLine("        return withAddedField(field).new FieldMyDataBuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldMyDataBuilder extends MyDataBuilder<FieldMyDataBuilder> {")
            .withSourceLine("        public FieldMyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyDataBuilder<T extends MyDataBuilder> {")
            .withSourceLine("        private testpkg.MyData instance;")
            .withSourceLine("")
            .withSourceLine("        private MyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateInnerBuilderWithSimpleSetter() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private MyData field;")
            .withSourceLine("")
            .withSourceLine("    public MyData getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(MyData aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg").forClassName("MyData")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("    private String dataField;")
            .withSourceLine("")
            .withSourceLine("    public String getDataField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setDataField(String aField) {")
            .withSourceLine("        dataField = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(testpkg.MyData aValue) {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldMyDataBuilder withField() {")
            .withSourceLine("        testpkg.MyData field = new testpkg.MyData();")
            .withSourceLine("")
            .withSourceLine("        return withField(field).new FieldMyDataBuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldMyDataBuilder extends MyDataBuilder<FieldMyDataBuilder> {")
            .withSourceLine("        public FieldMyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyDataBuilder<T extends MyDataBuilder> {")
            .withSourceLine("        private testpkg.MyData instance;")
            .withSourceLine("")
            .withSourceLine("        private MyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withDataField(java.lang.String aValue) {")
            .withSourceLine("            instance.setDataField(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateInnerBuilderWithCollectionSetter() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private MyData field;")
            .withSourceLine("")
            .withSourceLine("    public MyData getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(MyData aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg").forClassName("MyData")
            .withSourceLine("package testpkg;")
            .withSourceLine("import java.util.List;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("    private List<String> dataFields;")
            .withSourceLine("")
            .withSourceLine("    public list<String> getDataFields() {")
            .withSourceLine("        return dataFields;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setDataFields(List<String> aFields) {")
            .withSourceLine("        dataFields = aFields;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(testpkg.MyData aValue) {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldMyDataBuilder withField() {")
            .withSourceLine("        testpkg.MyData field = new testpkg.MyData();")
            .withSourceLine("")
            .withSourceLine("        return withField(field).new FieldMyDataBuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldMyDataBuilder extends MyDataBuilder<FieldMyDataBuilder> {")
            .withSourceLine("        public FieldMyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyDataBuilder<T extends MyDataBuilder> {")
            .withSourceLine("        private testpkg.MyData instance;")
            .withSourceLine("")
            .withSourceLine("        private MyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withDataFields(java.util.List<java.lang.String> aValue) {")
            .withSourceLine("            instance.setDataFields(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withAddedDataField(java.lang.String aValue) {")
            .withSourceLine("            if (instance.getDataFields() == null) {")
            .withSourceLine("                instance.setDataFields(new java.util.ArrayList<java.lang.String>());")
            .withSourceLine("            }")
            .withSourceLine("")
            .withSourceLine("            ((java.util.ArrayList<java.lang.String>)instance.getDataFields()).add(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateInnerBuilderWithFieldBuilder() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private MyData field;")
            .withSourceLine("")
            .withSourceLine("    public MyData getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(MyData aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg").forClassName("MyData")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("    private MyData2 dataField;")
            .withSourceLine("")
            .withSourceLine("    public MyData2 getDataField() {")
            .withSourceLine("        return dataField;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setDataField(MyData2 aField) {")
            .withSourceLine("        dataField = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();
        buildJavaSource().forPackage("testpkg").forClassName("MyData2")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyData2 {")
            .withSourceLine("    private String data2Field;")
            .withSourceLine("")
            .withSourceLine("    public String getData2Field() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setData2Field(String aField) {")
            .withSourceLine("        data2Field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(testpkg.MyData aValue) {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldMyDataBuilder withField() {")
            .withSourceLine("        testpkg.MyData field = new testpkg.MyData();")
            .withSourceLine("")
            .withSourceLine("        return withField(field).new FieldMyDataBuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldMyDataBuilder extends MyDataBuilder<FieldMyDataBuilder> {")
            .withSourceLine("        public FieldMyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyDataBuilder<T extends MyDataBuilder> {")
            .withSourceLine("        private testpkg.MyData instance;")
            .withSourceLine("")
            .withSourceLine("        private MyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withDataField(testpkg.MyData2 aValue) {")
            .withSourceLine("            instance.setDataField(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public DataFieldMyData2Builder withDataField() {")
            .withSourceLine("            testpkg.MyData2 dataField = new testpkg.MyData2();")
            .withSourceLine("")
            .withSourceLine("            return withDataField(dataField).new DataFieldMyData2Builder(dataField);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public class DataFieldMyData2Builder extends MyData2Builder<DataFieldMyData2Builder> {")
            .withSourceLine("            public DataFieldMyData2Builder(testpkg.MyData2 aInstance) {")
            .withSourceLine("                super(aInstance);")
            .withSourceLine("            }")
            .withSourceLine("")
            .withSourceLine("            public T endDataField() {")
            .withSourceLine("                return (T) MyDataBuilder.this;")
            .withSourceLine("            }")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyData2Builder<T extends MyData2Builder> {")
            .withSourceLine("        private testpkg.MyData2 instance;")
            .withSourceLine("")
            .withSourceLine("        private MyData2Builder(testpkg.MyData2 aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withData2Field(java.lang.String aValue) {")
            .withSourceLine("            instance.setData2Field(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateInnerBuilderWithCollectionFieldBuilder() throws Exception {
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private MyData field;")
            .withSourceLine("")
            .withSourceLine("    public MyData getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(MyData aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();
        buildJavaSource().forPackage("testpkg").forClassName("MyData")
            .withSourceLine("package testpkg;")
            .withSourceLine("import java.util.List;")
            .withSourceLine("")
            .withSourceLine("public class MyData {")
            .withSourceLine("    private List<MyData2> dataFields;")
            .withSourceLine("")
            .withSourceLine("    public List<MyData2> getDataFields() {")
            .withSourceLine("        return dataFields;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setDataFields(List<MyData2> aFields) {")
            .withSourceLine("        dataFields = aFields;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();
        buildJavaSource().forPackage("testpkg").forClassName("MyData2")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyData2 {")
            .withSourceLine("    private String data2Field;")
            .withSourceLine("")
            .withSourceLine("    public String getData2Field() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setData2Field(String aField) {")
            .withSourceLine("        data2Field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(testpkg.MyData aValue) {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldMyDataBuilder withField() {")
            .withSourceLine("        testpkg.MyData field = new testpkg.MyData();")
            .withSourceLine("")
            .withSourceLine("        return withField(field).new FieldMyDataBuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldMyDataBuilder extends MyDataBuilder<FieldMyDataBuilder> {")
            .withSourceLine("        public FieldMyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyDataBuilder<T extends MyDataBuilder> {")
            .withSourceLine("        private testpkg.MyData instance;")
            .withSourceLine("")
            .withSourceLine("        private MyDataBuilder(testpkg.MyData aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withDataFields(java.util.List<testpkg.MyData2> aValue) {")
            .withSourceLine("            instance.setDataFields(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withAddedDataField(testpkg.MyData2 aValue) {")
            .withSourceLine("            if (instance.getDataFields() == null) {")
            .withSourceLine("                instance.setDataFields(new java.util.ArrayList<testpkg.MyData2>());")
            .withSourceLine("            }")
            .withSourceLine("")
            .withSourceLine("            ((java.util.ArrayList<testpkg.MyData2>)instance.getDataFields()).add(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public DataFieldMyData2Builder withAddedDataField() {")
            .withSourceLine("            testpkg.MyData2 dataField = new testpkg.MyData2();")
            .withSourceLine("")
            .withSourceLine("            return withAddedDataField(dataField).new DataFieldMyData2Builder(dataField);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public class DataFieldMyData2Builder extends MyData2Builder<DataFieldMyData2Builder> {")
            .withSourceLine("            public DataFieldMyData2Builder(testpkg.MyData2 aInstance) {")
            .withSourceLine("                super(aInstance);")
            .withSourceLine("            }")
            .withSourceLine("")
            .withSourceLine("            public T endDataField() {")
            .withSourceLine("                return (T) MyDataBuilder.this;")
            .withSourceLine("            }")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class MyData2Builder<T extends MyData2Builder> {")
            .withSourceLine("        private testpkg.MyData2 instance;")
            .withSourceLine("")
            .withSourceLine("        private MyData2Builder(testpkg.MyData2 aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public T withData2Field(java.lang.String aValue) {")
            .withSourceLine("            instance.setData2Field(aValue);")
            .withSourceLine("")
            .withSourceLine("            return (T) this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateBuilderForClassExtendingGeneric() throws Exception {
        buildJavaSource().forPackage("testpkg").forClassName("MyException")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyException<L> extends Exception {")
            .withSourceLine("}")
            .buildFile();
        buildJavaSource().forPackage("testpkg").forClassName("MyBase")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyBase<K, L> {")
            .withSourceLine("    public void setField(K aField) throws MyException<L> {")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildFile();
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass extends MyBase<String, Integer> {")
            .withSourceLine("}")
            .buildType();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(java.lang.String aValue) throws testpkg.MyException<java.lang.Integer> {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    public void testGenerateParametrizedTypeFieldSetter() throws Exception {
        buildJavaSource().forPackage("testpkg").forClassName("A")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class A<T> {")
            .withSourceLine("}")
            .buildFile();
        IType builderClass = buildJavaSource().forPackage("testpkg").forClassName("MyClass")
            .withSourceLine("package testpkg;")
            .withSourceLine("")
            .withSourceLine("public class MyClass {")
            .withSourceLine("    private A<String> field;")
            .withSourceLine("")
            .withSourceLine("    public A<String> getField() {")
            .withSourceLine("        return field;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public void setField(A<String> aField) {")
            .withSourceLine("        field = aField;")
            .withSourceLine("    }")
            .withSourceLine("}")
            .buildType();

        // when
        String builderSource = generator.generateSource(builderClass, "builderpkg", "GeneratedBuilder", null, "with", "withAdded", "end", false);

        // then
        String expectedBuilderSource = buildBuilderSource("builderpkg", "GeneratedBuilder")
            .withSourceLine("    public GeneratedBuilder withField(testpkg.A<java.lang.String> aValue) {")
            .withSourceLine("        instance.setField(aValue);")
            .withSourceLine("")
            .withSourceLine("        return this;")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public FieldABuilder withField() {")
            .withSourceLine("        testpkg.A<java.lang.String> field = new testpkg.A<java.lang.String>();")
            .withSourceLine("")
            .withSourceLine("        return withField(field).new FieldABuilder(field);")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public class FieldABuilder extends ABuilder<FieldABuilder> {")
            .withSourceLine("        public FieldABuilder(testpkg.A<java.lang.String> aInstance) {")
            .withSourceLine("            super(aInstance);")
            .withSourceLine("        }")
            .withSourceLine("")
            .withSourceLine("        public GeneratedBuilder endField() {")
            .withSourceLine("            return GeneratedBuilder.this;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .withSourceLine("")
            .withSourceLine("    public static class ABuilder<T extends ABuilder> {")
            .withSourceLine("        private testpkg.A instance;")
            .withSourceLine("")
            .withSourceLine("        private ABuilder(testpkg.A aInstance) {")
            .withSourceLine("            instance = aInstance;")
            .withSourceLine("        }")
            .withSourceLine("    }")
            .buildSource();
        assertEquals("Builder source mismatch", expectedBuilderSource, builderSource);
    }

    private static IType generateBuilder(BuilderGenerator generator, IType buildClass, String packageName, String builderName)
            throws Exception {
        String builderSource = generator.generateSource(buildClass, packageName, builderName, null, "with",
                "withAdded", "end", false);
        ICompilationUnit compilationUnit = createCompilationUnit(createJavaFile(buildClass.getJavaProject(),
                packageName, packageName, builderSource));
        return compilationUnit.getType(builderName);
    }

    private static String getMethodSource(IType builderClass, String methodName, String[] parameterTypes)
            throws JavaModelException {
        String[] signatures = new String[parameterTypes.length];
        for (int i = 0; i < signatures.length; i++) {
            signatures[i] = Signature.createTypeSignature(parameterTypes[i], false);
        }
        return builderClass.getMethod(methodName, signatures).getSource();
    }

    JavaBuilder buildJavaSource() {
        return new JavaBuilder();
    }

    JavaBuilder buildBuilderSource(String aPackage, String aBuilderName) throws Exception {
        return new JavaBuilder() {
            @Override
            String buildSource() throws Exception {
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

    class JavaBuilder {
        String packageName;
        String className;
        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);

        JavaBuilder forPackage(String aPackage) {
            packageName = aPackage;
            return this;
        }

        JavaBuilder forClassName(String aClassName) {
            className = aClassName;
            return this;
        }

        JavaBuilder withSourceLine(String aLine) {
            out.println(aLine);
            return this;
        }

        JavaBuilder withSourceText(String aText) {
            out.print(aText);
            return this;
        }

        IFile buildFile() throws Exception {
            return createJavaFile(javaProject, packageName, className, buildSource());
        }

        String buildSource() throws Exception {
            out.flush();
            return buf.toString();
        }

        ICompilationUnit buildCompilationUnit() throws Exception {
            return createCompilationUnit(buildFile());
        }

        IType buildType() throws Exception {
            return buildCompilationUnit().getTypes()[0];
        }
    }
}
