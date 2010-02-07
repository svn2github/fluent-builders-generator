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
