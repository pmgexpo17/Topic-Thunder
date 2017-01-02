/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.genclient;

import javax.jms.JMSException;
import org.apache.activemq.MessageAvailableListener;
import org.pmg.jms.genbase.LifeCycle;

/**
 *
 * @author peter
 * @param <X>
 * @param <Y>
 */
public interface MessageRouter<X extends Membership, Y extends Deliverable>
                                extends LifeCycle, MessageAvailableListener {
    
    public void init(String destName, String selector) throws JMSException;
    public void setRoute(String route);
    public void addSubRoute(String subRoute);
}
