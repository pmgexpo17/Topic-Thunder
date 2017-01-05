/**
 * Copyright (c) 2016 Peter A McGill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
**/
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
 * Factory class for convenient bean provision
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
