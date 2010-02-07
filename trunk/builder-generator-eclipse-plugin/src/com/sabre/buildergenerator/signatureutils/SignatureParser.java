package com.sabre.buildergenerator.signatureutils;

import static org.eclipse.jdt.core.Signature.*;

public class SignatureParser {
    private static final char C_SLASH = '/';

    String input;
    int pos;
    SignatureHandler listener;

    public SignatureParser(String aInput, SignatureHandler aListener) {
        input = aInput;
        listener = aListener;
    }

    public void parse() throws SignatureParserException, ExceptionWrapper {
        if (eatChar(C_GENERIC_START)) {
            // TODO
        }
        parseTypeSignature();
    }

    private void parseTypeSignature() throws SignatureParserException, ExceptionWrapper {
        switch (peekChar()) {
        case C_BYTE:
        case C_CHAR:
        case C_DOUBLE:
        case C_FLOAT:
        case C_INT:
        case C_LONG:
        case C_SHORT:
        case C_VOID:
        case C_BOOLEAN:
            listener.simpleType(nextChar());
            break;
        case C_ARRAY:
            nextChar();
            listener.array();
            parseTypeSignature();
            break;
        case C_CAPTURE:
            nextChar();
            listener.captureOf();
            parseTypeSignature();
            break;
        case C_TYPE_VARIABLE:
            nextChar();
            listener.typeVariable(parseIdentifier());
            expectChar(C_NAME_END);
            listener.endType();
            break;
        case C_UNRESOLVED:
            nextChar();
            listener.startUnresolvedType(parseIdentifier());
            parseOptionalTypeArguments();
            while (eatChar(C_DOT) || eatChar(C_SLASH)) {
                listener.innerType(parseIdentifier());
                parseOptionalTypeArguments();
            }
            expectChar(C_NAME_END);
            listener.endType();
            break;
        case C_RESOLVED:
            nextChar();
            listener.startResolvedType(parseIdentifier());
            parseOptionalTypeArguments();
            while (eatChar(C_DOT) || eatChar(C_SLASH)) {
                listener.innerType(parseIdentifier());
                parseOptionalTypeArguments();
            }
            expectChar(C_NAME_END);
            listener.endType();
            break;
        }
    }

    private void parseOptionalTypeArguments() throws SignatureParserException, ExceptionWrapper {
        if (eatChar(C_GENERIC_START)) {
            listener.startTypeArguments();
            do {
                parseTypeArgument();
            } while (peekChar() != C_GENERIC_END);
            nextChar();
            listener.endTypeArguments();
        }
    }

    private void parseTypeArgument() throws SignatureParserException, ExceptionWrapper {
        switch (peekChar()) {
        case C_STAR:
            nextChar();
            listener.wildcardAny();
            break;
        case C_EXTENDS:
            nextChar();
            listener.wildcardExtends();
            parseTypeSignature();
            break;
        case C_SUPER:
            nextChar();
            listener.wildcardSuper();
            parseTypeSignature();
            break;
        default:
            parseTypeSignature();
            break;
        }
    }

    private String parseIdentifier() {
        int beg = pos;
        do {
            if (Character.isJavaIdentifierStart(peekChar())) {
                nextChar();
                while (pos < input.length() && Character.isJavaIdentifierPart(peekChar())) {
                    nextChar();
                }
            }
        } while (pos < input.length() && eatChar(C_DOT));
        return input.substring(beg, pos);
    }

    private char peekChar() {
        return input.charAt(pos);
    }

    private char nextChar() {
        return input.charAt(pos++);
    }

    private boolean eatChar(char c) {
        if (peekChar() == c) {
            pos ++;
            return true;
        } else {
            return false;
        }
    }

    private void expectChar(char c) throws SignatureParserException {
        if (peekChar() == c) {
            pos ++;
        } else {
            throw new SignatureParserException(input, pos);
        }
    }
}
