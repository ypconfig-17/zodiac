package com.tcsl.zodiac.server.core.store;

/**
 * @className: JraftStroeFactory
 * @description:
 * @author: yang pan
 */
public class JraftStroeFactory {


    public static JraftStroe createLibraStore(StoreType type) {

        JraftStroe jraftStroe;

        switch (type) {
            case MEMORY:
                jraftStroe = new MemoryDBStroe();
                break;
            case ROCKS:
                jraftStroe = null;
                break;
            default:
                throw new RuntimeException("不支持的类型");
        }

        return jraftStroe;
    }
}
