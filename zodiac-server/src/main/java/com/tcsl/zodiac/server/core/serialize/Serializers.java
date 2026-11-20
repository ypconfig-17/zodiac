package com.tcsl.zodiac.server.core.serialize;


/**
 * @className: Serializers
 * @description:
 * @author: yang pan
 * @create: 2021-08-09 11:46
 */
public class Serializers {

    public static final byte  PROTO_STUFF = 0x01;

    private static Serializer[] serializers = new  Serializer[2];

    static {
        addSerializer(PROTO_STUFF, new ProtoStuffSerializer());
    }

    public static Serializer getSerializer(final int idx) {
        return serializers[idx];
    }

    public static Serializer getDefault() {
        return serializers[PROTO_STUFF];
    }

    private static void addSerializer(final int idx, final Serializer serializer) {
        if (serializers.length <= idx) {
            final Serializer[] newSerializers = new Serializer[idx + 5];
            System.arraycopy(serializers, 0, newSerializers, 0, serializers.length);
            serializers = newSerializers;
        }
        serializers[idx] = serializer;
    }

    private Serializers() {
    }
}
