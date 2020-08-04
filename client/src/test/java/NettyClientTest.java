import com.google.common.collect.Maps;
import wq.wl.NettyClient;
import wq.wl.message.Message;
import wq.wl.util.IDGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import static wq.wl.message.Message.CommandType.DEL;
import static wq.wl.message.Message.CommandType.GET;
import static wq.wl.message.Message.CommandType.SET;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class NettyClientTest {

    public static void main(String[] args) throws Throwable {
        Map<String, String> configMap = Maps.newConcurrentMap();
        configMap.put("server.port", "9876");
        NettyClient nettyClient = new NettyClient(configMap);
        nettyClient.start();

        long i = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Message.Command.Builder builder = Message.Command.newBuilder();
        Message.Command command = null;
        while (true) {
            String next = br.readLine();
            System.out.println("------------------");
            command = builder
                    .setCommandId(IDGenerator.instance().getId())
                    .setCommandType(GET)
                    .setKey("key:" + next)
                    .build();
            nettyClient.sendCommand(command);
            command = builder
                    .setCommandId(IDGenerator.instance().getId())
                    .setCommandType(DEL)
                    .setKey("key:" + next)
                    .build();
            nettyClient.sendCommand(command);
            command = builder
                    .setCommandId(IDGenerator.instance().getId())
                    .setCommandType(SET)
                    .setKey("key:" + next)
                    .setValue("value:" + next)
                    .build();
            nettyClient.sendCommand(command);


            System.out.println(1);
        }
    }
}
