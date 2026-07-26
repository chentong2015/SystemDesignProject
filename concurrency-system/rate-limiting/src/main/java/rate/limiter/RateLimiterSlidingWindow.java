package rate.limiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

// Sliding Windows
// 滑动区间窗口范围内接受有限请求
// 连续发送的请求timestamp时间戳一定是增加的
//
// --- window0 ----|-------- window1 ----------|
// ---------------------------------------------
// --O---O--O--K---O--O-------------------O------
// ---------------------------------------------
// 1 2 3 4 5 6 7 8 10 11 12 13 14 15 16 17 18 19
public class RateLimiterSlidingWindow {

    public static void main(String[] args) {
        RateLimiter rateLimiter = new RateLimiter(10, 3);
        System.out.println(rateLimiter.invokeRequest("chen", 1));
        System.out.println(rateLimiter.invokeRequest("chen", 3));
        System.out.println(rateLimiter.invokeRequest("chen", 7));
        System.out.println(rateLimiter.invokeRequest("chen", 8)); // false
        System.out.println(rateLimiter.invokeRequest("tong", 1));
        System.out.println(rateLimiter.invokeRequest("tong", 5));
        System.out.println(rateLimiter.invokeRequest("chen", 10)); // false
        System.out.println(rateLimiter.invokeRequest("chen", 15));
    }

    static class RateLimiter {
        private int windSize;
        private int maxRequestsPerWind;
        private HashMap<String, List<Integer>> userTimestampMap;

        public RateLimiter(int windSize, int maxRequestsPerWind) {
            this.windSize = windSize;
            this.maxRequestsPerWind = maxRequestsPerWind;
            this.userTimestampMap = new HashMap<>();
        }

        // TODO. 使用List存储请求的历史时间，动态往后滑动的同时移除在窗口外的时间
        // Time: O(M)    M is max num request in window
        // Space: O(N*M) 每个用户都必须存储的请求时间
        public boolean invokeRequest(String userId, int timestamp) {
            List<Integer> timestampList = userTimestampMap.getOrDefault(userId, new ArrayList<>());
            timestampList = timestampList.stream()
                    .filter(t -> (timestamp - windSize) < t)
                    .collect(Collectors.toList());

            if (timestampList.size() < maxRequestsPerWind) {
                timestampList.add(timestamp);
                userTimestampMap.put(userId, timestampList);
                return true;
            }
            // 如果已经达到最大请求数目，拒绝添加
            return false;
        }
    }
}
