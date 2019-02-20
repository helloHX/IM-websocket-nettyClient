package com.hwl.channelInitializer;

import com.hwl.codec.NettyMessageDecoder;
import com.hwl.codec.NettyMessageEncoder;
import com.hwl.context.Context;
import com.hwl.handler.NettyMsgForwardHandler;
import com.hwl.handler.HeartBeatRespHandler;
import com.hwl.handler.LoginAuthRespHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ServerSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    Context context;

    public ServerSocketChannelInitializer(Context context) {
        this.context = context;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("nettyMessageDecoder",new NettyMessageDecoder(1024 * 10,4,4));
        socketChannel.pipeline().addLast("nettyMessageEncoder",new NettyMessageEncoder());
        socketChannel.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(context.getConfiguration().getHeartBeatRate()));
        socketChannel.pipeline().addLast("loginAuthRespHandler",new LoginAuthRespHandler(context));
        socketChannel.pipeline().addLast("HeartBeatHandler",new HeartBeatRespHandler(context));
        socketChannel.pipeline().addLast("customerHandler",new NettyMsgForwardHandler(context));
    }
}

