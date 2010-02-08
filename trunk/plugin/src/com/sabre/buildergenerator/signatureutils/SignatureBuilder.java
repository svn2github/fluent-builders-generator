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

import org.eclipse.jdt.core.Signature;

public class SignatureBuilder implements SignatureHandler {
    StringBuilder out;

    public SignatureBuilder(StringBuilder aOut) {
        out = aOut;
    }

    public void simpleType(char type) throws ExceptionWrapper {
        out.append(type);
    }

    public void startResolvedType(String identifier) throws ExceptionWrapper {
        out.append(Signature.C_RESOLVED);
        out.append(identifier);
    }

    public void startUnresolvedType(String identifier) throws ExceptionWrapper {
        out.append(Signature.C_UNRESOLVED);
        out.append(identifier);
    }

    public void startTypeArguments() throws ExceptionWrapper {
        out.append(Signature.C_GENERIC_START);
    }

    public void endTypeArguments() throws ExceptionWrapper {
        out.append(Signature.C_GENERIC_END);
    }

    public void endType() throws ExceptionWrapper {
        out.append(Signature.C_NAME_END);
    }

    public void typeVariable(String identifier) throws ExceptionWrapper {
        out.append(Signature.C_TYPE_VARIABLE);
        out.append(identifier);
    }

    public void wildcardAny() throws ExceptionWrapper {
        out.append(Signature.C_STAR);
    }

    public void wildcardExtends() throws ExceptionWrapper {
        out.append(Signature.C_EXTENDS);
    }

    public void wildcardSuper() throws ExceptionWrapper {
        out.append(Signature.C_SUPER);
    }

    public void array() throws ExceptionWrapper {
        out.append(Signature.C_ARRAY);
    }

    public void captureOf() throws ExceptionWrapper {
        out.append(Signature.C_CAPTURE);
    }

    public void innerType(String identifier) throws ExceptionWrapper {
        out.append(Signature.C_DOT);
        out.append(identifier);
    }

    public static void main(String[] args) throws ExceptionWrapper, SignatureParserException {
        StringBuilder out = new StringBuilder();
        SignatureParser parser = new SignatureParser("QMap<QMap<+QString;+QString;>;QMap<+QString;+QString;>;>;", new SignatureBuilder(out));
        parser.parse();
        System.out.println(out.toString());
//        System.out.println(resolveSignature("QString;"));
//        System.out.println(resolveSignature("QMap<QString;>;"));
//        System.out.println(resolveSignature("QMap<+QString;>;"));
//        System.out.println(resolveSignature("QMap<+QString;+QString;>;"));
//        System.out.println(resolveSignature("QMap<QMap<+QString;+QString;>;QMap<+QString;+QString;>;>;"));
    }
}
