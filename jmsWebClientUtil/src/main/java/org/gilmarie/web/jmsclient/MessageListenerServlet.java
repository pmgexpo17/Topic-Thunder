/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gilmarie.web.jmsclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pmg.jms.handler.JmsAjaxBean;
import org.pmg.jms.handler.XStreamHandler;

/**
 * A servlet for sending and receiving messages to/from JMS destinations using
 * HTTP POST for sending and HTTP GET for receiving. <p/> 
 * <dl>
 * <dt>defaultReadTimeout</dt>
 * <dd>The default time in ms to wait for messages. May be overridden by a
 * request using the 'timeout' parameter</dd>
 * <dt>maximumReadTimeout</dt>
 * <dd>The maximum value a request may specify for the 'timeout' parameter</dd>
 * <dt>maximumMessages</dt>
 * <dd>maximum messages to send per response</dd>
 * <dt></dt>
 * <dd></dd>
 * </dl>
 * 
 * 
 */
public class MessageListenerServlet extends MessageServletSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MessageListenerServlet.class);
    private final XStreamHandler xmlHandler = new XStreamHandler();    
    private ScheduledExecutorService scheduledService;
    private ScheduledFuture reclaimFuture;    
    private String clientMode;
    private final HashMap<String,JmsAjaxClient> ajaxClients = new HashMap<>();    
    private HttpSession session;

    /*
     * Start the client reclaim schedule
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        
        super.init(servletConfig);
        scheduledService = Executors.newScheduledThreadPool(6);
        setReclaimSchedule();
    }
    
    /**
     * Sends a message to a destination or manage subscriptions.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {

        // lets turn the HTTP post into a JMS Message
        JmsAjaxClient client = getAjaxClient( request );
        synchronized (client) {

            try {            
                parseRequest(request);
                if (clientMode.equals("poll")) {
                    doMessages(client, request, response);
                    return;
                }
                JmsAjaxBean ajaxBean = getAjaxBean(request);
                ajaxBean.destName = request.getParameter("destination");
                client.handle(ajaxBean);
            } catch (JMSException ex) {
                LOG.error("[{}] ajax jms action error",className,ex);
            }

            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    /**
     * Supports a HTTP DELETE to be equivalent of consuming a singe message
     * from a queue
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {
                
        JmsAjaxClient client = getAjaxClient(request);
        synchronized (client) {
            if (!zeroMessageActivity(client,request,response))
                doMessages(client, request, response);
        }
    }

    /**
     * Retrieves and returns messages from a consumer returned by a jetty 
     * continuation
     * 
     * @param client
     * @param request
     * @param response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    protected void doMessages(JmsAjaxClient client, HttpServletRequest request, 
            HttpServletResponse response) throws IOException, ServletException {

        LOG.info("[{}] messages are cached for retrieval",className);
        
        // prepare the response
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        
        TextMessage message = null;
        if (request.getAttribute("undelivered_message") != null) {
            UndeliveredAjaxMessage cachedMessage = (UndeliveredAjaxMessage)
                                    request.getAttribute("undelivered_message");
        
            message = (TextMessage) cachedMessage.getMessage();
        }
        try {
            String state = request.getParameter("state");
            StringWriter swriter = client.getAjaxResponse(state,message);
            String xmlResponse = swriter.toString();
            response.getWriter().println(xmlResponse);
        } catch (JMSException ex) {
            LOG.error("[{}] ajax client response error ",className,ex);
        }
    }

    //protected 
    /**
     * Reads a message from a destination up to some specific timeout period
     * 
     * @param client, the jms client that handles jms transmittal exchange
     * @param request
     * @param response
     * @return 
     * @throws java.io.IOException
     */

    public boolean zeroMessageActivity(JmsAjaxClient client,
                        HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        String state = request.getParameter("state");

        if (!client.zeroMessageActivity(state))
            return false;
            
        // prepare the response
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        return true;
    }

    /**
     * @param request
     * @return the timeout value for read requests which is always >= 0 and <=
     *         maximumReadTimeout to avoid DoS attacks
     */
    protected long getReadTimeout(HttpServletRequest request) {
        long answer = defaultReadTimeout;

        String name = request.getParameter("timeout");
        if (name != null) {
            answer = asLong(name);
        }
        if (answer < 0 || answer > maximumReadTimeout) {
            answer = maximumReadTimeout;
        }
        return answer;
    }
    
    private boolean isMessageBodyType(HttpServletRequest request) {
        
        return request.getContentLength() > 0 && 
                (request.getContentType() == null || 
                    !request.getContentType().toLowerCase().
                               startsWith("application/x-www-form-urlencoded"));
    }
    
    private void parseRequest(HttpServletRequest request) {
        
        String message = request.getParameter("message");
        if ( request.getParameter("poll") != null && 
                        "true".equals(request.getParameter("poll")) ) {
            clientMode = "poll";
            LOG.info("[{}] got poll request : {}",className,message);
        }
        else if (isMessageBodyType(request)) {
            clientMode = "postBody";
            LOG.info("[{}] got postBody type request",className);
        }
        else {
            clientMode = "post";
            LOG.info("[{}] got post request : {}",className,message);
        }
    }

    private JmsAjaxBean getAjaxBean(HttpServletRequest request) 
                                                    throws ServletException {
        
        String xmlText = "";
        switch (clientMode) {
            case "post" :
                xmlText = request.getParameter("message");
                break;
            case "postBody" :
                xmlText = getPostedMessageBody(request);
                break;
        }  
        return xmlHandler.getAjaxBean(xmlText);
    }
    /**
     * @param request
     * @return the text that was posted to the servlet which is used as the body
     *         of the message to be sent
     * @throws javax.servlet.ServletException
     */
    protected String getPostedMessageBody(HttpServletRequest request) 
                                                    throws ServletException {
        String answer = request.getParameter("body");
        if (answer == null && "text/xml".equals(request.getContentType())) {
            // lets read the message body instead
            StringBuilder buffer = new StringBuilder();
            try {
                BufferedReader reader = request.getReader();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    buffer.append(line);
                    buffer.append("\n");
                }
            } catch (IOException ex) {
                String errLog = String.format("[%s] failed to read form body",
                                                                    className);
                throw new ServletException(errLog,ex);
            }                
            return buffer.toString();
        }
        return answer;
    }

    private String getTimestamp() {

        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

    /*
     * Return the AjaxWebClient for this session+clientId.
     * Create one if it does not already exist.
     */
    protected JmsAjaxClient getAjaxClient( HttpServletRequest request ) {
        long now = (new Date()).getTime();
        session = request.getSession(true);
        
        String clientId = request.getParameter("clientId");      
        // if user doesn't supply a clientId we'll just use a default.
        if( clientId == null )
            clientId = "defaultClient";
        String sessionKey = session.getId() + '-' + clientId;
        
        JmsAjaxClient client = ajaxClients.get(sessionKey);
        synchronized (ajaxClients) {
            if( client == null ) {
                client = new JmsAjaxClient(clientId,session.getId(),
                                                            maximumReadTimeout);
                ajaxClients.put(sessionKey,client);
                LOG.info("[{}] created new JmsAjaxClient with key : {}",
                                                          className,sessionKey);
            }
        }
        if (!reclaimScheduleIsActive())
            setReclaimSchedule();
        return client;
    }
    
    @Override
    public void destroy() {

        for( String sessionKey: ajaxClients.keySet() )
            ajaxClients.get(sessionKey).closeTransmittel();
        ajaxClients.clear();
    }
    
    private boolean reclaimScheduleIsActive() {
        
        if (reclaimFuture == null)
            return false;
        return !(reclaimFuture.isCancelled() || reclaimFuture.isDone());
    }
    
    private void setReclaimSchedule() {
        
        LOG.info("[{}] starting resource reclaim schedule",className);
        reclaimFuture = scheduledService.scheduleAtFixedRate(new GameTimeout(),3,60,
                                                            TimeUnit.SECONDS);
    }
    /*
     * TO DO : make a cache of QueueTransmittal for recycling instead of creating
     * a new consumer and producer for every new client
     */

    private class GameTimeout implements Runnable {
        
        @Override
        public void run() {

            String logText = 
                "[{}] eval expired JmsAjaxClients, not in current session : {}";
            LOG.info(logText,className,session.getId());
            logText = "[{}] Removing zero message activity client : {}";
                                                    
            synchronized( ajaxClients ) {
                ArrayList<String> reclaimed = new ArrayList<>();
                for( String sessionKey: ajaxClients.keySet() ) {
                    JmsAjaxClient ajaxClient = ajaxClients.get(sessionKey);
                    synchronized (ajaxClient) {
                        if( ajaxClient.zeroPeriodActivity(session.getId()) ) {
                            ajaxClient.closeTransmittel();
                            reclaimed.add(sessionKey);
                        }
                    }
                    Iterator<String> it = reclaimed.iterator(); 
                    while (it.hasNext()) {
                        sessionKey = it.next();
                        ajaxClients.remove(sessionKey);
                        LOG.info(logText,className,sessionKey);
                    }
                }
            }
        }
    }
}
