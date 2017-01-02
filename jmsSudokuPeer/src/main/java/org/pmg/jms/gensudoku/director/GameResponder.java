/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gensudoku.director;

import com.google.inject.Inject;
import java.util.Iterator;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.pmg.jms.genclient.ClientResponder;
import org.pmg.jms.genconnect.OpenWireSessionPrvdr;
import org.pmg.jms.gendirector.ClientState;
import org.pmg.jms.genhandler.XStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class GameResponder extends ClientResponder {
    private static final Logger LOG = LoggerFactory.getLogger(GameResponder.class);
    private static final int MESSAGE_LIFESPAN = 3000;  // milliseconds (3 seconds)
    private static final int HIGH_PRIORITY = 9;
    private final XStreamHandler xmlHandler = new XStreamHandler();
    private final StartMapBean startBean = new StartMapBean();
    private final ClientState state;
    private String sessionId;    
    private String webClientId = "";    

    @Inject
    public GameResponder(OpenWireSessionPrvdr sessionProvider,
                                                            ClientState state) {

        super(sessionProvider);
        this.state = state;
        LOG.debug("[{}] state bean : {}",className, state.toString());
    }
    
    public void init(String sessionId) {

        this.sessionId = sessionId;
    }
    
    public void configure() throws JMSException { 

        webClientId = state.delegate.getString("clientId");
        Queue replyQueue = (Queue) state.delegate.getJMSReplyTo();
        setReplyToDest("webclient",replyQueue);
        startBean.configure(state.delegate);
    }

    /*
     - send reset notice to game server
    */
    public void notifyComplete() {
            
        try {
            LOG.info("[{}] notify game server to reset",className);
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer","game-control");
            mapMessage.setString("action","reset");
            mapMessage.setString("sessionId",sessionId);                
            send("game-control",mapMessage,DeliveryMode.NON_PERSISTENT,
                                                HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }                

    public void putMonitorAction(String action, int priority) {
            
        try {
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setStringProperty("peer","monitor");
            mapMessage.setString("action",action);
            send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                                    priority,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }

    public void putControlAction(String action) {
            
        try {
            MapMessage mapMessage = getSession().createMapMessage();
            mapMessage.setString("action",action);
            send("resolve-control",mapMessage,DeliveryMode.NON_PERSISTENT,
                                                HIGH_PRIORITY,MESSAGE_LIFESPAN);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }

    /*
     - send xml solution to the web client
    */
    public void putXmlSolution() {
        
        try {
            state.ajaxBean.setFrom(webClientId);
            String xmlText = xmlHandler.getXmlText(state.ajaxBean,true);
            TextMessage txtMessage = getSession().createTextMessage();
            txtMessage.setStringProperty("state","play");
            txtMessage.setText(xmlText);
            send("webclient",txtMessage,DeliveryMode.NON_PERSISTENT,
                                    Message.DEFAULT_PRIORITY,MESSAGE_LIFESPAN);
            LOG.info("[{}] game solution : {}",className,xmlText);
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }

    /*
     - send start map to sudoku resolve units to start reduction
    */
    public void putSessionKeys() {
            
        try {
            MapMessage mapMessage;
            Iterator<String> it = startBean.getIterator();
            while ( it.hasNext() ) {
                String peerId = it.next();
                mapMessage = getSession().createMapMessage();
                mapMessage.setStringProperty("peer",peerId);
                mapMessage.setString("gameId",startBean.gameId);
                mapMessage.setString("action","preset");
                send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                            HIGH_PRIORITY,MESSAGE_LIFESPAN);
            }
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }                

    /*
     - send start map to sudoku resolve units to start reduction
    */
    public void putStartMap() {
            
        try {
            Iterator<String> it = startBean.getIterator();
            while ( it.hasNext() ) {
                String peerId = it.next();
                MapMessage mapMessage = getSession().createMapMessage();
                mapMessage.setStringProperty("peer",peerId);
                mapMessage.setString("action","start");
                mapMessage.setString("gameId",startBean.gameId);
                mapMessage.setString("solved",startBean.getSolvent(peerId));
                send("resolve-reduce",mapMessage,DeliveryMode.NON_PERSISTENT,
                                            HIGH_PRIORITY,MESSAGE_LIFESPAN);
            }
        } catch (JMSException ex) {
            LOG.error("[{}] message send failure",className,ex);
        }
    }    
}
