package com.sabre.buildergenerator.javamodel.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.sabre.buildergenerator.javamodel.IModelHelper.ITypeInspector;

public class Main {
    public static class MyGeneric<T, U> {
        private T field;

        public T getField() {
            return field;
        }

        public void setField(T field) {
            this.field = field;
        }
    }

    public static class MyClass extends MyGeneric<String, Integer> {}

    public static void main(String[] args) throws Exception {
        new ReflectionModelHelper().walkHierarchyTree(MyClass.class, new ITypeInspector<Type>() {
            public void nextSuperType(String fullSignature, Type superType, Map<String, String> parameterSubstitution)
                    throws Exception {
                Class<?> superTypeClass = null;
                if (superType instanceof Class) {
                    superTypeClass = (Class<?>) superType;
                } else if (superType instanceof ParameterizedType) {
                    superTypeClass = (Class<?>) ((ParameterizedType) superType).getRawType();
                }
                System.out.println(superTypeClass.getName());
                for (Entry<String, String> entry : parameterSubstitution.entrySet()) {
                    System.out.print("  ");
                    System.out.print(entry.getKey());
                    System.out.print(":");
                    System.out.println(entry.getValue());
                }
            }

        });
    }
}
