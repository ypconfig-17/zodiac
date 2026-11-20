package com.tcsl.zodiac.rpc.netty;

import com.tcsl.zodiac.rpc.core.RpcRequest;
import com.tcsl.zodiac.rpc.core.RpcResponse;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @className: ExchangeFuture
 * @description:
 * @author: yang pan
 */
public class FutureRpcResponse implements Future<RpcResponse> {

    private static Logger logger = LoggerFactory.getLogger(FutureRpcResponse.class);

    private static Map<String, FutureRpcResponse> workingFutureMap = new ConcurrentHashMap<>();

    private RpcRequest rpcRequest;
    private final long createdTime;
    private Promise<RpcResponse> promise;

    public static FutureRpcResponse createExchangeFuture(RpcRequest request) {
        String requestId = request.getRequestId();
        return workingFutureMap.computeIfAbsent(requestId, id -> new FutureRpcResponse(request));
    }
    public static FutureRpcResponse removeExchangeFuture(String requestId) {
        return workingFutureMap.remove(requestId);
    }

    public FutureRpcResponse(RpcRequest rpcRequest) {
        this.createdTime = System.currentTimeMillis();
        this.rpcRequest = rpcRequest;
        promise = new DefaultPromise<>(new DefaultEventLoop());
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        return  promise.get();
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout,unit);
    }

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public Promise getPromise() {
        return promise;
    }

    public void setPromise(Promise promise) {
        this.promise = promise;
    }
}
