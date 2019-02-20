package com.hwl.handler;

import com.hwl.config.Configuration;
import com.hwl.config.LoginConfiguration;
import com.hwl.context.Context;
import com.hwl.entity.Header;
import com.hwl.entity.MessageType;
import com.hwl.entity.NettyMessage;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import static com.hwl.constants.Constants.SERVER_ID;


@Slf4j
@ChannelHandler.Sharable
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    private Context context;
    private Configuration configuration;
    private LoginConfiguration loginConfiguration;

    public LoginAuthReqHandler(Context context) {
        this.context = context;
        this.configuration = this.context.getConfiguration();
        this.loginConfiguration = this.configuration.getLoginConfiguration();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(buildLoginReq());
        log.info("连通");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端关闭");
    }

    /**
     * @return com.icode.message_common.netty_message.entity.NettyMessage
     * @Author huangwenlong
     * @Description 创建登录请求消息
     * @Date 17:05 2018/12/4
     * @Param []
     **/
    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setSender(this.loginConfiguration.getDeviceID());
        header.setReceiver(SERVER_ID);
        header.setSecret(this.loginConfiguration.getDeviceSecret());
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }


    /**
     * @return void
     * @Author huangwenlong
     * @Description 接收处理登录响应消息
     * @Date 17:06 2018/12/4
     * @Param [ctx, msg]
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                ctx.close();
            } else {
                log.info("login is ok : " + message);
                ctx.fireChannelRead(msg);
                //通知用户当前通道登录成功
                this.context.loginFinishNotify(ctx.channel());
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
