package com.sabre.buildergenerator.ui;

import java.util.Collection;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class RootTypeNode extends TypeNode {

	public RootTypeNode(IType type, Collection<IMethod> definedSettingMethods) {
		super(type, definedSettingMethods);
	}

	@Override
	public boolean isActive() {
		return true;
	}

}
