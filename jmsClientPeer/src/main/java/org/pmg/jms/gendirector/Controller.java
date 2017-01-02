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
package org.pmg.jms.gendirector;

import java.util.concurrent.Executor;
import org.pmg.jms.genbase.LifeCycle;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genhandler.HandlerCollection;

/**
 * AppController is the engine for ClientPeer runnable task execution
 * @author Peter A McGill
 */
public interface Controller extends LifeCycle {
    
    public HandlerCollection getRouter();
    public void runApp(Routable delegate);
    public Executor getExecutor();
    public void destroy();
    public void showAllRoutes();    
}
