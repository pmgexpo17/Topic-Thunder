/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import java.util.concurrent.Executor;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 *
 * @author peter
 */
public interface Deliverable {

    public Executor getExecutor();
    public Routable handle(Message message) throws JMSException;
    public void setRoute(String route);    
}
