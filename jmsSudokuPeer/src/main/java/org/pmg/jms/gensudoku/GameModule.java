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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.genclient.MessageRouter;
import org.pmg.jms.genclient.ServicePeer;
import org.pmg.jms.genconnect.ActiveMQConfig;
import org.pmg.jms.genconnect.SessionConfig;
import org.pmg.jms.gendirector.AppDirector;
import org.pmg.jms.gendirector.ClientDirector;
import org.pmg.jms.gendirector.ClientState;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.gensudoku.director.ResolveUnitA1;
import org.pmg.jms.gensudoku.director.ResponseUnitA1;
import org.pmg.jms.gensudoku.server.JoinPropogate;

/**
 * Guice bindings and JMS config providers
 * @author peter
 */
public class GameModule extends AbstractModule {
    
    @Override
    protected void configure() {

        install(new FactoryModuleBuilder().
            implement(ServicePeer.class, ClientPeerA1.class).
            implement(MessageRouter.class,Names.named("game"),
                new TypeLiteral<Messenger<MessageProvider,GamePropogate>>() {}).
            implement(ClientDirector.class,
                new TypeLiteral<AppDirector<ResolveUnitA1,ResponseUnitA1>>() {}).
            build(PeerFactory.class));
        install(new FactoryModuleBuilder().
            implement(MessageRouter.class,Names.named("join"),
                new TypeLiteral<Messenger<MessageProvider,JoinPropogate>>() {}).
            build(GameFactory.class));
        
        bind(Statement.class).to(ClientState.class);
        bind(Integer.class).annotatedWith(Names.named("ThreadSize")).
                                                                toInstance(27);
    }

    @Provides
    @Singleton
    ActiveMQConfig getAMQConfig() {
    
        ActiveMQConfig config = new ActiveMQConfig();
        config.user = env("ACTIVEMQ_USER", "admin");
        config.pwd = env("ACTIVEMQ_PASSWORD", "password");
        config.url = "tcp://localhost:61616";
        return config;
    }  

    private static String env(String key, String defaultValue) {
        
        String rc = System.getenv(key);
        if( rc== null )
            return defaultValue;
        return rc;
    }
   
    @Provides
    @Singleton
    SessionConfig getSessionConfig() {
        
        SessionConfig config = new SessionConfig();
        config.transacted = false;
        config.acknowledgeMode = javax.jms.Session.AUTO_ACKNOWLEDGE;
        return config;
    }
}
