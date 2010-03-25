package com.sabre.buildergenerator.ui;

import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class RootTypeNode extends TypeNode {

	public RootTypeNode(IType type, Set<IMethod> definedSettingMethods) {
		super(type, definedSettingMethods);
	}

	@Override
	public boolean isActive() {
		return true;
	}

}
