package wq.wl;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wq.wl.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static wq.wl.util.IDGenerator.generateId;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class RedisClient implements LifeCycle {


    public static ResultFuture<Message.Result> getFuture(long id) {
        return commandFutrueContainer.get(id);
    }

    public RedisClient(Map<String, String> configMap) {
        configMap.put("server.port", "9876");
        this.configMap = configMap;
        this.nettyClient = new NettyClient(configMap);
        try {
            start();
        } catch (Throwable throwable) {
            LOG.error("start failed, ", throwable);
            System.exit(-1);
        }
    }

    public boolean set(String key, String value) throws Exception {
        long id = generateId();
        ResultFuture<Message.Result> resultFuture = new ResultFuture<>();
        commandFutrueContainer.put(id, resultFuture);
        Message.Command getCommand = Message.Command.newBuilder()
                .setKey(key)
                .setValue(value)
                .setCommandType(Message.CommandType.SET)
                .setCommandId(id).build();
        nettyClient.sendCommand(getCommand);
        Message.Result result = resultFuture.get();
        return result.getResCode().equals(Message.ResCode.RES_SUCCESS);
    }

    public String get(String key) throws ExecutionException, InterruptedException {
        long id = generateId();
        ResultFuture<Message.Result> resultFuture = new ResultFuture<>();
        commandFutrueContainer.put(id, resultFuture);
        // todo
        Message.Command getCommand = Message.Command.newBuilder()
                .setKey(key)
                .setCommandType(Message.CommandType.GET)
                .setCommandId(id).build();
        nettyClient.sendCommand(getCommand);
        Message.Result result = resultFuture.get();
        if (result.getResCode().equals(Message.ResCode.RES_SUCCESS)) {
            return result.getResult();
        } else {
            // todo
            throw new RuntimeException();
        }
    }

    @Override
    public void start() throws Throwable {
        nettyClient.start();
    }

    @Override
    public void stop() throws Throwable {
        nettyClient.stop();
    }

    NettyClient nettyClient;

    private Map<String, String> configMap;

    private static ConcurrentMap<Long, ResultFuture<Message.Result>> commandFutrueContainer = Maps.newConcurrentMap();

    private static final Logger LOG = LoggerFactory.getLogger(CommandEncoder.class);

}
