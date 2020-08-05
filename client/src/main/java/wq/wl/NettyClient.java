package wq.wl;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import wq.wl.message.Message;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 * @author: wangliang
 * @time: 2020-08-03
 */
public class NettyClient implements LifeCycle {

    public NettyClient(Map<String, String> config) {
        this.config = config;
        port = Integer.parseInt(config.getOrDefault("server.port", "09"));
        host = config.getOrDefault("server.host", "127.0.0.1");
    }

    @Override
    public void start() throws Throwable {
        Bootstrap bootstrap = new Bootstrap();
        Bootstrap group = bootstrap.group(new NioEventLoopGroup(1));
        group.channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ChannelPipeline pipeline = sc.pipeline();
                        pipeline.addLast(new CommandEncoder());
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(100000, 0, 4, 0, 4));
                        pipeline.addLast(new ClientHandler());
                    }
                });
        ChannelFuture cf = bootstrap.connect(host, port).sync();
        channel = cf.channel();
    }

    public void sendCommand(Message.Command command) throws ExecutionException, InterruptedException {
        channel.writeAndFlush(command);
    }

    @Override
    public void stop() throws Throwable {
        channel.eventLoop().parent().shutdownGracefully();
        channel.close();
    }

    private Map<String, String> config;

    private int port;

    private String host;

    private Channel channel;

}
