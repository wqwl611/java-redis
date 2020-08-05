import com.google.common.collect.Maps;
import wq.wl.RedisClient;

import java.util.Map;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class RedisClientTest {

    public static void main(String[] args) throws Throwable {
        Map<String, String> configMap = Maps.newConcurrentMap();
        configMap.put("server.port", "9876");
        RedisClient redisClient = new RedisClient(configMap);
        // String s = redisClient.get("90");
        for (int i = 0; i < 100; i++) {
            long startTime = System.currentTimeMillis();
            redisClient.set("xxx"+i, "hello");
            System.out.println("cost time: " + (System.currentTimeMillis() - startTime));
        }
        String hello = redisClient.get("xxx");
        redisClient.stop();
        System.out.println(hello);
    }

}
