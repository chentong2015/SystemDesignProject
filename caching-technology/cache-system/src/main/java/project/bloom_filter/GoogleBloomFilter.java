package project.bloom_filter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.ArrayList;
import java.util.List;

// TODO. Google版布隆过滤器: 单机版本不支撑分布式
public class GoogleBloomFilter {

    // expectedInsertions: 预计插入的数据
    // fpp: 容错率
    private BloomFilter<Integer> bloomFilter =
            BloomFilter.create(Funnels.integerFunnel(), 10000, 0.1);

    // 根据定义的参数，源码中使用两个算法计算出需要的位数组长度和hash函数个数
    // TODO: 定义的位数组长度位int类型的最大值(2^31-1)，约20多亿
    // long numBits = optimalNumOfBits(expectedInsertions, fpp);
    // int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);

    public void testBloomFilter() {
        int size = 10000;
        for (int index = 1; index <= size; index++) {
            bloomFilter.put(index);
        }

        List<Integer> valuesContain = new ArrayList<>(1000);
        for (int index = size + 1000; index < size + 2000; index++) {
            // 如果将不在bloomFilter的数据判断为包含在其中，则视为误判
            // 误判的概率接近于容错率，可以自定义
            if (bloomFilter.mightContain(index)) {
                valuesContain.add(index);
            }
        }
        System.out.println("误判的数量: " + valuesContain.size());
    }
}
