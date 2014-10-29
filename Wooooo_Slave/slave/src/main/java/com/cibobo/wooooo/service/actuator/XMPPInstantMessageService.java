package com.cibobo.wooooo.service.actuator;

import android.content.Context;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.cibobo.wooooo.service.connection.ConnectionService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.packet.Message;

import java.io.IOException;

/**
 * Created by Beibei on 15.10.2014.
 * This class should not include any handler from android.os
 */
public class XMPPInstantMessageService implements ConnectionService{
    //Constant String
    private String XMPP_LOG_TITLE = "XMPPInstantMessageService";

    private static XMPPInstantMessageService serviceInstance = null;

    private String username = "hiwi.fzi@googlemail.com";
    private String password = "Karlsruhe";
    private String host = "talk.google.com";
    private String service = "gmail.com";
    private int port = 5222;
    private String targetUser = "cibobo2005@gmail.com";

    private AbstractXMPPConnection connection = null;

    private String savedMessage;
    /**
     * Create an instance of the XMPP Message Service
     * @return an Instance of the XMPP Instant Message Service
     */
    public static XMPPInstantMessageService getInstance() {
        if(serviceInstance == null){
            serviceInstance = new XMPPInstantMessageService();
        }
        return serviceInstance;
    }

    @Override
    public void setConnection() {

    }

    @Override
    public void sendMessage() {
        Message message = new Message(targetUser);
        message.setBody("This is a test message from Woooooo!!!");
        try {
            connection.sendPacket(message);
        } catch (SmackException.NotConnectedException e) {
            Log.e(XMPP_LOG_TITLE, e.toString());
        }
    }

    @Override
    public Object receiveMessage() {
//        PacketFilter filter = new PacketTypeFilter(Message.class);
//        connection.addPacketListener(new PacketListener() {
//            @Override
//            public void processPacket(Packet packet) throws SmackException.NotConnectedException {
//                Message message = (Message)packet;
//                savedMessage = message.getBody();
//            }
//        }, filter);
        //New API in smack 4.1 to create a object of ChatManager.
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        savedMessage = message.getBody();
                    }
                });
            }
        });
        return savedMessage;
    }

    @Override
    public boolean connect(String username, String password) {
        /*
         * Message: Google has recently switched to not allowing PLAIN and similar methods on its accounts.
         * If you still want to use the auth mechanism created by smack, the account must be enabled during
         * https://www.google.com/settings/security/lesssecureapps
         */
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(host,port,service);
        connection = new XMPPTCPConnection(connectionConfiguration);

        try {
            connection.connect();
            Log.i(XMPP_LOG_TITLE, "Connect to " + connection.getHost());
            //TODO: using the input username and password
            connection.login(this.username,this.password);
            Log.i(XMPP_LOG_TITLE,"Login as " + connection.getUser());

            Presence presence = new Presence(Presence.Type.unavailable);
            connection.sendPacket(presence);
        } catch (SmackException e) {
            Log.e(XMPP_LOG_TITLE, e.toString());
            return false;
        } catch (IOException e) {
            Log.e(XMPP_LOG_TITLE, e.toString());
            return false;
        } catch (XMPPException e) {
            Log.e(XMPP_LOG_TITLE, e.toString());
            return false;
        }
        return true;
    }

    public void autoAnswer(){
        PacketFilter filter = new PacketTypeFilter(Message.class);
        connection.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                Message message = (Message)packet;
                String receivedMessage = message.getBody();
                //TODO:should check whether the body of the received message doesn't contain any other infors.
                if(receivedMessage.equals("cibobo")){
                    sendMessage();
                }
            }
        }, filter);
    }

    public String getUsername(){
        return this.username;
    }

    public Object getReceivedMessage(){
        return this.savedMessage;
    }

    public void testReceiver(final Context context){
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        savedMessage = message.getBody();
                    }
                });
            }
        });
    }
}
