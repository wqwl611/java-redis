package wq.wl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class BytebufUtils {
    private static final Logger LOG = LoggerFactory.getLogger(BytebufUtils.class);

    public static ByteBuf serialize(com.google.protobuf.GeneratedMessage message) {
        byte[] bytes = message.toByteArray();
        ByteBuf byteBuf = Unpooled.directBuffer(bytes.length + 5);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        LOG.debug("send data: [{}]", message.toString());
        return byteBuf;
    }
}
