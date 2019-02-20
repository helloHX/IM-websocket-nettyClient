package com.hwl;

import com.hwl.client.IOTA;
import com.hwl.config.Configuration;
import com.hwl.config.LoginConfiguration;
import com.hwl.customer.CustomerHandler;

public class App {
    public static void main(String[] args) {
        LoginConfiguration loginConfiguration = new LoginConfiguration();
        loginConfiguration.setDeviceID("t1");
        loginConfiguration.setDeviceSecret("123456");
        Configuration configuration = new Configuration("localhost", 1212,
                "localhost", 1223);
        configuration.setLoginConfiguration(loginConfiguration);
        configuration.addChannelHandler("customer", new CustomerHandler());
        IOTA iota = new IOTA(configuration);
        new Thread(iota::bind).start();
    }
}
