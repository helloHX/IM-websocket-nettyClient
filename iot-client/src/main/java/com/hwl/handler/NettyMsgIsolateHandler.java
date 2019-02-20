package com.hwl.handler;

import com.hwl.context.Context;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyMsgIsolateHandler
 * @Description 用户自定义消息处理了类
 * @Author huangWenLong
 * @Date 2018/11/20 21:51
 **/
@Slf4j
@ChannelHandler.Sharable
public class NettyMsgIsolateHandler extends ChannelInboundHandlerAdapter{

    private Context context;
    public NettyMsgIsolateHandler(Context context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage)msg;
        if(nettyMessage.getHeader() != null
                && nettyMessage.getHeader().getType() == MessageType.INSTANT_MESSAGING.value()) {
            //将消息内容较
               ctx.fireChannelRead(msg);
            }else{
                log.warn("the message is not a nettyMessage");
            }
        }
}
