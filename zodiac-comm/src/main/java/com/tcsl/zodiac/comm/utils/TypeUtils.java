package com.tcsl.zodiac.comm.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @className: TypeUtils
 * @description:
 * @author: yang pan
 */
public class TypeUtils {

    /**
     * Create a parameterized type instance.
     *
     * @param raw           raw class
     * @param typeArguments the types used for parameterization
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType parameterize(final Class<?> raw, final Type... typeArguments) {
        checkParameterizeMethodParameter(raw, typeArguments);
        return new ParameterizedTypeImpl(raw, raw.getEnclosingClass(), typeArguments);
    }

    /**
     * Check parameterize method parameter.
     *
     * @param raw           raw class
     * @param typeArguments the types used for parameterization
     * @return {@link ParameterizedType}
     */
    private static void checkParameterizeMethodParameter(Class<?> raw, final Type... typeArguments) {
        if (raw == null) {
            throw new NullPointerException("raw cannot be null");
        }
        if (typeArguments == null) {
            throw new NullPointerException("typeArguments cannot be null");
        }
        if (typeArguments.length != raw.getTypeParameters().length) {
            throw new IllegalArgumentException(
                    String.format("invalid number of type parameters specified: expected %s, got %s",
                            raw.getTypeParameters().length, typeArguments.length));
        }

        for (int i = 0; i < typeArguments.length; i++) {
            if (typeArguments[i] == null) {
                throw new IllegalArgumentException("There can be no null in typeArguments");
            }
        }

    }

    /**
     * ParameterizedType implementation class.
     */
    private static final class ParameterizedTypeImpl implements ParameterizedType {

        /**
         * type.
         */
        private final Class<?> raw;

        /**
         * owner type to use, if any.
         */
        private final Type useOwner;

        /**
         * formal type arguments.typeArguments
         */
        private final Type[] typeArguments;

        private ParameterizedTypeImpl(final Class<?> raw, final Type useOwner, final Type[] typeArguments) {
            this.raw = raw;
            this.useOwner = useOwner;
            this.typeArguments = typeArguments;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return useOwner;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments.clone();
        }

        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();

            buf.append(raw.getName());
            buf.append('<');
            buf.append(typeArguments[0].getTypeName());
            for (int i = 1; i < typeArguments.length; i++) {
                buf.append(", ");
                buf.append(typeArguments[i].getTypeName());
            }
            buf.append('>');

            return buf.toString();
        }
    }
}
