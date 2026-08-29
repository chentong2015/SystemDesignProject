package project;

import project.bloom_filter.CustomBloomFilter;
import project.model.Order;

import java.util.List;

// TODO. 查询时先判断布隆过滤器中是否存在，解决缓存穿透(存在容错概率)
public class BloomFilterCache {

    private final CustomBloomFilter bloomFilter;

    // 在启动时将需要查询的表格中的id字段添加到BloomFilter中
    public BloomFilterCache(List<Order> orders) {
        this.bloomFilter = new CustomBloomFilter();
        for (Order order : orders) {
            this.bloomFilter.put(order.getId());
        }
    }

    public String findOrder(int id) {
        if (!bloomFilter.isExist(id)) {
            return "can not find in the bloom filter";
        }
        return "find nothing";
    }

    public CustomBloomFilter getBloomFilter() {
        return bloomFilter;
    }
}
