/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmonitor;

import org.pmg.jms.sudoku.genresolvar.TrialAgent;

/**
 * Required for Guice binding
 * @author peter
 */
public interface TrialMonitor {

    public void addTrialAgent(TrialAgent trialAgent);
    public void clear();
    public boolean gameIsStalled(String peerId);
    public boolean gameIsStalled();
    public int getRetroIndex() throws RollbackException;
    public TrialOption getTrialOption();            
    public boolean hasTrialOption(String action);
    public void reset();
    public void rollbackReductor();
    public void setState(String trialState);
}
