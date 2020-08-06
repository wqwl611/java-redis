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
        String s = redisClient.get("90");
        boolean set = redisClient.set("你好", "你好");
        String 你好 = redisClient.get("你好");
        for (int i = 0; i < 100000000; i++) {
            long startTime = System.currentTimeMillis();
            redisClient.set("xxx你好" + i, "hello");
            System.out.println("cost time: " + (System.currentTimeMillis() - startTime));
        }
        String hello = redisClient.get("xxx");
        redisClient.stop();
        System.out.println(hello);
    }

}
