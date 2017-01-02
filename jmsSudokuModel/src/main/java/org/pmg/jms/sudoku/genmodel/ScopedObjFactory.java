/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genmodel;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.sudoku.genmonitor.TrialMonitor;
import org.pmg.jms.sudoku.genresolvar.Reductive;

/**
 *
 * @author peter
 */
public class ScopedObjFactory {

    @Inject @OpenWire Provider<Connector> connectorPrvdr;
    @Inject Provider<Controller> controllerPrvdr;
    @Inject Provider<Reductive> reductorBeanPrvdr;    
    @Inject Provider<Statement> clientStatePrvdr;
    @Inject Provider<TrialMonitor> governorPrvdr;
    
    public @OpenWire Connector getConnector() {

        return connectorPrvdr.get();
    }
    
    public Controller getController() {
        
        return controllerPrvdr.get();
    }

    public Reductive getReductorBean() {
        
        return reductorBeanPrvdr.get();
    }

    public Statement getClientState() {
        
        return clientStatePrvdr.get();
    }
    
    public TrialMonitor getTrialGovernor() {
        
        return governorPrvdr.get();
    }
}
