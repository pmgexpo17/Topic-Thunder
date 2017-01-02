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
package org.pmg.jms.genhandler;

import java.util.HashMap;
import javax.jms.JMSException;
import org.pmg.jms.genbase.Destroyable;
import org.pmg.jms.genbase.LifeCycle;
import org.pmg.jms.genclient.Routable;

/**
 * A handler collection stores all handlers for a ClientPeer application
 * @author Peter A McGill
 */
public interface HandlerCollection extends LifeCycle, Destroyable {

    public void addHandler(String routeId, Handler handler);

    public HashMap<String,Handler> getHandlers();
    
    public Handler removeHandler(String routeId);
    
    public void setDefaultHandler(Handler handler);
    
    public void handle(Routable delegate) throws JMSException;

}
