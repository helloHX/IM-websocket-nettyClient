package com.hwl.server;

import com.hwl.channelInitializer.ServerSocketChannelInitializer;
import com.hwl.channelInitializer.WebSocketChannelInitializer;
import com.hwl.config.Configuration;
import com.hwl.context.Context;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

@Slf4j
public class SocketServer {

    Context context;
    Configuration configuration;

    public SocketServer(Configuration configuration) {
        this.configuration = configuration;
        context = new Context(configuration);
    }

    public void start() {
     if (configuration.isSocketServerEnable()) {
            new Thread(this::bind).start();
        }
        if (configuration.isWSocketServerEnable()) {
            new Thread(this::webSocketBind).start();
        }
    }
    /**
     * @return void
     * @Author huangwenlong
     * @Description 启动webSocket服务
     * @Date 10:37 2018/11/22
     * @Param []
     **/
    private void webSocketBind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelInitializer<SocketChannel> wSocketChannelChannelInitializer
                    = new WebSocketChannelInitializer(context);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(wSocketChannelChannelInitializer);
            Channel ch = bootstrap.bind(configuration.getWPort()).sync().channel();
            log.info("web socket server started at port" + configuration.getWPort() + ".");
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * @return void
     * @Author huangwenlong
     * @Description 启动socket 服务
     * @Date 10:37 2018/11/22
     * @Param []
     **/
    private void bind() {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelInitializer<SocketChannel> socketChannelChannelInitializer
                    = new ServerSocketChannelInitializer(context);
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(socketChannelChannelInitializer);
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(configuration.getIp(),configuration.getPort())).sync();
            log.info("Netty server start ok :" + configuration.getIp() + ":" + configuration.getPort());
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
