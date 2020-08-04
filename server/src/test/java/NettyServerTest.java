import com.google.common.collect.Maps;
import org.junit.Test;
import wq.wl.network.NettyServer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class NettyServerTest {

    public static void main(String[] args) throws Throwable{
        Map<String, String> configMap = Maps.newConcurrentMap();
        configMap.put("server.port", "9876");
        NettyServer nettyServer = new NettyServer(configMap);
        nettyServer.start();
        TimeUnit.SECONDS.sleep(1000000);
    }
}
