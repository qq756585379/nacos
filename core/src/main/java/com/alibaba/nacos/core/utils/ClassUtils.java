
package com.alibaba.nacos.core.utils;

import org.springframework.core.ResolvableType;

import java.util.Objects;

@SuppressWarnings("all")
public final class ClassUtils {

    public static <T> Class<T> resolveGenericType(Class<?> declaredClass) {
        return (Class<T>) ResolvableType.forClass(declaredClass).getSuperType().resolveGeneric(0);
    }

    public static <T> Class<T> resolveGenericTypeByInterface(Class<?> declaredClass) {
        return (Class<T>) ResolvableType.forClass(declaredClass).getInterfaces()[0].resolveGeneric(0);
    }

    public static Class findClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("this class name not found");
        }
    }

    public static String getName(Object obj) {
        Objects.requireNonNull(obj, "obj");
        return obj.getClass().getName();
    }

    public static String getCanonicalName(Object obj) {
        Objects.requireNonNull(obj, "obj");
        return obj.getClass().getCanonicalName();
    }

    public static String getSimplaName(Object obj) {
        Objects.requireNonNull(obj, "obj");
        return obj.getClass().getSimpleName();
    }

    public static String getName(Class cls) {
        Objects.requireNonNull(cls, "cls");
        return cls.getName();
    }

    public static String getCanonicalName(Class cls) {
        Objects.requireNonNull(cls, "cls");
        return cls.getCanonicalName();
    }

    public static String getSimplaName(Class cls) {
        Objects.requireNonNull(cls, "cls");
        return cls.getSimpleName();
    }
}
