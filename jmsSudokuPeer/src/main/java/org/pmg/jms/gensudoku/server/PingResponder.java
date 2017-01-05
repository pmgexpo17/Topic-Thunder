/**
 * Copyright (c) 2016 Peter A McGill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
**/
package org.pmg.jms.gensudoku.server;

import com.google.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.apache.activemq.MessageAvailableListener;
import org.pmg.jms.genclient.ClientMember;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.genconnect.SessionProvider;
import org.pmg.jms.genhandler.XStreamHandler;

/**
 * Delivers a ping message response to ping requests by webClient initialization
 * @author peter
 */
public class PingResponder extends ClientMember implements 
                                                    MessageAvailableListener {

    private final XStreamHandler xmlHandler = new XStreamHandler();
    private MessageProducer respondant;

    @Inject
    public PingResponder(@OpenWire SessionProvider sessionPrvdr) {

        super(sessionPrvdr);
    }
        
    @Override
    protected void doStart() throws JMSException, Exception
    {
        super.doStart();
        respondant = getSession().createProducer(null);
        setListener(this);
    }

    @Override
    protected void doStop() throws JMSException, Exception
    {
        super.doStop();
        respondant.close();
    }
    
    @Override
    public void onMessageAvailable(MessageConsumer consumer)
    {
        try
        {
            TextMessage request = (TextMessage) consumer.receive(10);
            acknowledgePing(request);
        } catch (JMSException ex) {
            LOG.error("[{}] ping acknowledge failed : {}",className,
                                                                destName,ex);
        }
    }

    public void acknowledgePing(TextMessage request) throws JMSException {
            
        LOG.info("[{}] returning ping query : {}",className,request.getText());        
        String webClientId = request.getStringProperty("clientId");
        Queue replyQueue = (Queue) request.getJMSReplyTo();
        TextMessage textMsg = getSession().createTextMessage();
        textMsg.setStringProperty("state","ping");
        textMsg.setText(request.getText());
        respondant.send(replyQueue,textMsg);
        LOG.info("[{}] acknowledged web client[{}] ping query",className,
                                                                   webClientId);            
    }
}
