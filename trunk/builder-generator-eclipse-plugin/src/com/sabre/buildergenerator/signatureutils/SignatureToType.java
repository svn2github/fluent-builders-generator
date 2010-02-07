package com.sabre.buildergenerator.signatureutils;

import static org.eclipse.jdt.core.Signature.C_BOOLEAN;
import static org.eclipse.jdt.core.Signature.C_BYTE;
import static org.eclipse.jdt.core.Signature.C_CHAR;
import static org.eclipse.jdt.core.Signature.C_DOUBLE;
import static org.eclipse.jdt.core.Signature.C_FLOAT;
import static org.eclipse.jdt.core.Signature.C_INT;
import static org.eclipse.jdt.core.Signature.C_LONG;
import static org.eclipse.jdt.core.Signature.C_SHORT;
import static org.eclipse.jdt.core.Signature.C_VOID;


public class SignatureToType {
    public static String resolveSignature(String signature) {
        final StringBuilder out = new StringBuilder();

        SignatureParser parser = new SignatureParser(signature, new SignatureHandler() {
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
}
