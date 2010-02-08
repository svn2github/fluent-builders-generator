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

public class ExceptionWrapper extends Exception {
    private static final long serialVersionUID = 1258103901536809908L;

    public ExceptionWrapper(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unchecked")
    public <T extends Throwable> ExceptionWrapper rethrow() throws T {
        T cause;
        try {
            cause = (T) getCause();
        } catch (ClassCastException e) {
            return this;
        }
        throw cause;
    }

    public void done() {
        throw new RuntimeException(getCause());
    }
}
