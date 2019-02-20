package com.hwl.channelInitailizer;


import com.hwl.codec.NettyMessageDecoder;
import com.hwl.codec.NettyMessageEncoder;
import com.hwl.context.Context;
import com.hwl.handler.MyChannelDuplexHandler;
import com.hwl.handler.NettyMsgIsolateHandler;
import com.hwl.handler.HeartBeatReqHandler;
import com.hwl.handler.LoginAuthReqHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Context context;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("NettyMessageDecoder",
        new NettyMessageDecoder(1024 * 10,4,4));
        socketChannel.pipeline().addLast("MessageEncoder",new NettyMessageEncoder());
        socketChannel.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(context.getConfiguration().getHeartBeatRate()));
        socketChannel.pipeline().addLast("LoginAuthHandler",new LoginAuthReqHandler(context));
        socketChannel.pipeline().addLast("HeartBeatHandler",new HeartBeatReqHandler(context));
        socketChannel.pipeline().addLast("customerHandler",new NettyMsgIsolateHandler(context));
        context.getConfiguration().getChannelHandlerMap().forEach((name,handler) ->{
            if(handler instanceof MyChannelDuplexHandler){//填入上下文，以便使用上下文资源
                ((MyChannelDuplexHandler) handler).setContext(context);
            }
            socketChannel.pipeline().addLast(name,handler);
        });
    }
}
