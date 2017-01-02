/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gendirector;

import javax.jms.JMSException;

/**
 *
 * @author peter
 */
@FunctionalInterface
public interface Resolve {
    
    public boolean hasNext() throws JMSException;
}
