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
public interface Quicken {
    
    public void respond() throws JMSException;
}
