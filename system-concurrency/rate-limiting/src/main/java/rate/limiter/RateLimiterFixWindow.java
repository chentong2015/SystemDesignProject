package rate.limiter;

import java.util.HashMap;

// Fixed Windows
// 固定时间范围内接受有限的请求
// 连续发送的请求timestamp时间戳一定是增加的
//
// --- window0 ----|-------- window1 ----------|
// ---------------------------------------------
// --O---O--O--K---O--O-----------O-------------
// ---------------------------------------------
// 1 2 3 4 5 6 7 8 10 11 12 13 14 15 16 17 18 19
public class RateLimiterFixWindow {

    public static void main(String[] args) {
        RateLimiter rateLimiter = new RateLimiter(10, 3);
        System.out.println(rateLimiter.invokeRequest("chen", 1));
        System.out.println(rateLimiter.invokeRequest("chen", 3));
        System.out.println(rateLimiter.invokeRequest("chen", 7));
        System.out.println(rateLimiter.invokeRequest("chen", 8)); // false
        System.out.println(rateLimiter.invokeRequest("tong", 1));
        System.out.println(rateLimiter.invokeRequest("tong", 5));
        System.out.println(rateLimiter.invokeRequest("chen", 10)); // true
    }

     static class RateLimiter {

         private int windSize;
         private int maxRequestsPerWind;
         private HashMap<String, WindowSlot> userSlotMap;

         public RateLimiter(int windSize, int maxRequestsPerWind) {
             this.windSize = windSize;
             this.maxRequestsPerWind = maxRequestsPerWind;
             this.userSlotMap = new HashMap<>();
         }

         // Time: O(1)
         // Space: O(N) N is num of users
         public boolean invokeRequest(String userId, int timestamp) {
             if (userSlotMap.containsKey(userId)) {
                 WindowSlot slot = userSlotMap.get(userId);
                 if (timestamp < slot.startTimestamp + windSize) {
                     // 属于同一个窗口区间内
                     if (slot.count < maxRequestsPerWind) {
                         slot.count++;
                         return true;
                     }
                     return false;
                 } else {
                     // 超过时间戳后，移动到下一个窗口区间，重新统计
                     slot.startTimestamp += windSize;
                     slot.count = 1;
                     return true;
                 }
             } else {
                 WindowSlot slot = new WindowSlot();
                 slot.count++;
                 userSlotMap.put(userId, slot);
                 return true;
             }
         }
     }

     // 记录当前区间内的统计
     static class WindowSlot {
         int count;
         int startTimestamp;
     }
}
