package com.sabre.buildergenerator.ui;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.eclipsejavamodel.ModelHelper.TypeMethods;
import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.ui.TypeHelperRouter.SetType;

public class TypeTree {

    private final Map<IType, TypeNode> typeNodes;
    private final TypeHelperRouter typeHelperRouter;
    private final Map<IType, TypeMethods> setterMethods;
    private final Set<IType> typesAlreadyProcessed;
    private RootTypeNode rootNode;

    /**
     * @param aType
     *            a root type
     * @param typeHelperRouter
     *            a helper router
     * @throws Exception
     *             an exception thrown sometimes
     */
    public TypeTree(IType aType, TypeHelperRouter typeHelperRouter) throws Exception {
        this.typeNodes = new LinkedHashMap<IType, TypeNode>();
        this.typeHelperRouter = typeHelperRouter;
        this.setterMethods = typeHelperRouter.findSetterMethods(aType);
        this.typesAlreadyProcessed = new HashSet<IType>();

        TypeMethods typeMethods = getSetterMethodsForType(aType);
        if (typeMethods != null) {
            rootNode = new RootTypeNode(aType, typeMethods.getMethods());
            processType(rootNode, typeMethods.getParameterSubstitution());

            for (TypeNode typeNode : typeNodes.values()) {
                for (MethodNode methodNode : typeNode.getMethodNodes()) {
                    SetType setType = typeHelperRouter.resolveSetterSetType(typeNode.getElement(),
                            methodNode.getElement(), typeMethods.getParameterSubstitution());
                    if (!setType.isSimpleType()) {
                        TypeNode setTypeNode = getNodeFor(setType.getType());
                        if (setTypeNode != null) {
                            setTypeNode.addPointingMethodNode(methodNode);
                            methodNode.setPointedTypeNode(setTypeNode);
                        }
                    }
                }
            }
        }
    }

    private TypeMethods getSetterMethodsForType(IType aType) {
        TypeMethods typeMethods = setterMethods.get(aType);
        return typeMethods;
    }

    private void processType(TypeNode typeNode, Map<String, String> parameterSubstitution)
            throws JavaModelException, SignatureParserException, Exception {
        typeNodes.put(typeNode.getElement(), typeNode);
        for (TreeNode<IMethod> setterNode : typeNode.getMethodNodes()) {
            SetType setType = typeHelperRouter.resolveSetterSetType(typeNode.getElement(),
                    setterNode.getElement(), parameterSubstitution);
            if (!setType.isSimpleType()) {
                IType setIType = setType.getType();
                TypeMethods newTypeMethods = getSetterMethodsForType(setIType);
                if (!typesAlreadyProcessed.contains(setIType) && newTypeMethods != null
                        && setType.getType().isClass() && !setIType.isBinary()) {
                    typesAlreadyProcessed.add(setIType);

                    Map<String, String> newParameterSubstitution = newTypeMethods
                            .getParameterSubstitution();
                    processType(createTypeNode(setIType), newParameterSubstitution);
                }
            }
        }
    }

    private TypeNode createTypeNode(IType setType) throws Exception {
        return new TypeNode(setType, getSetterMethodsForType(setType).getMethods());
    }

    TypeNode getNodeFor(IType aBaseType) {
        return typeNodes.get(aBaseType);
    }

    public TypeNode[] getSortedTypesNodes() {
        return typeNodes.values().toArray(new TypeNode[typeNodes.size()]);
    }

    public void populateStateChange() {
        makeAllNodesInvisible();
        rootNode.populateStateChange();
    }

    private void makeAllNodesInvisible() {
        for (TypeNode typeNode : typeNodes.values()) {
            typeNode.deactivate();
        }
    }

}
