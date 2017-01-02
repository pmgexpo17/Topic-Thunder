/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gendirector;

import com.google.inject.Provider;
import javax.jms.JMSException;
import org.pmg.jms.genbase.LifeCycle;

/**
 *
 * @author peter
 */
public interface Respondar extends LifeCycle, Provider<Quicken> {

    public void configure() throws JMSException;
    public Quicken apply() throws JMSException;
}
