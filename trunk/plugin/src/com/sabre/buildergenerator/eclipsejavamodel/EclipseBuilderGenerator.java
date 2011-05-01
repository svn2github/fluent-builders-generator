package com.sabre.buildergenerator.eclipsejavamodel;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.javamodel.IModelHelper;
import com.sabre.buildergenerator.javamodel.ISignatureResolver;
import com.sabre.buildergenerator.javamodel.ITypeAccessor;
import com.sabre.buildergenerator.javamodel.ITypeResolver;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;
import com.sabre.buildergenerator.sourcegenerator.MarkedFields;

public class EclipseBuilderGenerator extends BuilderGenerator<IType, ITypeParameter, IMethod, JavaModelException> {
    public EclipseBuilderGenerator() {
        ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException> typeAccessor = new TypeAccessor();
        MarkedFields<IType, ITypeParameter, IMethod, JavaModelException> typesAndFieldsToGenerate = new MarkedFields<IType, ITypeParameter, IMethod, JavaModelException>();
        IModelHelper<IType, IMethod, JavaModelException> typeHelper = new ModelHelper();
        ISignatureResolver<IType, JavaModelException> signatureResolver = new SignatureResolver();
        ITypeResolver<IType, JavaModelException> typeResolver = new TypeResolver();
        typesAndFieldsToGenerate.setTypeAccessor(typeAccessor);
        setTypeAccessor(typeAccessor);
        setTypeHelper(typeHelper);
        setTypeResolver(typeResolver);
        setSignatureResolver(signatureResolver);
        setTypesAndFieldsToGenerate(typesAndFieldsToGenerate);
    }
}
