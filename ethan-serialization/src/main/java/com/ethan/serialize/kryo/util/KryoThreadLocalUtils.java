package com.ethan.serialize.kryo.util;

import com.esotericsoftware.kryo.Kryo;

/**
 * Utility classes to ensure Kryo thread safety.
 *
 * @author Huang Z.Y.
 */
public class KryoThreadLocalUtils {

    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // Register necessary bean
        return kryo;
    });

    public static Kryo getKryo() {
        return KRYO_THREAD_LOCAL.get();
    }

    public static void releaseKryo() {
        KRYO_THREAD_LOCAL.remove();
    }

}
