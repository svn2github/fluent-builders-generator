package com.sabre.buildergenerator.signatureutils;

import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_BOOLEAN;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_BYTE;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_CHAR;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_DOUBLE;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_FLOAT;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_INT;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_LONG;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_SHORT;
import static com.sabre.buildergenerator.signatureutils.SignatureParser.C_VOID;

import org.eclipse.jdt.core.Signature;

public class SignatureUtil {
    // TODO non static methods
    public static String signatureToTypeName(String typeSignature) {
        final StringBuilder out = new StringBuilder();

        SignatureParser parser = new SignatureParser(typeSignature, new SignatureHandler() {
            int typeArgumentsDepth = 0;
            boolean isFirstTypeArgument = false;
            boolean isWildcard = false;
            boolean isArray = false;

            public void array() {
                isArray = true;
            }

            public void simpleType(char type) {
                switch (type) {
                case C_BYTE:    out.append("byte"); break;
                case C_CHAR:    out.append("char"); break;
                case C_DOUBLE:  out.append("double"); break;
                case C_FLOAT:   out.append("float"); break;
                case C_INT:     out.append("int"); break;
                case C_LONG:    out.append("long"); break;
                case C_SHORT:   out.append("short"); break;
                case C_VOID:    out.append("void"); break;
                case C_BOOLEAN: out.append("boolean"); break;
                }
                endType();
            }

            public void startResolvedType(String identifier) {
                typeArgumentsSeparator();
                out.append(identifier);
            }

            public void startUnresolvedType(String identifier) {
                typeArgumentsSeparator();
                out.append(identifier);
            }

            public void startTypeArguments() {
                out.append("<");
                typeArgumentsDepth++;
                isFirstTypeArgument = true;
            }

            public void endTypeArguments() {
                out.append(">");
                typeArgumentsDepth--;
                isFirstTypeArgument = false;
            }

            public void typeVariable(String identifier) {
                typeArgumentsSeparator();
                out.append(identifier);
            }

            public void wildcardAny() {
                typeArgumentsSeparator();
                out.append("?");
            }

            public void wildcardExtends() {
                typeArgumentsSeparator();
                out.append("? extends ");
                isWildcard = true;
            }

            public void wildcardSuper() {
                typeArgumentsSeparator();
                out.append("? super ");
                isWildcard = true;
            }

            private void typeArgumentsSeparator() {
                if (typeArgumentsDepth > 0 && !isFirstTypeArgument && !isWildcard) {
                    out.append(",");
                }
                isFirstTypeArgument = false;
                isWildcard = false;
            }

            public void endType() {
                if (isArray) {
                    out.append("[]");
                    isArray = false;
                }
            }

            public void captureOf() {
                typeArgumentsSeparator();
                out.append("capture of ");
                isWildcard = true;
            }

            public void innerType(String identifier) {
                out.append(".");
                out.append(identifier);
            }
        });
        try {
            parser.parse();
            return out.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String createTypeSignature(String type) {
        // TODO don't use org.eclipse.jdt.core.Signature class
        return Signature.createTypeSignature(type, false);
    }
}
