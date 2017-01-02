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
import org.pmg.jms.gensudoku.server.JoinPropogate;
import org.pmg.jms.gensudoku.server.JoinResponder;
import org.pmg.jms.gensudoku.server.NextGameResponder;
import org.pmg.jms.gensudoku.server.PingResponder;

/**
 *
 * @author peter
 */
public interface GameFactory {

    ClientPeerA1 getClientPeer();
    @Named("join") Messenger<MessageProvider,JoinPropogate> getJoinMessenger();
    @Named("game") Messenger<MessageProvider,GamePropogate> getResetMessenger();
    public PingResponder getPingResponder();
    public JoinResponder getJoinResponder();
    public NextGameResponder getNextGameResponder(String sessionId);
}
