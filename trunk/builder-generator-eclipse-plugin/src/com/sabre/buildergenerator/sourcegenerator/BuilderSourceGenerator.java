package com.sabre.buildergenerator.sourcegenerator;

import java.io.PrintWriter;
import java.io.StringWriter;


public class BuilderSourceGenerator extends AbstractBuilderSourceGenerator<String> {
    public static void main(String[] args) {
        BuilderSourceGenerator generator = new BuilderSourceGenerator();

        StringWriter sw = new StringWriter();

        generator.setOut(new PrintWriter(sw));
        generator.startBuilderClass("example.Company", "example.builder", "CompanyBuilder");
        generator.addFieldSetter("name", "java.lang.String", new String[] { "java.lang.Exception" });
        generator.addFieldSetter("location", "example.Address", null);
        generator.addFieldBuilder("location", "example.Address", null);
        generator.addFieldSetter("employees", "java.util.List<example.Person>", null);
        generator.addCollectionElementSetter("employees", "java.util.List<example.Person>", "employee",
            "java.util.ArrayList", null);
        generator.addCollectionElementBuilder("employees", "java.util.List<example.Person>", "employee",
            "java.util.ArrayList", null);
        generator.startInnerBuilderClass("example.Person");
        generator.addInnerFieldSetter("firstName", "java.lang.String", null);
        generator.addInnerFieldSetter("lastName", "java.lang.String", null);
        generator.addInnerFieldBuilder("address", "example.Address", null);
        generator.endInnerBuilderClass();
        generator.endBuilderClass();
        sw.flush();
    }

    @Override public String getClassQName(String aT) {
        int i = aT.indexOf('<');

        return i != -1 ? aT.substring(0, i) : aT;
    }

    @Override public String getClassName(String aT) {
        String classQName = getClassQName(aT);
        int i = classQName.lastIndexOf('.');

        return i != -1 ? classQName.substring(i + 1) : classQName;
    }

    @Override public String getPackage(String aT) {
        int i = aT.lastIndexOf('.');

        return i != -1 ? aT.substring(0, i) : aT;
    }

    @Override public String getInnerType(String aT) {
        int i = aT.indexOf('<');

        String ret = i != -1 ? aT.substring(i + 1, aT.length() - 1) : "";

        i = ret.lastIndexOf(' ');

        if (i != -1) {
            ret = ret.substring(i + 1);
        }

        return ret;
    }

    @Override public String getType(String aT) {
        return aT;
    }

    @Override public String getTypeWithParams(String aT) {
        int i = aT.lastIndexOf('.');

        return i != -1 ? aT.substring(i + 1) : aT;
    }
}
