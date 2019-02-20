package com.hwl.handler;

import com.hwl.cache.ChannelManager;
import com.hwl.context.Context;
import com.hwl.domain.ClientType;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    ChannelManager channelManager;
    private Context context;

    public LoginAuthRespHandler(Context context) {
        this.context = context;
        this.channelManager = this.context.getChannelManager();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            NettyMessage loginResp = null;
            Object sign = message.getHeader().getSender();
            boolean isOK = false;
            //重复登录，拒绝
            if (sign != null && channelManager.isRegistered(sign.toString())) {
                loginResp = buildResponse((byte) -1);
            } else {
                if (channelManager.isWhiteCount(sign.toString())) {
                    isOK = true;
                }
                loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
            }
            log.info("The login response is :" + loginResp + "body [" + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
            if (isOK) {
                register(ctx.channel(), (String) sign);
            }
        } else {
            //INSTANSTS_MESSAGE必须是登录后的设备，否者关闭通道
            String from = ctx.channel().attr(Constants.OWNER).get();
            if (from != null && channelManager.isRegistered(from)) {
                ctx.fireChannelRead(msg);
            }else{
                ctx.channel().writeAndFlush(buildResponse((byte) -1));
                ctx.close();
            }
        }
    }

    /**
     * @return void
     * @Author huangwenlong
     * @Description 建立channel对应的客户端的联系
     * @Date 20:13 2018/11/20
     * @Param [channel, signal]
     **/
    private void register(Channel channel, String signal) {
        log.info("------***--- device (" + signal +") online ------****----" );
        channel.attr(Constants.OWNER).set(signal);
        channelManager.register(signal, channel, ClientType.NETTY_CLIENT);
    }

    /**
     * @return void
     * @Author huangwenlong
     * @Description 当通道断开时清理对channel的管理
     * @Date 19:48 2018/12/11
     * @Param [ctx]
     **/
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("------***--- device (" + ctx.channel().attr(Constants.OWNER).get() +") offline ------****----" );
        channelManager.nodeOffline(ctx.channel().attr(Constants.OWNER).get());
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

}
