/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmodel;

import org.pmg.jms.genclient.Routable;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.genhandler.JmsAjaxBean;

/**
 *
 * @author peter
 */
@SudokuModelScoped
public class ClientState implements Statement {
    
    public volatile Routable delegate;
    public volatile JmsAjaxBean ajaxBean;
    public volatile String current;
    public volatile String next;
    public volatile String action;
    public volatile String transition;

    @Override
    public void iterate(String next) {
        
        this.next = next;
        current = next;
    }
    
    @Override
    public void iterate() {
        
        current = next;
    }

    @Override
    public void setDelegate(Routable delegate) {
        
        this.delegate = delegate;
    }
}
