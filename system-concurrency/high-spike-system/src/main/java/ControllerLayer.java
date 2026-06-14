
// @RestController
public class ControllerLayer {

    // 将商品数据先加载到缓存中
    // @PostConstruct
    public void init() {
        // stringRedisTemplate.opsForValue().set("stock", String.valueOf(stock));
    }

    // @PostMapping("/{productId}")
    public String secondKill(Long productId) {
        // TODO: 使用Redis构建一级缓存
        // stringRedisTemplate.opsForValue().decrement() 原子操作，线程安全(单线程模型)
        // if(stock < 0) {
        //    TODO: 如果stock减低后为负数则必须还原，否则后面由于异常恢复的stock卖不出去(导致少买商品)
        //    stringRedisTemplate.opsForValue().increment() 原子操作
        //    return; 直接在缓存中判断，是否售完
        // }

        // TODO: 后面的业务逻辑不一定成功，导致上面缓存数据和数据库中数据不一致
        ServiceLayer.secondKillService(productId);

        // 在后面抛出异常的位置进行数据恢复
        // stringRedisTemplate.opsForValue().increment()
        return "success";
    }
}
