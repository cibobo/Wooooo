package com.cibobo.wooooo.model;

/**
 * Created by Beibei on 16.10.2014.
 * Abstract class for Message Data, which should be extended by other message type
 */
public class MessageData {
    private String sender;
    private String receiver;
    private Object content;

    public MessageData(String sender, String receiver, String content){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return this.sender;
    }

    public void setReceiver(String receiver){
        this.receiver = receiver;
    }

    public String getReceiver(){
        return this.receiver;
    }

    public void setContent(Object object){
        this.content = object;
    }

    public Object getContent(){
        return this.content;
    }
}
