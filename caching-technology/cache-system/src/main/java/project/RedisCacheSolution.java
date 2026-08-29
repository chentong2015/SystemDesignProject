package project;

import org.springframework.data.redis.core.StringRedisTemplate;
import project.model.Order;
import project.model.OrderService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// TODO: 分布式锁(互斥锁): 只有一个线程能够拿到，其他的线程阻塞
public class RedisCacheSolution {

    private BloomFilterCache bloomFilterCache;
    private StringRedisTemplate redisTemplate;

    // 此处使用JVM级别锁来测试 //
    private final Lock distributedLock = new ReentrantLock();

    public String findOrder4(int id) {
        // 1. 判断不隆过滤器中是否存在
        if (!bloomFilterCache.getBloomFilter().isExist(id)) {
            return "can not find in the bloom filter";
        }

        // 2. 查询缓存数据
        Object cacheOrder = redisTemplate.opsForValue().get(String.valueOf(id));
        if (cacheOrder != null) {
            return "find order in cache";
        }

        // 3. 使用互斥锁
        distributedLock.lock();
        try {
            // 双重检测: 拿到锁之后，需要再次查询缓存，缓存可能被别的线程所修改，避免直接查询数据库
            cacheOrder = redisTemplate.opsForValue().get(String.valueOf(id));
            if (cacheOrder != null) {
                return "find order in cache";
            }

            Order order = OrderService.selectOrderById(id);
            if (order != null) {
                redisTemplate.opsForValue().set(String.valueOf(id), order.getName(), 10, TimeUnit.MINUTES);
                return "get order from db";
            }
        } finally {
            distributedLock.unlock();
        }
        return "find nothing";
    }
}
