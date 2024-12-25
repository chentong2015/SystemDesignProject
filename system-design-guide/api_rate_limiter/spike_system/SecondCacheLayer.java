import java.util.concurrent.ConcurrentHashMap;

// TODO: 直接在JVM级别构建二级缓存，直接走内存
//  - 避免JVM <-> Redis造成的网络开销
//  - 二级缓存能够进一步削减发送到DB服务器的请求数量
public class SecondCacheLayer {

    // TODO: JVM级别的缓存无法解决分布式锁的问题，必须要在集群中同步
    private boolean isSoldOut = false; // JVM级别的标记，内存对象
    private final ConcurrentHashMap<Long, Boolean> productSoldOutMap = new ConcurrentHashMap<>();

    // 该方法被回调执行，可以删除之前设置的标记，清除JVM级别的缓存
    public void removeProductSoldOutFlag(Long productId) {
        if (productSoldOutMap.get(productId))
            productSoldOutMap.remove(productId);
    }

    public void secondKillPlus() {
        // 如果已经卖完，则直接返回，避免操作Redis(一级缓存的逻辑)
        if (!isSoldOut) {
            // stringRedisTemplate.opsForValue().decrement() 原子操作，线程安全(单线程模型)
            // if(stock < 0) {
            //    isSoldOut = true;
            //    stringRedisTemplate.opsForValue().increment();
            //    设置zookeeper的标记，同时设置客户端监听效果
            //    return;
            // }
        }

        // 如果后面没有成功操作，必须还原标记
        isSoldOut = false;

        // TODO: 通过Zookeeper实时同步集群JVM缓存，分布式集群中任何一个结点都能更新缓存标记
        // if (zooKeeper.exists(zkProductPath, true) == null) {
        //      修改zooKeeper中path下结点值，通知所有监听的客户端
        //      zooKeeper.create(zkProductPath, "false".getBytes(), ...);
        // }
        return;
    }
}
