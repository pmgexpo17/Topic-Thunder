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
import org.pmg.jms.genclient.ClientMember;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.genconnect.SessionProvider;

/**
 * Injects an OpenWire session provider
 * @author peter
 */
public class MessageProvider extends ClientMember {
 
    @Inject
    public MessageProvider(@OpenWire SessionProvider sessionPrvdr) {        
        
        super(sessionPrvdr);
    }
}
