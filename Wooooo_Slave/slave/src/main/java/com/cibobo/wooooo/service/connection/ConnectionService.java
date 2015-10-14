package com.cibobo.wooooo.service.connection;

import com.cibobo.wooooo.model.UserData;

/**
 * Created by Beibei on 15.10.2014.
 */
public interface ConnectionService {

    public void setConnection();

    public boolean connect(UserData userData);

    public void disconnect();

    public void sendMessage();

    public Object receiveMessage();
}
