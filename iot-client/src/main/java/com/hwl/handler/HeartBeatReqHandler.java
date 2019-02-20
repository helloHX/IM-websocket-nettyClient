package com.hwl.handler;

import com.hwl.context.Context;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
/**
 * @Author huangwenlong
 * @Description 在完成身份认证后开始不断发送心跳
 * @Date 19:01 2018/12/13
 **/
@Slf4j
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;
    private Context context;
    private int heartBeatRate;

    public HeartBeatReqHandler(Context context) {
        this.context = context;
        this.heartBeatRate = this.context.getConfiguration().getHeartBeatRate();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatReqHandler.HeartBeatTask(ctx),0,this.heartBeatRate,TimeUnit.MILLISECONDS
            );
        }else if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()){
            log.debug("Client receive server heart beat message : ---->" + message);
        }else{
            //其他信息交由客户端处理
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable{
            private final ChannelHandlerContext ctx;
            public HeartBeatTask(final ChannelHandlerContext ctx){
             this.ctx = ctx;
            }

            @Override
            public void run() {
                NettyMessage heatBeat = buildHeartBeat();
                log.debug("Client send heart beat message to server : ----->" + heartBeat);
                ctx.writeAndFlush(heatBeat);
            }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
