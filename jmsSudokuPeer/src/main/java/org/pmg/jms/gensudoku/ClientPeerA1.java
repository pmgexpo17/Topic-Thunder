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
package org.pmg.jms.gensudoku;

import com.google.inject.Inject;
import org.pmg.jms.genclient.ClientPeer;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.gendirector.Controller;

/**
 * Injects an OpenWire connector plus a controller
 * @author peter
 */
public class ClientPeerA1 extends ClientPeer {

    @Inject
    public ClientPeerA1(@OpenWire Connector connector, Controller controller) {
        
        super(connector, controller);
    }
}
