package com.hwl.handler;

import com.hwl.context.Context;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import lombok.Setter;

/**
 * @ClassName MyChannelDuplexHandler
 * @Description 用户需要继承的处理器并能够在Login登录成功后收到回调的类
 * 建议用户继承此类
 * @Author HuangWenLong
 * @Date 2018/12/13 21:51
 **/
public class MyChannelDuplexHandler extends ChannelDuplexHandler {
    @Setter
    protected Context context;
    /**
     * @Author huangwenlong
     * @Description 完成登录验证后的回调
     * @Date 21:56 2018/12/13
     * @Param [ctx]
     * @return void
     **/
    public void LoginFinish(Channel channel){}
}
