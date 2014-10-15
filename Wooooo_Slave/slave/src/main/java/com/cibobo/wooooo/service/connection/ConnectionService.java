package com.cibobo.wooooo.service.connection;

/**
 * Created by Beibei on 15.10.2014.
 */
public interface ConnectionService {

    public void setConnection();

    public boolean connect(String username, String password);

    public void sendMessage();
}
