package com.hwl.handler;

import com.hwl.context.Context;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    Context context;

    public HeartBeatRespHandler(Context context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;
        //返回心跳应答消息
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()){
           log.debug("Receive client heart beat message : ---> " + message);
            NettyMessage heartBeat = buildHeatBeat();
            log.debug("Send heart beat response message to client :---> " + message);
            ctx.writeAndFlush(heartBeat);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }


}
