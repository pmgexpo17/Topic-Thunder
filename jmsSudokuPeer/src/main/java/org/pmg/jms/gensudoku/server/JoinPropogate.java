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
import java.util.HashMap;
import java.util.concurrent.Executor;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.pmg.jms.genclient.Deliverable;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genclient.TextDelegate;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.genhandler.JmsAjaxBean;
import org.pmg.jms.genhandler.XStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class JoinPropogate implements Deliverable {

    private static final Logger LOG = LoggerFactory.getLogger(JoinPropogate.class);
    private final String className = getClass().getSimpleName();
    private final Controller controller;
    private String routeId;

    @Inject
    public JoinPropogate(Controller controller) {

        this.controller = controller;
    }

    @Override
    public Executor getExecutor() {
        
        return controller.getExecutor();
    }

    @Override
    public Routable handle(Message message) throws JMSException {
        
        return new Delegate((TextMessage) message, routeId);
    }

    @Override
    public void setRoute(String routeId) {
        
        this.routeId = routeId;
    }

    protected class Delegate extends TextDelegate {
        
        private final XStreamHandler xmlHandler = new XStreamHandler();

        public Delegate(TextMessage textMessage, String routeId) {
            
            super(textMessage, routeId);
            JmsAjaxBean ajaxBean = xmlHandler.getAjaxBean(textMessage);
            setString("clientId",ajaxBean.getFrom());
            setString("action",ajaxBean.getAction());
            setObject("startMap",ajaxBean.getMessage());
            LOG.info("[{}] clientId :{}",className,ajaxBean.getFrom());            
        }

        @Override
        public void run() {

            controller.runApp(this);
        }
    }
}
