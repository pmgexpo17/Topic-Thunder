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

import com.google.inject.Inject;
import javax.jms.JMSException;
import org.pmg.jms.genbase.AbstractLifeCycle;
import org.pmg.jms.genclient.Routable;

/**
 * Handles a ClientPeer application task by delegating the task to a
 * sub-handler
 * @author Peter A McGill
 */
public class SuperHandler extends AbstractLifeCycle implements Handler {

    protected Handler handler;

    public Handler getHandler() {
        return handler;
    }

    @Inject
    public void setHandler(Handler handler) {
        if (isStarted())
            throw new IllegalStateException(STARTED);
        
        this.handler = handler;
    }

    @Override
    public void handle(Routable delegate) throws JMSException
    {
        if (handler != null && isStarted()) {
            handler.handle(delegate);
        }
    }

    @Override
    public void destroy()
    {
        if (!isStopped())
            throw new IllegalStateException("!STOPPED");
        Handler child=getHandler();
        if (child!=null)
           setHandler(null);
    }
}
