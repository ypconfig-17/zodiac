package com.tcsl.zodiac.server.core.store;



import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.errors.Errors;
import com.alipay.sofa.jraft.rhea.storage.KVStoreClosure;
import com.google.common.collect.Lists;
import com.tcsl.zodiac.server.core.jraft.JraftClosure;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @className: MemoryDBStroe
 * @description:
 * @author: yang pan
 */
public class MemoryDBStroe implements JraftStroe {


    private final ConcurrentMap<String,String> repository = new ConcurrentHashMap<>();

    public ConcurrentMap<String, String> getRepository() {
        return repository;
    }


    @Override
    public void delete(String key, JraftClosure closure) {
       repository.remove(key);
        setSuccess(closure,Boolean.TRUE);
    }

    @Override
    public void add(String key,String value, JraftClosure closure) {
        repository.put(key,value);
        setSuccess(closure,Boolean.TRUE);
    }

    @Override
    public void get(String key, JraftClosure closure, boolean readOnlySafe) {
        String value = repository.get(key);
        setSuccess(closure,value);
    }

    @Override
    public void get(String key, JraftClosure closure) {
        String value = repository.get(key);
        setSuccess(closure,value);
    }

    @Override
    public void allKeys(JraftClosure closure, boolean readOnlySafe) {
        allKeys(closure);
    }

    @Override
    public void allKeys(JraftClosure closure) {
        Set<String> keySet = repository.keySet();
        setSuccess(closure,keySet);
    }

    @Override
    public void getAll(JraftClosure closure) {
        Collection<String> values = repository.values();
        List<String> list = Lists.newArrayList(values);
        setSuccess(closure,list);
    }

    @Override
    public void getAll(JraftClosure closure, boolean readOnlySafe) {
        getAll(closure);
    }


    static void setSuccess(final JraftClosure closure, final Object data) {
        if (closure != null) {
            // closure is null on follower node
            closure.setData(data);
            closure.run(Status.OK());
        }
    }

    static void setFailure(final KVStoreClosure closure, final String message) {
        if (closure != null) {
            // closure is null on follower node
            closure.setError(Errors.STORAGE_ERROR);
            closure.run(new Status(RaftError.EIO, message));
        }
    }














    public static class Persistence<T> {

        private final T data;

        public Persistence(T data) {
            this.data = data;
        }

        public T data() {
            return data;
        }
    }

    public  static class defaultDb extends Persistence<Map<String,String>> {

        public defaultDb(Map<String, String> data) {
            super(data);
        }
    }

    public static class TailIndex extends Persistence<Integer> {

        public TailIndex(Integer data) {
            super(data);
        }
    }

}

