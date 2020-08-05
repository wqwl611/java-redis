package wq.wl.network;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wq.wl.kvdb.KvDb;
import wq.wl.message.Message;
import wq.wl.util.IDGenerator;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    private KvDb kvDb;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        LOG.debug("server receive data.");
        Message.Command command = parseCommand(msg);
        LOG.debug("receive command: [{}]", command.toString());
        Message.Result result = processCommand(command);
        ctx.writeAndFlush(result);
        LOG.debug("server response send.");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOG.info("connection from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("exception: ", cause);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOG.info("close one connection.");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("close one connection.");
    }

    private Message.Result processCommand(Message.Command command) {
        switch (command.getCommandType()) {
            case GET:
                return processGet(command);
            case DEL:
                return processDel(command);
            case SET:
                return processSet(command);
            default:
                LOG.error("unknown command type.");
                return Message.Result.newBuilder()
                        .setCommandId(IDGenerator.instance().getId())
                        .setResCode(Message.ResCode.RES_FAIL)
                        .setResMsg("unknown command type.")
                        .build();
        }
    }

    Message.Result.Builder failResultBuilder = Message.Result.newBuilder().setResCode(Message.ResCode.RES_FAIL);

    private Message.Result processSet(Message.Command command) {
        try {
            kvDb.set(command.getKey(), command.getValue());
        } catch (RocksDBException e) {
            return getFailResult(command, e);
        }
        Message.Result result = Message.Result.newBuilder()
                .setResCode(Message.ResCode.RES_SUCCESS)
                .setCommandId(command.getCommandId())
                // .setResMsg("set success.")
                .build();
        return result;
    }

    private Message.Result getFailResult(Message.Command command, RocksDBException e) {
        return failResultBuilder.setResMsg(e.getMessage()).setCommandId(command.getCommandId()).build();
    }

    private Message.Result processDel(Message.Command command) {
        try {
            kvDb.del(command.getKey());
        } catch (RocksDBException e) {
            return getFailResult(command, e);
        }
        Message.Result result = Message.Result.newBuilder()
                .setCommandId(command.getCommandId())
                .setResCode(Message.ResCode.RES_SUCCESS)
                // .setResMsg("del success.")
                .build();
        return result;
    }

    private Message.Result processGet(Message.Command command) {
        String result = null;
        try {
            result = kvDb.get(command.getKey());
        } catch (RocksDBException e) {
            return getFailResult(command, e);
        }
        return Message.Result.newBuilder()
                .setCommandId(command.getCommandId())
                .setResCode(Message.ResCode.RES_SUCCESS)
                .setResult(result)
                .build();
    }

    private Message.Command parseCommand(ByteBuf msg) throws InvalidProtocolBufferException {
        try {
            return Message.Command.parseFrom(getBytes(msg));
        } catch (InvalidProtocolBufferException e) {
            // 理论上不应该出现
            LOG.error("parse command error: ", e);
            throw e;
        }
    }

    private byte[] getBytes(ByteBuf msg) {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        return bytes;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

}
