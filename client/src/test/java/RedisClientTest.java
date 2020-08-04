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
        redisClient.set("xxx", "hello");
        String hello = redisClient.get("xxx");
        redisClient.stop();
        System.out.println(hello);
    }

}
