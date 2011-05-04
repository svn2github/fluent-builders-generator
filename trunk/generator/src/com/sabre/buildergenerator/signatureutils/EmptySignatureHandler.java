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

public class EmptySignatureHandler implements SignatureHandler {

    public void endType() throws ExceptionWrapper {
    }

    public void endTypeArguments() throws ExceptionWrapper {
    }

    public void simpleType(char type) throws ExceptionWrapper {
    }

    public void startResolvedType(String identifier) throws ExceptionWrapper {
    }

    public void startTypeArguments() throws ExceptionWrapper {
    }

    public void startUnresolvedType(String identifier) throws ExceptionWrapper {
    }

    public void typeVariable(String identifier) throws ExceptionWrapper {
    }

    public void wildcardAny() throws ExceptionWrapper {
    }

    public void wildcardExtends() throws ExceptionWrapper {
    }

    public void wildcardSuper() throws ExceptionWrapper {
    }

    public void array() throws ExceptionWrapper {
    }

    public void captureOf() throws ExceptionWrapper {
    }

    public void innerType(String identifier) throws ExceptionWrapper {
    }

}
