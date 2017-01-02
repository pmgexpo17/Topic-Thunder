/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gilmarie.web.jmsclient;

import java.util.ArrayList;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import org.apache.activemq.MessageAvailableListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class AjaxListener implements MessageAvailableListener {
    private static final Logger LOG = LoggerFactory.getLogger(AjaxListener.class);
    
    private final ArrayList<UndeliveredAjaxMessage> undeliveredMessages;

    AjaxListener(long maximumReadTimeout) {
        
        this.undeliveredMessages = new ArrayList<>();
    }

    public ArrayList<UndeliveredAjaxMessage> getMessageCache() {    
        
        return undeliveredMessages;
    }
    
    @Override
    public synchronized void onMessageAvailable(MessageConsumer consumer) {

        try {
            Message message = consumer.receive(10);
            bufferMessageForDelivery( message, consumer );
        } catch (Exception e) {
            LOG.error("[AjaxListener] Error receiving message " + e, e);
        }
    }
    
    public void bufferMessageForDelivery( Message message, MessageConsumer consumer ) {
        try {
            if( message != null ) {
                if (message instanceof TextMessage)
                    LOG.info("[AjaxListener] got message : {}",((TextMessage) message).getText());
                synchronized( undeliveredMessages ) {
                    undeliveredMessages.add( new UndeliveredAjaxMessage( message, consumer ) );
                }
            }
        } catch (Exception e) {
            LOG.error("[AjaxListener] Error receiving message " + e, e);
        }
    }
}
