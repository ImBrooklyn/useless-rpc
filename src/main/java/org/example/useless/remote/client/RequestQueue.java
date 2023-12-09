package org.example.useless.remote.client;

import lombok.extern.slf4j.Slf4j;
import org.example.useless.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ImBrooklyn
 * @since 20/08/2023
 */
@Slf4j
public class RequestQueue {

    private RequestQueue() {
    }

    private static final RequestQueue INSTANCE = new RequestQueue();

    public static RequestQueue getInstance() {
        return INSTANCE;
    }

    private final Map<String, CompletableFuture<RpcResponse<?>>> MAP = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<?>> future) {
        MAP.put(requestId, future);
    }

    public void complete(RpcResponse<?> response) {
        CompletableFuture<RpcResponse<?>> future = MAP.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        } else {
            log.error("unrecognized request, requestId: {}", response.getRequestId());
            throw new IllegalStateException("unrecognized request");
        }
    }
}
