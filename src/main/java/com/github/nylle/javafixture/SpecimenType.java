package com.github.nylle.javafixture;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class SpecimenType<T> extends TypeCapture<T> {

    private final Type type;

    private SpecimenType(final Type type) {
        this.type = type;
    }

    protected SpecimenType() {
        this.type = capture();
    }

    public static <T> SpecimenType<T> fromClass(final Type typeReference) {
        return new SpecimenType<>(typeReference);
    }

    public static <T> SpecimenType<T> fromRawType(final Class<?> rawType, final Type[] actualTypeArguments) {
        return new SpecimenType<>(TypeCapture.create(rawType, actualTypeArguments));
    }

    public static Class<?> castToClass(Type type) {
        if (type instanceof WildcardType) {
            return (Class<?>) Stream.of(((WildcardType) type).getUpperBounds())
                    .findFirst()
                    .orElse(Stream.of(((WildcardType) type).getLowerBounds())
                            .findFirst()
                            .orElse(Object.class));
        }

        if (isParameterized(type)) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }

        return (Class<?>) type;
    }

    public static boolean isParameterized(final Type type) {
        return type instanceof ParameterizedType && ((ParameterizedType) type).getActualTypeArguments().length > 0;
    }

    public Class<T> asClass() {
        return (Class<T>) castToClass(type);
    }

    public ParameterizedType asParameterizedType() {
        if (isParameterized()) {
            return (ParameterizedType) type;
        }

        throw new SpecimenTypeException(format("%s is not a ParameterizedType", type));
    }

    public Type[] getGenericTypeArguments() {
        if (isParameterized()) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }

        throw new SpecimenTypeException(format("%s is not a ParameterizedType", type));
    }

    public Type getGenericTypeArgument(final int index) {
        return getGenericTypeArguments()[index];
    }

    public String[] getTypeParameterNames() {
        if (isParameterized()) {
            return Stream.of(asClass().getTypeParameters()).map(x -> x.getName()).toArray(size -> new String[size]);
        }

        throw new SpecimenTypeException(format("%s is not a ParameterizedType", type));
    }

    public String getTypeParameterName(final int index) {
        return getTypeParameterNames()[index];
    }

    public Class<?> getComponentType() {
        if (isArray()) {
            return asClass().getComponentType();
        }

        throw new SpecimenTypeException(format("%s is not an array", type));
    }

    public T[] getEnumConstants() {
        if (isEnum()) {
            return asClass().getEnumConstants();
        }

        throw new SpecimenTypeException(format("%s is not an enum", type));
    }

    public String getName() {
        if (isParameterized()) {
            return asParameterizedType().getTypeName();
        }

        return asClass().getName();
    }

    public boolean isParameterized() {
        return isParameterized(type);
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(asClass());
    }

    public boolean isMap() {
        return Map.class.isAssignableFrom(asClass());
    }

    public boolean isTimeType() {
        if (Temporal.class.isAssignableFrom(asClass())) {
            return true;
        }

        if (TemporalAdjuster.class.isAssignableFrom(asClass())) {
            return true;
        }

        if (TemporalAmount.class.isAssignableFrom(asClass())) {
            return true;
        }

        if (asClass().equals(ZoneId.class)) {
            return true;
        }

        if (asClass().equals(java.util.Date.class)) {
            return true;
        }

        if (asClass().equals(java.sql.Date.class)) {
            return true;
        }

        return false;
    }

    public boolean isSpecialType() {
        if (asClass().equals(java.math.BigInteger.class)) {
            return true;
        }
        if (asClass().equals(java.math.BigDecimal.class)) {
            return true;
        }
        if (asClass().equals(java.io.File.class)) {
            return true;
        }
        if (asClass().equals(java.net.URI.class)) {
            return true;
        }
        return false;
    }

    public boolean isPrimitive() {
        return asClass().isPrimitive();
    }

    public boolean isBoxed() {
        return asClass() == Double.class || asClass() == Float.class || asClass() == Long.class
                || asClass() == Integer.class || asClass() == Short.class || asClass() == Character.class
                || asClass() == Byte.class || asClass() == Boolean.class;
    }

    public boolean isEnum() {
        return asClass().isEnum();
    }

    public boolean isArray() {
        return asClass().isArray();
    }

    public boolean isInterface() {
        return asClass().isInterface();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(asClass().getModifiers());
    }

    public List<Constructor<T>> getDeclaredConstructors() {
        return Stream.of(this.asClass().getDeclaredConstructors()).map(x -> (Constructor<T>) x).collect(toList());
    }

    public List<Method> getFactoryMethods() {
        return Stream.of(this.asClass().getDeclaredMethods())
                .filter(x -> Modifier.isStatic(x.getModifiers()))
                .filter(x -> x.getReturnType().equals(this.asClass()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof SpecimenType<?>) {
            SpecimenType<?> that = (SpecimenType<?>) o;
            return this.type.equals(that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
