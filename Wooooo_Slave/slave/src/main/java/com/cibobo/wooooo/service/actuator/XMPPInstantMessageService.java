package com.cibobo.wooooo.service.actuator;

import com.cibobo.wooooo.service.connection.ConnectionService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.packet.Message;

import java.io.IOException;

/**
 * Created by Beibei on 15.10.2014.
 */
public class XMPPInstantMessageService implements ConnectionService{
    private static XMPPInstantMessageService serviceInstance = null;

    private String username = "hiwi.fzi@googlemail.com";
    private String password = "Karlsruhe";
    private String host = "talk.google.com";
    private String service = "gmail.com";
    private int port = 5222;

    private String targetUser = "cibobo2005@gmail.com";

    private AbstractXMPPConnection connection = null;

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
            e.printStackTrace();
        }
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
            //TODO: using the input username and password
            connection.login(this.username,this.password);

        } catch (SmackException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getUsername(){
        return this.username;
    }
}
