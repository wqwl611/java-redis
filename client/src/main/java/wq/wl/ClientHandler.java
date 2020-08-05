package wq.wl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wq.wl.message.Message;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        Message.Result result = Message.Result.parseFrom(bytes);
        setFutureResult(result);
        LOG.debug("receive result: [{}]", result.toString());
    }

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private void setFutureResult(Message.Result result) {
        long commandId = result.getCommandId();
        ResultFuture<Message.Result> future = RedisClient.getFuture(commandId);
        if (future == null) {
            LOG.error("理论上不可能。");
        }
        future.setResult(result);
    }
}
