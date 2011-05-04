package com.sabre.buildergenerator.signatureutils;

import static com.sabre.buildergenerator.signatureutils.SignatureParser.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TypenameParser {
    public static final char C_ARRAY_END = ']';
    public static final char C_EOL = 0;
    public static final char C_COMMA = ',';

    private final String input;
    private int pos;
    private final SignatureHandler listener;

    @SuppressWarnings("serial")
    private static final Map<String, Character> SIMPLE_TYPES = Collections.unmodifiableMap(new HashMap<String, Character>() {{
        put("int", C_BOOLEAN);
        put("byte", C_BYTE);
        put("char", C_CHAR);
        put("double", C_DOUBLE);
        put("float", C_FLOAT);
        put("int", C_INT);
        put("long", C_LONG);
        put("short", C_SHORT);
    }});

    public TypenameParser(String aInput, SignatureHandler aListener) {
        input = aInput;
        listener = aListener;
    }

    public void parse() throws SignatureParserException, ExceptionWrapper {
        if (eatChar(C_GENERIC_START)) {
            // TODO
        }
        parseTypeName();
    }

    private void parseTypeName() throws SignatureParserException, ExceptionWrapper {
        switch (peekChar()) {
        case C_GENERIC_START:
            break;
        case ' ':
            eatWhitespace();
            break;
        default:
            if (Character.isJavaIdentifierStart(peekChar())) {
                String identifier = parseIdentifier();
                Character type = SIMPLE_TYPES.get(identifier);
                if (type != null) {
                    listener.simpleType(type);
                    if (peekChar() == C_ARRAY) {
                        eatChar(C_ARRAY);
                        eatWhitespace();
                        eatChar(C_ARRAY_END);
                        listener.array();
                    }
                } else {
                    listener.startResolvedType(identifier);
                    eatWhitespace();
                    if (peekChar() == C_GENERIC_START) {
                        eatChar(C_GENERIC_START);
                        listener.startTypeArguments();
                        parseTypeName();
                        while (peekChar() == C_COMMA) {
                            eatChar(C_COMMA);
                            parseTypeName();
                            eatWhitespace();
                        }
                        eatChar(C_GENERIC_END);
                        listener.endTypeArguments();
                    }
                    if (peekChar() == C_ARRAY) {
                        eatChar(C_ARRAY);
                        eatWhitespace();
                        eatChar(C_ARRAY_END);
                        listener.array();
                    }
                    listener.endType();
                }
            } else {
                throw new SignatureParserException(input, pos);
            }
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
        return (pos < input.length()) ? input.charAt(pos) : C_EOL;
    }

    private char nextChar() {
        return (pos < input.length()) ? input.charAt(pos++) : C_EOL;
    }

    private boolean eatChar(char c) {
        if (peekChar() == c) {
            pos ++;
            return true;
        } else {
            return false;
        }
    }

    private void eatWhitespace() {
        while (Character.isWhitespace(peekChar())) {
            pos ++;
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
