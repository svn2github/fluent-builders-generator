package com.sabre.buildergenerator.typeutils;

public class TypeUtil {
    // TODO non static methods
    public static String normalizeType(String qualifiedTypeName) {
        return qualifiedTypeName
            .replaceAll(" +extends +", "=extends=")
            .replaceAll(" +super +", "=super=")
            .replaceAll(" +capture +of +", "=capture=of=")
            .replaceAll(" +& +", "=&=")
            .replaceAll(" ", "")
            .replaceAll("=", " ");
    }

    /**
     * Calls callback on all type parameters. Replaces parameters with processed strings.
     *
     * for type "package.MyType<java.lang.String,? extends package.MyBase>.MyInner<package.MyGeneric<String>>"
     * parameters are:
     * - "java.lang.String"
     * - "? extends package.MyBase"
     * - "package.MyGeneric<String>"
     *
     * @param qualifiedTypeName type to extract parameters
     * @param proc callback
     * @return processed type description
     */
    public static String processTypeParams(String qualifiedTypeName, TypeProcessor proc) {
        StringBuilder buf = new StringBuilder();
        int depth = 0;
        int beg = 0;
        int i = 0;
        for (char c : qualifiedTypeName.toCharArray()) {
            if (c == '<') {
                depth++;
                if (depth == 1) {
                    buf.append(qualifiedTypeName.substring(beg, i));
                    buf.append('<');
                    beg = i + 1;
                }
            } else if (c == '>') {
                if (depth == 1) {
                    buf.append(proc.processType(qualifiedTypeName.substring(beg, i)));
                    buf.append('>');
                    beg = i + 1;
                }
                depth--;
            } else if (c == ',' && depth == 1) {
                buf.append(proc.processType(qualifiedTypeName.substring(beg, i)));
                buf.append(", ");
                beg = i + 1;
            }
            i++;
        }
        if (beg < qualifiedTypeName.length()) {
            buf.append(qualifiedTypeName.substring(beg));
        }
        return buf.toString();
    }

    public static interface TypeProcessor {
        String processType(String type);
    }
}
