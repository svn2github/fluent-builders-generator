package com.sabre.buildergenerator.sourcegenerator.java;

public class JavaSourceBuilder extends JavaSourceBuilderBase<JavaSourceBuilder> {
    public static JavaSourceBuilder javaSource() {
        return new JavaSourceBuilder();
    }

    public JavaSourceBuilder() {
        super(new com.sabre.buildergenerator.sourcegenerator.java.JavaSource());
    }

    public com.sabre.buildergenerator.sourcegenerator.java.JavaSource build() {
        return getInstance();
    }
}

@SuppressWarnings("unchecked")
class JavaSourceBuilderBase<GeneratorT extends JavaSourceBuilderBase> {
    private com.sabre.buildergenerator.sourcegenerator.java.JavaSource instance;

    protected JavaSourceBuilderBase(com.sabre.buildergenerator.sourcegenerator.java.JavaSource aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.sourcegenerator.java.JavaSource getInstance() {
        return instance;
    }

    public GeneratorT withPackge(java.lang.String aValue) {
        instance.setPackge(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withImports(java.util.List<java.lang.String> aValue) {
        instance.setImports(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withImport(java.lang.String aValue) {
        if (instance.getImports() == null) {
            instance.setImports(new java.util.ArrayList<java.lang.String>());
        }

        ((java.util.ArrayList<java.lang.String>) instance.getImports()).add(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withClazzes(java.util.List<com.sabre.buildergenerator.sourcegenerator.java.Clazz> aValue) {
        instance.setClazzes(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withClazz(com.sabre.buildergenerator.sourcegenerator.java.Clazz aValue) {
        if (instance.getClazzes() == null) {
            instance.setClazzes(new java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Clazz>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Clazz>) instance.getClazzes())
                .add(aValue);

        return (GeneratorT) this;
    }

    public ClazzClazzBuilder withClazz() {
        com.sabre.buildergenerator.sourcegenerator.java.Clazz clazz = new com.sabre.buildergenerator.sourcegenerator.java.Clazz();

        return withClazz(clazz).new ClazzClazzBuilder(clazz);
    }

    public class ClazzClazzBuilder extends ClazzBuilderBase<ClazzClazzBuilder> {
        public ClazzClazzBuilder(com.sabre.buildergenerator.sourcegenerator.java.Clazz aInstance) {
            super(aInstance);
        }

        public GeneratorT endClazz() {
            return (GeneratorT) JavaSourceBuilderBase.this;
        }
    }
}

@SuppressWarnings("unchecked")
class ClazzBuilderBase<GeneratorT extends ClazzBuilderBase> {
    private com.sabre.buildergenerator.sourcegenerator.java.Clazz instance;

    protected ClazzBuilderBase(com.sabre.buildergenerator.sourcegenerator.java.Clazz aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.sourcegenerator.java.Clazz getInstance() {
        return instance;
    }

    public GeneratorT withAnnotations(java.util.List<java.lang.String> aValue) {
        instance.setAnnotations(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withAnnotation(java.lang.String aValue) {
        if (instance.getAnnotations() == null) {
            instance.setAnnotations(new java.util.ArrayList<java.lang.String>());
        }

        ((java.util.ArrayList<java.lang.String>) instance.getAnnotations()).add(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withModifiers(int aValue) {
        instance.setModifiers(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withName(java.lang.String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withDeclarations(java.util.List<com.sabre.buildergenerator.sourcegenerator.java.Statement> aValue) {
        instance.setDeclarations(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withDeclaration(com.sabre.buildergenerator.sourcegenerator.java.Statement aValue) {
        if (instance.getDeclarations() == null) {
            instance
                    .setDeclarations(new java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Statement>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Statement>) instance.getDeclarations())
                .add(aValue);

        return (GeneratorT) this;
    }

    public DeclarationStatementBuilder withDeclaration() {
        com.sabre.buildergenerator.sourcegenerator.java.Statement declaration = new com.sabre.buildergenerator.sourcegenerator.java.Statement();

        return withDeclaration(declaration).new DeclarationStatementBuilder(declaration);
    }

    public class DeclarationStatementBuilder extends StatementBuilderBase<DeclarationStatementBuilder> {
        public DeclarationStatementBuilder(com.sabre.buildergenerator.sourcegenerator.java.Statement aInstance) {
            super(aInstance);
        }

        public GeneratorT endDeclaration() {
            return (GeneratorT) ClazzBuilderBase.this;
        }
    }

    public GeneratorT withMethods(java.util.List<com.sabre.buildergenerator.sourcegenerator.java.Method> aValue) {
        instance.setMethods(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withMethod(com.sabre.buildergenerator.sourcegenerator.java.Method aValue) {
        if (instance.getMethods() == null) {
            instance.setMethods(new java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Method>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Method>) instance.getMethods())
                .add(aValue);

        return (GeneratorT) this;
    }

    public MethodMethodBuilder withMethod() {
        com.sabre.buildergenerator.sourcegenerator.java.Method method = new com.sabre.buildergenerator.sourcegenerator.java.Method();

        return withMethod(method).new MethodMethodBuilder(method);
    }

    public class MethodMethodBuilder extends MethodBuilderBase<MethodMethodBuilder> {
        public MethodMethodBuilder(com.sabre.buildergenerator.sourcegenerator.java.Method aInstance) {
            super(aInstance);
        }

        public GeneratorT endMethod() {
            return (GeneratorT) ClazzBuilderBase.this;
        }
    }

    public GeneratorT withBaseClazz(java.lang.String aValue) {
        instance.setBaseClazz(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withInterfaces(java.util.List<java.lang.String> aValue) {
        instance.setInterfaces(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withInterface(java.lang.String aValue) {
        if (instance.getInterfaces() == null) {
            instance.setInterfaces(new java.util.ArrayList<java.lang.String>());
        }

        ((java.util.ArrayList<java.lang.String>) instance.getInterfaces()).add(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withTypeArgs(java.util.List<java.lang.String> aValue) {
        instance.setTypeArgs(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withTypeArg(java.lang.String aValue) {
        if (instance.getTypeArgs() == null) {
            instance.setTypeArgs(new java.util.ArrayList<java.lang.String>());
        }

        ((java.util.ArrayList<java.lang.String>) instance.getTypeArgs()).add(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withInnerClasses(java.util.List<com.sabre.buildergenerator.sourcegenerator.java.Clazz> aValue) {
        instance.setInnerClasses(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withInnerClass(com.sabre.buildergenerator.sourcegenerator.java.Clazz aValue) {
        if (instance.getInnerClasses() == null) {
            instance.setInnerClasses(new java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Clazz>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Clazz>) instance.getInnerClasses())
                .add(aValue);

        return (GeneratorT) this;
    }

    public InnerClassClazzBuilder withInnerClass() {
        com.sabre.buildergenerator.sourcegenerator.java.Clazz innerClass = new com.sabre.buildergenerator.sourcegenerator.java.Clazz();

        return withInnerClass(innerClass).new InnerClassClazzBuilder(innerClass);
    }

    public class InnerClassClazzBuilder extends ClazzBuilderBase<InnerClassClazzBuilder> {
        public InnerClassClazzBuilder(com.sabre.buildergenerator.sourcegenerator.java.Clazz aInstance) {
            super(aInstance);
        }

        public GeneratorT endInnerClass() {
            return (GeneratorT) ClazzBuilderBase.this;
        }
    }
}

@SuppressWarnings("unchecked")
class StatementBuilderBase<GeneratorT extends StatementBuilderBase> {
    private com.sabre.buildergenerator.sourcegenerator.java.Statement instance;

    protected StatementBuilderBase(com.sabre.buildergenerator.sourcegenerator.java.Statement aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.sourcegenerator.java.Statement getInstance() {
        return instance;
    }

    public GeneratorT withStatement(java.lang.String aValue) {
        instance.setStatement(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withParams(java.util.List<java.lang.Object> aValue) {
        instance.setParams(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withParam(java.lang.Object aValue) {
        if (instance.getParams() == null) {
            instance.setParams(new java.util.ArrayList<java.lang.Object>());
        }

        ((java.util.ArrayList<java.lang.Object>) instance.getParams()).add(aValue);

        return (GeneratorT) this;
    }
}

@SuppressWarnings("unchecked")
class MethodBuilderBase<GeneratorT extends MethodBuilderBase> {
    private com.sabre.buildergenerator.sourcegenerator.java.Method instance;

    protected MethodBuilderBase(com.sabre.buildergenerator.sourcegenerator.java.Method aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.sourcegenerator.java.Method getInstance() {
        return instance;
    }

    public GeneratorT withModifiers(int aValue) {
        instance.setModifiers(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withReturnType(java.lang.String aValue) {
        instance.setReturnType(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withName(java.lang.String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withParameters(
            java.util.List<com.sabre.buildergenerator.sourcegenerator.java.MethodParameter> aValue) {
        instance.setParameters(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withParameter(com.sabre.buildergenerator.sourcegenerator.java.MethodParameter aValue) {
        if (instance.getParameters() == null) {
            instance
                    .setParameters(new java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.MethodParameter>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.MethodParameter>) instance
                .getParameters()).add(aValue);

        return (GeneratorT) this;
    }

    public ParameterMethodParameterBuilder withParameter() {
        com.sabre.buildergenerator.sourcegenerator.java.MethodParameter parameter = new com.sabre.buildergenerator.sourcegenerator.java.MethodParameter();

        return withParameter(parameter).new ParameterMethodParameterBuilder(parameter);
    }

    public class ParameterMethodParameterBuilder extends MethodParameterBuilderBase<ParameterMethodParameterBuilder> {
        public ParameterMethodParameterBuilder(com.sabre.buildergenerator.sourcegenerator.java.MethodParameter aInstance) {
            super(aInstance);
        }

        public GeneratorT endParameter() {
            return (GeneratorT) MethodBuilderBase.this;
        }
    }

    public GeneratorT withExceptions(java.util.List<java.lang.String> aValue) {
        instance.setExceptions(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withException(java.lang.String aValue) {
        if (instance.getExceptions() == null) {
            instance.setExceptions(new java.util.ArrayList<java.lang.String>());
        }

        ((java.util.ArrayList<java.lang.String>) instance.getExceptions()).add(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withInstructions(java.util.List<com.sabre.buildergenerator.sourcegenerator.java.Statement> aValue) {
        instance.setInstructions(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withInstruction(com.sabre.buildergenerator.sourcegenerator.java.Statement aValue) {
        if (instance.getInstructions() == null) {
            instance
                    .setInstructions(new java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Statement>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.sourcegenerator.java.Statement>) instance.getInstructions())
                .add(aValue);

        return (GeneratorT) this;
    }

    public InstructionStatementBuilder withInstruction() {
        com.sabre.buildergenerator.sourcegenerator.java.Statement instruction = new com.sabre.buildergenerator.sourcegenerator.java.Statement();

        return withInstruction(instruction).new InstructionStatementBuilder(instruction);
    }

    public class InstructionStatementBuilder extends StatementBuilderBase<InstructionStatementBuilder> {
        public InstructionStatementBuilder(com.sabre.buildergenerator.sourcegenerator.java.Statement aInstance) {
            super(aInstance);
        }

        public GeneratorT endInstruction() {
            return (GeneratorT) MethodBuilderBase.this;
        }
    }

    public GeneratorT withReturnValue(com.sabre.buildergenerator.sourcegenerator.java.Statement aValue) {
        instance.setReturnValue(aValue);

        return (GeneratorT) this;
    }

    public ReturnValueStatementBuilder withReturnValue() {
        com.sabre.buildergenerator.sourcegenerator.java.Statement returnValue = new com.sabre.buildergenerator.sourcegenerator.java.Statement();

        return withReturnValue(returnValue).new ReturnValueStatementBuilder(returnValue);
    }

    public class ReturnValueStatementBuilder extends StatementBuilderBase<ReturnValueStatementBuilder> {
        public ReturnValueStatementBuilder(com.sabre.buildergenerator.sourcegenerator.java.Statement aInstance) {
            super(aInstance);
        }

        public GeneratorT endReturnValue() {
            return (GeneratorT) MethodBuilderBase.this;
        }
    }
}

@SuppressWarnings("unchecked")
class MethodParameterBuilderBase<GeneratorT extends MethodParameterBuilderBase> {
    private com.sabre.buildergenerator.sourcegenerator.java.MethodParameter instance;

    protected MethodParameterBuilderBase(com.sabre.buildergenerator.sourcegenerator.java.MethodParameter aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.sourcegenerator.java.MethodParameter getInstance() {
        return instance;
    }

    public GeneratorT withType(java.lang.String aValue) {
        instance.setType(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withName(java.lang.String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }
}
