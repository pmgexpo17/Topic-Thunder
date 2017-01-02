/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gendirector;

import org.pmg.jms.genclient.Routable;

/**
 *
 * @author peter
 */
public interface Statement {

    public void iterate(String next);
    public void iterate();
    public void setDelegate(Routable delegate);
}
