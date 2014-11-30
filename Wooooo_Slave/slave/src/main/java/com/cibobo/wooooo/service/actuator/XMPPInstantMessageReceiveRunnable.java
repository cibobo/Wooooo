package com.cibobo.wooooo.service.actuator;


import android.util.Log;


import com.cibobo.wooooo.model.MessageData;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.Observable;


/**
 * Created by cibobo on 11/30/14.
 * Runnable used to listener of packet to get the message send from server
 * Extends Observable, so that it can notify the registered observers, when it catch a new message
 */
public class XMPPInstantMessageReceiveRunnable extends Observable implements Runnable{
    private final String tag = "XMPPInstantMessageReceiveRunnable";

    //Monitor can be replaced by this pointer.
    //private final Object Monitor = new Object();
    private boolean pauseFlag = false;

    /*
     *@message: the initialization of XMPPInstantmessageReceiveRunnable can not be done in the constructor of XMPPInstantMessageService,
     * because there is an Instant of Service defined inside of Thread.
     */
    private XMPPInstantMessageService messageService;

    private AbstractXMPPConnection connection;

    public XMPPInstantMessageReceiveRunnable(){
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
                        Message message = (Message) packet;
                    /*
                     *@message: the listener receive every time two messages: the first one contains the content but the second one contains null as body.
                     * So a judgement must be created here to deal with the second message.
                     */
                        if (message.getBody() == null) {
                            Log.e(tag, "Received message contains null");
                        } else {
                            if (message.getBody().equals("cibobo")) {
                                //messageService.autoAnswer(packet);
                                MessageData messageData = new MessageData(packet.getFrom(), packet.getTo(), ((Message) packet).getBody());
                                //SetChanged must be called before notify all observers
                                setChanged();
                                //Send received message to every observers
                                notifyObservers(messageData);
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


}
