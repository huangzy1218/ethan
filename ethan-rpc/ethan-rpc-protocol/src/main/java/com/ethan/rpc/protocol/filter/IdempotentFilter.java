package com.ethan.rpc.protocol.filter;

import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Result;
import com.ethan.rpc.RpcException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Huang Z.Y.
 */
public class IdempotentFilter implements Filter {

    // Redisson client instance
    private final RedissonClient redissonClient;

    /**
     * Local cache can also be used to avoid accessing Redis every time.
     */
    private static final Map<String, Result> localCache = new ConcurrentHashMap<>();

    public IdempotentFilter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // Get the unique RequestId from the request (the client needs to carry this ID in the request)
        String requestId = invocation.getAttachment(invocation.getId());
        if (requestId == null || requestId.isEmpty()) {
            throw new RpcException("RequestId is missing in invocation");
        }

        // If the local cache already contains the result of this request, return it directly
        if (localCache.containsKey(requestId)) {
            return localCache.get(requestId);
        }

        // Use Redisson distributed lock to ensure idempotency
        RLock lock = redissonClient.getLock("lock:request:" + requestId);

        try {
            // Try to acquire the lock with a wait time of 3 seconds and a lock time of 10 seconds
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    // Check if the processing result of this request already exists in Redis
                    Result result = getResultFromCache(requestId);
                    if (result != null) {
                        // If there is already a result in the Redis cache, return it directly
                        return result;
                    }

                    // If it has not been processed, execute the actual business logic
                    result = invoker.invoke(invocation);

                    // Store the result in Redis cache and local cache
                    saveResultToCache(requestId, result);
                    localCache.put(requestId, result);

                    return result;
                } finally {
                    lock.unlock(); // Ensure that the lock is eventually released
                }
            } else {
                throw new RpcException("Could not acquire lock for RequestId: " + requestId);
            }
        } catch (InterruptedException e) {
            throw new RpcException("Thread interrupted while trying to acquire lock", e);
        }
    }

    // Implementation to get the request result from Redis
    private Result getResultFromCache(String requestId) {
        // Read cache data from Redis through Redisson or RedisTemplate
        // Here we assume there is a simple Redis API to get the result
        return (Result) redissonClient.getBucket("cache:request:" + requestId).get();
    }

    // Implementation to store the processing result in Redis
    private void saveResultToCache(String requestId, Result result) {
        // Store data in Redis through Redisson or RedisTemplate
        redissonClient.getBucket("cache:request:" + requestId).set(result, 30, TimeUnit.MINUTES);
    }

}
    