package com.sabre.buildergenerator.javamodel.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.sabre.buildergenerator.javamodel.IModelHelper;
import com.sabre.buildergenerator.javamodel.ISignatureResolver;
import com.sabre.buildergenerator.javamodel.ISignatureUtils;
import com.sabre.buildergenerator.javamodel.ITypeAccessor;
import com.sabre.buildergenerator.javamodel.ITypeResolver;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;
import com.sabre.buildergenerator.sourcegenerator.MarkedFields;

public class ReflectionBuilderGenerator extends BuilderGenerator<Type, TypeVariable<?>, Method, Exception> {
    public ReflectionBuilderGenerator() {
        ITypeAccessor<Type, TypeVariable<?>, Method, Exception> typeAccessor = new ReflectionTypeAccessor();
        MarkedFields<Type, TypeVariable<?>, Method, Exception> typesAndFieldsToGenerate = new MarkedFields<Type, TypeVariable<?>, Method, Exception>();
        IModelHelper<Type, Method, Exception> typeHelper = new ReflectionModelHelper();
        ISignatureResolver<Type, Exception> signatureResolver = new ReflectionSignatureResolver();
        ITypeResolver<Type, Exception> typeResolver = new ReflectionTypeResolver();
        typesAndFieldsToGenerate.setTypeAccessor(typeAccessor);
        ISignatureUtils signatureUtils = new SignatureUtils();
        typesAndFieldsToGenerate.setSignatureUtils(signatureUtils);
        setTypeAccessor(typeAccessor);
        setTypeHelper(typeHelper);
        setTypeResolver(typeResolver);
        setSignatureResolver(signatureResolver);
        setTypesAndFieldsToGenerate(typesAndFieldsToGenerate);
    }
}
