package com.hwl.client;

import com.hwl.channelInitailizer.ClientSocketChannelInitializer;
import com.hwl.config.Configuration;
import com.hwl.config.LoginConfiguration;
import com.hwl.context.Context;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IOTA {
    ScheduledExecutorService executorService ;
    Configuration configuration;
    Channel channel;
    Context context;

    public IOTA(Configuration configuration) {
        this.configuration = configuration;
        context = new Context(configuration);
    }

    /**
     * @Author huangwenlong
     * @Description 创建与netty服务器的连接
     * @Date 19:28 2018/11/19
     * @Param []
     * @return void
     **/
    public void bind() {
        try{
            executorService = Executors.newScheduledThreadPool(1);
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ClientSocketChannelInitializer(context));
            ChannelFuture future = bootstrap.connect(
                    new InetSocketAddress(this.configuration.getServerIp(),this.configuration.getServerPort()),
                    new InetSocketAddress(this.configuration.getLocalIp(),this.configuration.getLocalPort()))
                    .sync();
            log.info("Netty client start ok :" + this.configuration.getServerIp()+ ":" +this.configuration.getServerPort());
            channel = future.channel();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            executorService.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(5);
                    bind();//发起重连操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * @Author huangwenlong
     * @Description 关闭IOTA客户端
     * @Date 20:45 2018/12/13
     * @Param []
     * @return void
     **/
    public void close(){
        this.channel.close();
        executorService.shutdown();
    }


    /**
     * @Author huangwenlong
     * @Description 同步获取DataTransServer
     * @Date 21:18 2018/12/12
     * @Param []
     * @return com.hwl.customer.DataTransServer
     **/
//    public DataTransServer getSyncDataTransServer() throws InterruptedException {
//        synchronized (context.getDataTransServer()){
//            context.getDataTransServer().wait();
//        }
//        return context.getDataTransServer();
//    }

}

