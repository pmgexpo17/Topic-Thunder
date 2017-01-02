/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gilmarie.web.jmsclient;

import java.util.Iterator;
import java.util.Random;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.MessageAvailableConsumer;
import org.pmg.jms.handler.JmsAjaxBean;
import org.pmg.jms.handler.XStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class QueueTransmittal {

    private static final Logger LOG = LoggerFactory.getLogger(QueueTransmittal.class);
    private final XStreamHandler xmlHandler = new XStreamHandler();
    private static final int MESSAGE_LIFESPAN = 5000;  // milliseconds (3 seconds)    
    private static final long maximumReadTimeout = 25000;
    protected final String className;
    private final Session session;
    private final QueueCache qcache;
    private MessageAvailableConsumer consumer;
    private MessageProducer producer;
    private AjaxListener listener;    

    public QueueTransmittal(Session session) {
        
        className = getClass().getSimpleName();
        qcache = new QueueCache();
        this.session = session;
    }

    public synchronized void clearMessageCache() {
        
        listener.getMessageCache().clear();
    }

    public synchronized void close() {

        try {
            if (consumer != null) {
                consumer.setAvailableListener(null);
                consumer.close();
            }
            if (producer != null)
                producer.close();
        } catch (JMSException ex) {
            LOG.error("[{}] consumer, producer close error",className,ex);
        }
    }

    public void join(String destName) throws JMSException {
        
        if (destName.isEmpty()) {
            LOG.info("[{}] consumer creation aborted, destination is required",
                                                                    className);
            return;
        }
        qcache.peerDest = getQueueForAmq(destName);        
    }

    public synchronized Iterator<UndeliveredAjaxMessage> getMessageIterator() {
        
        return listener.getMessageCache().iterator();
    }

    private Queue getQueueForAmq(String peerDest) throws JMSException {

        if (peerDest.contains("://"))
            peerDest = peerDest.substring(peerDest.indexOf("://") + 3);
        return session.createQueue(peerDest);
    }

    private String getTimestamp() {

        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

    public boolean hasMessageAvailable() {
        
        return !listener.getMessageCache().isEmpty();
    }
    
    public TextMessage readMessage() throws JMSException {
        
        if (consumer.getAvailableListener() == null)
            return null;
   
        // Look for any available messages
        return (TextMessage) consumer.receive(10);
    }
    

    public void send(JmsAjaxBean msgBean) throws JMSException {

        LOG.info("[{}] message bean : {}",className,msgBean.toString());
        TextMessage message = session.createTextMessage();
        if (msgBean.action.equals("ping"))
            message.setStringProperty("clientId",msgBean.getFrom());
        String xmlText = xmlHandler.getXmlText(msgBean,false);        
        message.setText(xmlText);
        message.setJMSReplyTo(qcache.peerReplyTo); 
        String correlationId = this.getTimestamp();
        message.setJMSCorrelationID(correlationId);
        LOG.info("[{}[{}] sending message : {}",className,msgBean.state,
                                                                       xmlText);
        producer.send(qcache.peerDest,message,
                        DeliveryMode.NON_PERSISTENT,
                        Message.DEFAULT_PRIORITY,
                        MESSAGE_LIFESPAN);
    }    

    public void start() {
        
        try {
            listener = new AjaxListener(maximumReadTimeout);
            producer = session.createProducer(null);            
            qcache.peerReplyTo = session.createTemporaryQueue();
            consumer = (MessageAvailableConsumer) 
                                     session.createConsumer(qcache.peerReplyTo);
            consumer.setAvailableListener(listener);
        } catch (JMSException ex) {
            LOG.error("[{}] consumer, producer create error",className,ex);
        }
    }
    
    private class QueueCache {
        
        public Queue peerReplyTo;
        public Queue peerDest;
    }
}
