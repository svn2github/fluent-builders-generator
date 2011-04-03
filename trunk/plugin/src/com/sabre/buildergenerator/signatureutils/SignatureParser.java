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

package com.sabre.buildergenerator.signatureutils;

public class SignatureParser {
    public static final char C_BOOLEAN = 'Z';
    public static final char C_BYTE = 'B';
    public static final char C_CHAR = 'C';
    public static final char C_DOUBLE = 'D';
    public static final char C_FLOAT = 'F';
    public static final char C_INT = 'I';
    public static final char C_SEMICOLON = ';';
    public static final char C_COLON = ':';
    public static final char C_LONG = 'J';
    public static final char C_SHORT = 'S';
    public static final char C_VOID = 'V';
    public static final char C_TYPE_VARIABLE = 'T';
    public static final char C_STAR = '*';
    public static final char C_EXCEPTION_START = '^';
    public static final char C_EXTENDS = '+';
    public static final char C_SUPER = '-';
    public static final char C_DOT = '.';
    public static final char C_SLASH = '/';
    public static final char C_DOLLAR = '$';
    public static final char C_ARRAY = '[';
    public static final char C_RESOLVED = 'L';
    public static final char C_UNRESOLVED = 'Q';
    public static final char C_NAME_END = ';';
    public static final char C_PARAM_START = '(';
    public static final char C_PARAM_END = ')';
    public static final char C_GENERIC_START = '<';
    public static final char C_GENERIC_END = '>';
    public static final char C_CAPTURE = '!';

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
            parseTypeArgument();
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
        case C_STAR:
        case C_EXTENDS:
        case C_SUPER:
            nextChar();
            break;
        default:
            throw new SignatureParserException(input, pos);
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
