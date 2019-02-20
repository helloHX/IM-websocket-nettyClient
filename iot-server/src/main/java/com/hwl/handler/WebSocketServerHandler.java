package com.hwl.handler;

import com.hwl.config.Configuration;
import com.hwl.context.Context;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

/**
 * @ClassName WebSocketServerHandler
 * @Description TODO
 * @Author HuangWenlong
 * @Date 2018/11/22 10:39
 **/
@Slf4j
@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;
    private Context context;
    private Configuration configuration;

    public WebSocketServerHandler(Context context) {
        this.context = context;
        this.configuration = this.context.getConfiguration();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) {
        //判断是否是关闭链路的指令
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            handshaker.close(channelHandlerContext.channel(), ((CloseWebSocketFrame) webSocketFrame).retain());
            return ;
        }
        //判断是否是Ping消息
        if(webSocketFrame instanceof PingWebSocketFrame){
            channelHandlerContext.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }
        //暂不支持二进制信息
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame type not suppored", webSocketFrame.getClass().getName()
            ));
        }
        //返回应答消息
        String request = ((TextWebSocketFrame) webSocketFrame).text();
        log.info(String.format("%s received %s", channelHandlerContext.channel(), request));
        //将请求内容交给自定义处理器处理
        channelHandlerContext.fireChannelRead(request);
    }



    private void handlerHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) {
        //如果http解码失败，返回http异常

        if (!req.decoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(channelHandlerContext, req,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //构造握手相应
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(configuration.getWsPath(), null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
        }else{
            handshaker.handshake(channelHandlerContext.channel(), req);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext channelHandlerContext, FullHttpRequest req, DefaultFullHttpResponse defaultFullHttpResponse) {
        //返回应答客户端
        if(defaultFullHttpResponse.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.status().toString(), CharsetUtil.UTF_8);
            defaultFullHttpResponse.content().writeBytes(buf);
            buf.release();
            setContentLength(defaultFullHttpResponse, defaultFullHttpResponse.content().readableBytes());
        }
        ChannelFuture future = channelHandlerContext.channel().writeAndFlush(defaultFullHttpResponse);
        if (!isKeepAlive(req) || defaultFullHttpResponse.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //传统http的接入
        if(o instanceof FullHttpRequest){
            handlerHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        }else if(o instanceof WebSocketFrame){
            handlerWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }
}
