package com.cibobo.wooooo.model;

/**
 * Created by Beibei on 16.10.2014.
 * Abstract class for Message Data, which should be extended by other message type
 */
public class MessageData {
    private UserData sender;
    private UserData receiver;
    private Object content;

    public MessageData(String sender, String receiver, String content){
        this.sender = new UserData(sender);
        this.receiver = new UserData(receiver);
        this.content = content;
    }

    public void setSender(UserData sender){
        this.sender = sender;
    }

    public UserData getSender(){
        return this.sender;
    }

    public void setReceiver(UserData receiver){
        this.receiver = receiver;
    }

    public UserData getReceiver(){
        return this.receiver;
    }

    public void setContent(Object object){
        this.content = object;
    }

    public Object getContent(){
        return this.content;
    }
}
