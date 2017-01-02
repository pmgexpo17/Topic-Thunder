/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gilmarie.web.jmsclient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.pmg.jms.handler.JmsAjaxBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class JmsAjaxClient extends JmsAmqClient {
    private static final Logger LOG = LoggerFactory.getLogger(JmsAjaxClient.class);    
    private final HashMap<String,QueueTransmittal> transmittel = new HashMap<>();
    private final String webClientId;
    private final String sessionId;
    private volatile String clientState;
    private volatile long lastAccess;
    private volatile int activityScore = 0;

    public JmsAjaxClient(String webClientId, String sessionId, 
                                                    long maximumReadTimeout) {
        this.webClientId = webClientId;
        this.sessionId = sessionId;
        lastAccess = System.nanoTime();
        clientState = "ready";
    }

    public void handle(JmsAjaxBean ajax) throws JMSException {
        
        switch (ajax.method) {
            case "listen" :
                if (transmittel.containsKey(ajax.state))
                    break;
                QueueTransmittal transmittal = new QueueTransmittal(session);
                transmittal.start();
                transmittal.join(ajax.destName);
                transmittel.put(ajax.state,transmittal);
                LOG.info("[{}[{}] subscribed to destination : {}",className,
                                                    ajax.state,ajax.destName);
                break;
            case "unlisten" :
                if (!transmittel.containsKey(ajax.state))
                    break;
                transmittel.remove(ajax.state).close();
                LOG.info("[{}[{}] unsubscribed from destination : {}",className,
                                                    ajax.state,ajax.destName);
                break;
            case "send" :
                if (!transmittel.containsKey(ajax.state)) {
                    ajax.method = "listen";
                    handle(ajax);
                }
                clientState = ajax.state;
                LOG.info("[{}] state is updated : {}",className,clientState);
                transmittel.get(clientState).send(ajax);
                activityScore++;
                break;
            case "reset" :
                clientState = "ready";
                break;
            default:
                LOG.warn("[{}] unknown ajax method : {}",className,
                                                               ajax.toString());
                break;
        }
    }
    
    public boolean zeroMessageActivity(String state) {
        
        if (!transmittel.containsKey(state))
            return true;
        return !transmittel.get(state).hasMessageAvailable();
    }

    public String getXmlText(String state, TextMessage message) throws JMSException {

        activityScore++;
        String xmlRoot = String.format("<response id=\"%s\">",state);
        try {
            String xmlText = xmlRoot + message.getText() + "</response>";
            LOG.info("[{}] client message : {}",className,xmlText);
            return xmlText;
        } catch (JMSException ex) {
            LOG.error("[{}] get text message failed",className,ex);
        }
        return xmlRoot + "</response>";
    }
            
    public StringWriter getAjaxResponse(String state, TextMessage message) 
                                                          throws JMSException {
        synchronized (state) {
            
            if (!transmittel.containsKey(state))
                return getErrorResponse(state);
            
            StringWriter swriter = new StringWriter();
            PrintWriter writer = new PrintWriter(swriter);
            writer.println("<ajax-response>");

            if (message != null) 
                writer.println(getXmlText(state,message));

            Iterator<UndeliveredAjaxMessage> messageCache =
                             transmittel.get(state).getMessageIterator();        
            while (messageCache.hasNext()) {
                message = (TextMessage) messageCache.next().getMessage();
                writer.println(getXmlText(state,message));
            }
            transmittel.get(state).clearMessageCache();
            writer.println("</ajax-response>");
            writer.flush();
            return swriter;
        }
    }
    
    private StringWriter getErrorResponse(String state) {
    
        LOG.error("[{}] transmittal not installed for state : {}",className,state);

        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        writer.println("<ajax-response>");
        writer.println("</ajax-response>");
        writer.flush();
        return swriter;
    }
    
    public boolean zeroPeriodActivity(String sessionId) {
        
        // only reclaims clients which are not serving the current session
        if (this.sessionId.equals(sessionId))
            return false;
        synchronized (clientState) {
            if (!clientState.equals("ready"))
                return false;
            boolean zeroStatus = activityScore == 0;
            activityScore = 0;
            LOG.info("[{}] zero period activity status : {}",className,
                                                                    zeroStatus);
            return zeroStatus;
        }
    }
    
    public void closeTransmittel() {
        
        LOG.info("[{}] resource reclaim, closing transmittals",className);
        for( String key: transmittel.keySet() )
            transmittel.get(key).close();
        transmittel.clear();
    }
}
