package com.hwl;


import com.hwl.config.Configuration;
import com.hwl.server.SocketServer;

/**
 * Unit test for simple App.
 */
public class App {
    /**
     * Rigorous Test :-)
     */
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.setHeartBeatRate(5000);
        configuration.setIp("localhost");
        configuration.setPort(1212);
        configuration.setWPort(1224);
        configuration.setWsPath("ws//localhost:1224/websocket");
        configuration.setSocketServerEnable(true);
        configuration.setWSocketServerEnable(true);
        SocketServer socketServer = new SocketServer(configuration);
        socketServer.start();
    }
}
