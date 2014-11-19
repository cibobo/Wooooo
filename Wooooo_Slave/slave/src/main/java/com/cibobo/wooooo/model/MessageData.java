package com.cibobo.wooooo.model;

/**
 * Created by Beibei on 16.10.2014.
 * Abstract class for Message Data, which should be extended by other message type
 */
public abstract class MessageData {
    private UserData sender;
    private UserData receiver;
    private Object content;

    public void setSender(UserData sender){
        this.sender = sender;
    }

    public void setReceiver(UserData receiver){
        this.receiver = receiver;
    }

    public void setContent(Object object){
        this.content = object;
    }
}
