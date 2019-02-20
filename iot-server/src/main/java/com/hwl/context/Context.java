package com.hwl.context;

import com.hwl.cache.ChannelManager;
import com.hwl.config.Configuration;
import lombok.Data;

/**
 * @ClassName Context
 * @Description 上下文类
 * @Author huangWenLong
 * @Date 2018/12/11 16:18
 **/
@Data
public class Context {
    private Configuration configuration;
    private ChannelManager channelManager = ChannelManager.getInstance();
    public Context(Configuration configuration) {
        this.configuration = configuration;
    }

}
