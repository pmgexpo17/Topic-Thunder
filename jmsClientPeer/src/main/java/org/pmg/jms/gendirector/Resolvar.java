/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gendirector;

import com.google.inject.Provider;
import javax.jms.JMSException;

/**
 *
 * @author peter
 */
public interface Resolvar extends Provider<Resolve> {

    public Resolve apply() throws JMSException;
}
