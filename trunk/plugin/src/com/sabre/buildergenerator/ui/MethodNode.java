package com.sabre.buildergenerator.ui;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

public class MethodNode extends TreeNode<IMethod> {

    private TypeNode pointedTypeNode;

    private TypeNode parentTypeNode;

    /**
     * @param aElement
     *            a eclipse jdt model element
     * @param parentTypeNode
     *            owning type node
     */
    public MethodNode(IMethod aElement, TypeNode parentTypeNode) {
        super(aElement, null);

        this.parentTypeNode = parentTypeNode;
    }

    public TypeNode getPointedTypeNode() {
        return pointedTypeNode;
    }

    /**
     * @param aPackage
     *            a package string representation
     * @return is method accessible from the specified package
     */
    public boolean isAccessibleFromPackage(String aPackage) {
        if (!isPublic()) {
            if (!isPrivate()) {
                IPackageFragment owningClassPackage = getElement().getDeclaringType()
                        .getPackageFragment();
                return owningClassPackage.getElementName().equals(aPackage);
            } else {
                return false;
            }
        } else {
            return true;
        }

    }

    private boolean isPrivate() {
        try {
            return Flags.isPrivate(getElement().getFlags());
        } catch (JavaModelException exception) {
            throw new RuntimeException(exception);
        }
    }

    private boolean isPublic() {
        try {
            return Flags.isPublic(getElement().getFlags());
        } catch (JavaModelException exception) {
            throw new RuntimeException(exception);
        }
    }

    void setPointedTypeNode(TypeNode pointedTypeNode) {
        this.pointedTypeNode = pointedTypeNode;
    }

    @Override
    public String toString() {
        return parentTypeNode.toString() + "#" + getElement().getElementName();
    }

    public void setSelected(boolean b) {
        if (parentTypeNode != null && !parentTypeNode.isActive()) {
            throw new IllegalStateException("Can't select setter of inactive type");
        }

        super.setSelected(b);
    }

    public TypeNode getParentTypeNode() {
        return parentTypeNode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodNode) {
            MethodNode other = (MethodNode) obj;
            if (other.parentTypeNode.equals(parentTypeNode)) {
                return super.equals(obj);
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return parentTypeNode.hashCode()*31 + super.hashCode();
    }

}
