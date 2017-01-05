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
package org.pmg.jms.genconfig;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import javax.jms.Connection;
import org.pmg.jms.genconnect.ConnectionProvider;
import org.pmg.jms.genconnect.Connector;
import org.pmg.jms.genconnect.OpenWire;
import org.pmg.jms.genconnect.OpenWireConnectPrvdr;
import org.pmg.jms.genconnect.OpenWireConnector;
import org.pmg.jms.genconnect.OpenWireSessionPrvdr;
import org.pmg.jms.genconnect.SessionAgent;
import org.pmg.jms.genconnect.SessionProvider;
import org.pmg.jms.gendirector.Controller;
import org.pmg.jms.gendirector.AppController;

/**
 - These Guice bindings are purposely incomplete
 - You must add :
 - 1.ServicePeer to ClientPeerDes binding, where ClientPeerDes extends
 -   ClientPeer and injects the connector transport type with annotation
 -   Eg @OpenWire transport is provided in the package
 - 2.Statement to ClientState binding, which depends on the scoping application
 -   Ie, if your ClientState scoping is not achievable with JmsClientScoped then
 -   create a new scope and ClientState bean and bind them
 - 3.ActiveMQConfig provides method or Provider binding
 - 4.SessionConfig provides method or Provider binding
 - 5.Annotated ThreadSize Integer binding
   @author Peter A McGill
 */
public class JmsClientModule extends AbstractModule {

    @Override
    protected void configure() {
        ThrowingProviderBinder
            .create(binder())
                .bind(ConnectionProvider.class, Connection.class)
                    .annotatedWith(OpenWire.class)
                        .to(OpenWireConnectPrvdr.class)
                            .asEagerSingleton();
        bind(Connector.class)
                .annotatedWith(OpenWire.class)
                    .to(OpenWireConnector.class);
        ThrowingProviderBinder
            .create(binder())
                .bind(SessionProvider.class, SessionAgent.class)
                    .annotatedWith(OpenWire.class)
                        .to(OpenWireSessionPrvdr.class);
        bind(Controller.class).to(AppController.class);

        final JmsClientScope jmsClientScope = new JmsClientScope();
        bindScope(JmsClientScoped.class, jmsClientScope);
        bind(JmsClientScope.class)
            .annotatedWith(Names.named("JmsClientScoped"))
                .toInstance(jmsClientScope);        
    }
}
