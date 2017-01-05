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

import com.google.inject.name.Named;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.gendirector.AppDirector;
import org.pmg.jms.gensudoku.director.ResolveUnitA1;
import org.pmg.jms.gensudoku.director.ResponseUnitA1;

/**
 * Compliments FactoryModuleBuilder binding in GameModule
 * @author peter
 */
public interface PeerFactory {

    ClientPeerA1 getClientPeer();
    @Named("game") Messenger<MessageProvider,GamePropogate> 
                                            getGameMessenger(String sessionId);
    AppDirector<ResolveUnitA1,ResponseUnitA1> getGameDirector(String sessionId);    
}
