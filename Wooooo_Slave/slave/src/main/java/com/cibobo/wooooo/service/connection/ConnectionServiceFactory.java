package com.cibobo.wooooo.service.connection;

import com.cibobo.wooooo.service.actuator.XMPPInstantMessageService;

/**
 * Created by Beibei on 15.10.2014.
 * Connection Service Factory, which can create different type of the connection
 */
public class ConnectionServiceFactory {

    public static ConnectionService createXMPPInstantMessageService(){
        return XMPPInstantMessageService.getInstance();
    }
}
