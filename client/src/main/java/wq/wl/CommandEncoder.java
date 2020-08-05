package wq.wl;

import com.google.common.util.concurrent.Uninterruptibles;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wq.wl.message.Message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static wq.wl.util.BytebufUtils.serialize;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class CommandEncoder extends MessageToMessageEncoder<Message.Command> {

    private static final Logger LOG = LoggerFactory.getLogger(CommandEncoder.class);


    @Override
    protected void encode(ChannelHandlerContext ctx, Message.Command msg, List<Object> out) throws Exception {
        // Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        ByteBuf byteBuf = serialize(msg);
        out.add(byteBuf);
        LOG.debug("start to send. id: {}", msg.getCommandId());
    }

}
