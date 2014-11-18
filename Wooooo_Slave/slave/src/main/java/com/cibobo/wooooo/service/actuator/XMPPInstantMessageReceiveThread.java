package com.cibobo.wooooo.service.actuator;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.io.IOException;

/**
 * Created by cibobo on 11/8/14.
 * Thread used to create a packet listener for XMPP message
 * Reimplement the suspend and resume functions
 */
public class XMPPInstantMessageReceiveThread extends Thread {
    private final String tag = "XMPPInstantMessageReceiveThread";

    //Monitor can be replaced by this pointer.
    //private final Object Monitor = new Object();
    private boolean pauseFlag = false;

    /*
     *@message: the initialization of XMPPInstantmessageReceiveThread can not be done in the constructor of XMPPInstantMessageService,
     * because there is an Instant of Service defined inside of Thread.
     */
    private XMPPInstantMessageService messageService;

    private AbstractXMPPConnection connection;

    public XMPPInstantMessageReceiveThread(){
        messageService = XMPPInstantMessageService.getInstance();
        connection = messageService.getConnection();
    }

    @Override
    public void run() {
        //create a normal packet filter to filter out only message packet.
        PacketFilter filter = new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {
                if(packet!=null && packet instanceof Message) {
                    return true;
                } else {
                    Log.d(tag, "Received a null message or other type of packet");
                    return false;
                }
            }
        };
        if(connection != null) {
            if(connection.isConnected()) {
                Log.d(tag, "checked connection");

                connection.addPacketListener(new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) {
                        //Call checkPause to freeze the current thread.
                        Log.d(tag, "process packet called");
                        checkPause();
                        Message message = (Message) packet;
                    /*
                     *@message: the listener receive every time two messages: the first one contains the content but the second one contains null as body.
                     * So a judgement must be created here to deal with the second message.
                     */
                        if (message.getBody() == null) {
                            Log.e(tag, "Received message contains null");
                        } else {
                            if (message.getBody().equals("cibobo")) {
                                messageService.autoAnswer(packet);
                            }
                        }
                    }
                }, filter);
            } else {
                messageService.connect(messageService.getUsername(),messageService.getPassword());
            }
        } else {
            Log.d(tag, "connection is null!!!!");
        }
    }

    /**
     * Check whether the pauseFlag is set as true,if yes, freeze the current thread with wait
     */
    private void checkPause(){
        Log.d(tag, "Checking pause");
        synchronized (this){
            while(pauseFlag){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Log.e(tag, e.toString());
                }
            }
        }
    }

    /**
     * suspend & resume function are not supported by the Thread, so implement them manually.
     */
    public void suspendThread(){
        pauseFlag = true;
    }

    public void resumeThread(){
        synchronized (this){
            pauseFlag = false;
            this.notify();
        }
    }
}
