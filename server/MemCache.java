import java.util.concurrent.ConcurrentHashMap;
import net.spy.memcached.MemcachedClient;

public class MemCache {
  public static String memcachedServer = "ec2-54-185-40-120.us-west-2.compute.amazonaws.com";
  public static MemcachedClient client;

  public static ConcurrentHashMap<String, Long> statsMap = new ConcurrentHashMap<>();

  public static void updateStatsTable(String countLatency, String sumLatency, String maxLatency, Long latencyValue){
    // update max latency
    Long currentMax = statsMap.get(maxLatency);
    if(currentMax == null){
      statsMap.put(maxLatency, latencyValue);
    } else {
      if(latencyValue > currentMax){
        statsMap.put(maxLatency, latencyValue);
      }
    }

    // update count
    Long currentCount = statsMap.get(countLatency);
    if(currentCount == null){
      statsMap.put(countLatency, 1L);
    } else {
      statsMap.put(countLatency, currentCount + 1L);
    }

    // update sum
    Long currentSum = statsMap.get(sumLatency);
    if(currentSum == null){
      statsMap.put(sumLatency, latencyValue);
    } else {
      statsMap.put(sumLatency, currentSum + latencyValue);
    }
  }
}
