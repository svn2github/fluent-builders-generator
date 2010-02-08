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

public class SignatureParserException extends Exception {
    private static final long serialVersionUID = 8662714979833291087L;

    private final String signature;
    private final int pos;

    public SignatureParserException(String aSignature, int aPosition) {
        super("Syntax error in signature: \"" + aSignature + "\" at position " + aPosition);
        signature = aSignature;
        pos = aPosition;
    }

    public String getSignature() {
        return signature;
    }

    public int getPosition() {
        return pos;
    }
}
