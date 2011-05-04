package com.sabre.buildergenerator.javamodel;

import java.util.Map;

import com.sabre.buildergenerator.signatureutils.SignatureParserException;

public interface ISignatureResolver<IType, JavaModelException extends Exception> {

    /**
     * QMyType&lt;QMyClss;QString;&gt;; --&gt; Qmypackage.MyType&lt;Qmylib.MyClss;Qjava.lang.String;&gt;;
     * @param owningType context for the resolution
     * @param signature unresolved signature
     * @return resolved signature
     * @throws SignatureParserException
     * @throws JavaModelException
     */
    public abstract String resolveSignature(final IType owningType, String signature) throws SignatureParserException,
            JavaModelException;

    /**
     * @param owningType
     * @param signature
     * @return
     * @throws SignatureParserException
     * @throws JavaModelException
     */
    public abstract IType resolveType(final IType owningType, String signature) throws JavaModelException,
            SignatureParserException;

    /**
     * @param owningType
     * @param typeSignature
     * @param typeParameterMapping
     * @return
     * @throws SignatureParserException
     * @throws JavaModelException
     */
    public abstract String resolveTypeWithParameterMapping(IType owningType, String typeSignature,
            Map<String, String> typeParameterMapping) throws JavaModelException, SignatureParserException;

    /**
     * QList&lt;QString;&gt;; --> QList;
     * @param signature signature
     * @return signature for type erasure
     */
    public abstract String getTypeErasure(String signature);

    public abstract String[] getTypeArguments(String resolvedFieldTypeSignature);

    public abstract String createTypeSignature(String typeQName);
}
