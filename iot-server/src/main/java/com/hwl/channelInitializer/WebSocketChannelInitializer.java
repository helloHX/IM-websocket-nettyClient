package com.hwl.channelInitializer;

import com.hwl.context.Context;
import com.hwl.handler.WSMsgForwardHandler;
import com.hwl.handler.WSLoginAuthRespHandler;
import com.hwl.handler.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @ClassName WebSocketChannelInitializer
 * @Description websocket handler 初始化
 * @Author Huangwenlong
 * @Date 2018/11/21 20:01
 **/
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    private Context context;

    public WebSocketChannelInitializer(Context context) {
        this.context = context;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
        pipeline.addLast("wsHandler",new WebSocketServerHandler(context));
        pipeline.addLast("wsloginAuthRespHandler",new WSLoginAuthRespHandler(context));
        pipeline.addLast("wsCustomerHandler",new WSMsgForwardHandler(context));

    }
}
