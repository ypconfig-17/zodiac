package com.tcsl.zodiac.server.core.store;

import com.tcsl.zodiac.server.core.jraft.JraftClosure;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;

import java.util.Set;

/**
 * @className: JraftStroe
 * @description:
 * @author: yang pan
 */
public interface JraftStroe {

    void delete(String key, JraftClosure closure);

    void add(String key, String value, JraftClosure closure);

    void get(String key, JraftClosure closure,boolean readOnlySafe);

    void get(String key, JraftClosure closure);

    void allKeys( JraftClosure closure,boolean readOnlySafe);

    void allKeys(JraftClosure closure);

    void getAll(JraftClosure closure);
    void getAll(JraftClosure closure,boolean readOnlySafe);
}

