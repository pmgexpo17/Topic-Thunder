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
package org.pmg.jms.genclient;

import org.pmg.jms.genbase.Container;
import org.pmg.jms.genbase.Destroyable;
import org.pmg.jms.genbase.Dumpable;
import org.pmg.jms.genbase.LifeCycle;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.genhandler.Handler;

/**
 * ServicePeer defines a jms component container, which handles lifecycle 
 * creation and completion. 
 * @author Peter A McGill
 */
public interface ServicePeer extends 
                                  Container, Destroyable, Dumpable, LifeCycle {
                                                        
    public Connector getConnector();
    public Controller getController();
    public void addHandler(String route,Handler handler);
}
