package com.cibobo.wooooo.service.actuator;

import android.util.Log;

import com.cibobo.wooooo.model.LocationMessageData;
import com.cibobo.wooooo.model.MessageData;
import com.cibobo.wooooo.model.UserData;
import com.cibobo.wooooo.service.connection.ConnectionService;
import com.cibobo.wooooo.unused.XMPPInstantMessageReceiveThread;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.packet.Message;

import java.io.IOException;

/**
 * Created by Beibei on 15.10.2014.
 * This class should not include any handler from android.os
 *
 * Updated by Beibei on 29.09.2015.
 * Google stopped their gtalk service, so that we have to change our code to use jabber
 */
public class XMPPInstantMessageService implements ConnectionService{

    //Constant String
    private String tag = "XMPPInstantMessageService";

    private static XMPPInstantMessageService serviceInstance = null;

    private UserData loginUser = null;

    private String servername = "Jabber";

    /*
    private String host = "talk.google.com";
    private String service = "gmail.com";
    private String targetUser = "cibobo2005@gmail.com";
    */

    private String host = "jabber.de";

    private int port = 5222;

    private String targetUser = "cibobo2005@gmail.com";

    private AbstractXMPPConnection connection = null;
    private ConnectionConfiguration connectionConfiguration = null;

    private XMPPInstantMessageReceiveThread messageReceiverThread = null;
    private XMPPInstantMessageReceiveRunnable messageReceiveRunnable = null;


    private String savedMessage;

    private XMPPInstantMessageService(){
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(host,port);
        connection = new XMPPTCPConnection(connectionConfiguration);
    }

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
    public boolean connect(UserData userData) {
        /*
         * Message: Google has recently switched to not allowing PLAIN and similar methods on its accounts.
         * If you still want to use the auth mechanism created by smack, the account must be enabled during
         * https://www.google.com/settings/security/lesssecureapps
         */
        String username = userData.getUserName();
        String password = userData.getPassWord();

        try {
            /*
             *  Message: Change the server to Jabber
             *  Jabber allow to use PLAIN method to authenticate.
             *  The more complicated method should be deactivated
             */
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            connection.connect();
            Log.i(tag, "Connect to " + connection.getHost());

            //TODO: Should add some code to make sure, that if the password is wrong, the APP can still running correctly.
            /*
             * Jabber only need the user name before @ to login.
             * So call substring to get the login user name.
             */
            String loginUsername = username.substring(0, username.indexOf('@'));
            Log.d(tag, "username: " + loginUsername + " | password: " + password);

            connection.login(loginUsername, password);
            Log.i(tag,"Login as " + connection.getUser());

            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
        } catch (SmackException e) {
            Log.e(tag, e.toString());
            return false;
        } catch (IOException e) {
            Log.e(tag, e.toString());
            return false;
        } catch (XMPPException e) {
            Log.e(tag, e.toString());
            return false;
        }

        this.loginUser = userData;

        return true;
    }


    //TODO: TO find out why after restart of the SW, it will be an AlreadyLoggedInException
    //A possible solution is to keep the login state after the shut down of the App.
    @Override
    public void disconnect() {
        try {
            if(serviceInstance.messageReceiverThread != null){
                //Destroy the running Thread for message receiver
                serviceInstance.messageReceiverThread.interrupt();
                try {
                    //wait 50ms until the thread going to die.
                    serviceInstance.messageReceiverThread.join(50);
                    Log.d(tag, "Killing thread");
                } catch (InterruptedException e) {
                    Log.e(tag, e.toString());
                } finally {
                    serviceInstance.messageReceiverThread = null;
                }
            } else {
                Log.d(tag, "No thread to kill in disconnection");
            }
            connection.disconnect();
        } catch (SmackException.NotConnectedException e) {
            Log.e(tag,e.toString());
        }
    }

    @Override
    public void sendMessage() {
        Message message = new Message(targetUser);
        message.setBody("This is a test message from Woooooo!!!");
        try {
            connection.sendPacket(message);
        } catch (SmackException.NotConnectedException e) {
            Log.e(tag, e.toString());
        }
    }

    public void sendMessage(MessageData messageData){
        Message message = new Message(messageData.getReceiver().getUserName());
        message.setBody(messageData.getContent().toString());
        try {
            connection.sendPacket(message);
            Log.d(tag, "Send a new Message: " + message.toString());
        } catch (SmackException.NotConnectedException e) {
            Log.e(tag, e.toString());
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

    /**
     * Return the receive runnable; if it not exist, create one.
     * @message MessageReceiveRunnable cannot be created in the constructor of the XMPPInstantmessageService, because there is an Instant of it contained by MessageReceiveRunnable
     * @return
     */
    public XMPPInstantMessageReceiveRunnable getMessageReceiveRunnable(){
        if(messageReceiveRunnable == null){
            messageReceiveRunnable = new XMPPInstantMessageReceiveRunnable();
        }
        return messageReceiveRunnable;
    }

    /**
     * Return the current connection
     * @return current connection
     */
    public AbstractXMPPConnection getConnection(){
        return this.connection;
    }

    /**
     * Create a standard message with defined receiver
     * @return standard message
     */
    public Message createMessage(){
        //TODO: create a completed message, so that it can be accepted by other software using XMPP protocol, like Hangouts
        Message message = new Message(targetUser);
        return message;
    }


//    /**
//     * Start the receiver thread; if it is the first time to start it, create a new instant.
//     */
//    @Override
//    public void startReceiverThread(){
//        if(messageReceiverThread == null) {
//            /*
//             *@message: because messageReceiverThread contains an Instance of XMPPInstantMessageService, the initialization of thread can not be done in the constructor of service
//             */
//            messageReceiverThread = new XMPPInstantMessageReceiveThread();
//            messageReceiverThread.start();
//        } else {
//            messageReceiverThread.resumeThread();
//        }
//        Log.i(tag, "current thread ID = " + messageReceiverThread.getId());
//
//    }
//
//
//    /**
//     * Stop the current thread. Only suspend the thread but not destroy it.
//     * The same thread can be resumed during the next call of startReceiverThread
//     */
//    @Override
//    public void stopReceiverThread(){
//        if(this.messageReceiverThread!=null){
//            messageReceiverThread.suspendThread();
//        }
//
//    }

    public void autoAnswer(Packet packet){
        Message message = (Message) packet;
        savedMessage = message.getBody();
        Log.d(tag, "receive package: " + ((Message) packet).getType());
        Log.d(tag, "Receive message: " + savedMessage);

        //Create a location data for current location information.
        LocationMessageData locationData = new LocationMessageData();
        Log.d(tag, locationData.toString());

        Message answer = this.createMessage();
        answer.setBody(locationData.toString());

        //Try to use the original receive message
//        message.setTo(this.targetUser);
//        message.setBody(locationData.toString());

        try {
            connection.sendPacket((Packet)answer);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(){
        return this.loginUser.getUserName();
    }

    public String getPassword(){
        return this.loginUser.getPassWord();
    }

    public UserData getUserData(){
        return this.loginUser;
    }

    public Object getReceivedMessage(){
        return this.savedMessage;
    }

}
