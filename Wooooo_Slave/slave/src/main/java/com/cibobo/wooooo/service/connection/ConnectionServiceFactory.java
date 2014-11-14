package com.cibobo.wooooo.service.connection;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;

/**
 * Created by Beibei on 15.10.2014.
 * Connection Service Factory, which can create different type of the connection
 *
 * TODO: currently makes factory totally no sense. Should check the design pattern to find out a better design to create instants for several different connections.
 */
public class ConnectionServiceFactory {

    public static ConnectionService createXMPPInstantMessageService(){
        return XMPPInstantMessageService.getInstance();
    }
}
