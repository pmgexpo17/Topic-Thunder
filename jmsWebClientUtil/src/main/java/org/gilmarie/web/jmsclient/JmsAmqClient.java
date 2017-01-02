/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gilmarie.web.jmsclient;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.servlet.ServletContext;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class JmsAmqClient {

    public static final String WEB_CLIENT_ATTRIBUTE = "org.apache.activemq.webclient";
    public static final String CONNECTION_FACTORY_ATTRIBUTE = "org.apache.activemq.connectionFactory";
    public static final String CONNECTION_FACTORY_PREFETCH_PARAM = "org.apache.activemq.connectionFactory.prefetch";
    public static final String CONNECTION_FACTORY_OPTIMIZE_ACK_PARAM = "org.apache.activemq.connectionFactory.optimizeAck";
    public static final String BROKER_URL_INIT_PARAM = "org.apache.activemq.brokerURL";
    public static final String SELECTOR_NAME = "org.apache.activemq.selectorName";
    protected final String className;
    private static final Logger LOG = LoggerFactory.getLogger(JmsAmqClient.class);

    private static transient ConnectionFactory factory;

    private transient Connection connection;
    protected transient Session session;    
    protected int deliveryMode = DeliveryMode.NON_PERSISTENT;

    public JmsAmqClient() {
        
        className = getClass().getSimpleName();
        if (factory == null) {
            throw new IllegalStateException("initContext(ServletContext) not called");
        }
        setSession();
    }

    protected static synchronized void initConnectionFactory(ServletContext servletContext) {
        if (factory == null) {
            factory = (ConnectionFactory)servletContext.getAttribute(CONNECTION_FACTORY_ATTRIBUTE);
        }
        if (factory == null) {
            String brokerURL = servletContext.getInitParameter(BROKER_URL_INIT_PARAM);

            LOG.debug("Value of: " + BROKER_URL_INIT_PARAM + " is: " + brokerURL);

            if (brokerURL == null) {
                String errmsg = "missing brokerURL, init-Param : %s";
                errmsg = String.format(errmsg,BROKER_URL_INIT_PARAM);
                throw new IllegalStateException(errmsg);
            }

            ActiveMQConnectionFactory amqfactory = new ActiveMQConnectionFactory(brokerURL);

            // Set prefetch policy for factory
            if (servletContext.getInitParameter(
                                CONNECTION_FACTORY_PREFETCH_PARAM) != null) {
                int prefetch = Integer.valueOf(
                    servletContext.getInitParameter(
                                CONNECTION_FACTORY_PREFETCH_PARAM)).intValue();
                amqfactory.getPrefetchPolicy().setAll(prefetch);
            }

            // Set optimize acknowledge setting
            if (servletContext.getInitParameter(
                               CONNECTION_FACTORY_OPTIMIZE_ACK_PARAM) != null) {
                boolean optimizeAck = Boolean.valueOf(
                    servletContext.getInitParameter(
                        CONNECTION_FACTORY_OPTIMIZE_ACK_PARAM)).booleanValue();
                amqfactory.setOptimizeAcknowledge(optimizeAck);
            }

            factory = amqfactory;

            servletContext.setAttribute(CONNECTION_FACTORY_ATTRIBUTE, factory);
        }
    }

    private Connection getConnection() throws JMSException {

        if (connection == null) {
            connection = factory.createConnection();
            connection.start();
        }
        return connection;
    }        

    private void setSession() {

        try {        
            if (session == null)
                session = createSession();
            LOG.info("[{}] session created",className);
        } catch (JMSException ex) {
            LOG.error("[{}] broker connection failed",className,ex);
        }
    }

    private Session createSession() throws JMSException {

        return getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
}
