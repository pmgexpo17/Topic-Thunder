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
package org.pmg.jms.sudoku.genmodel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.pmg.jms.genclient.MessageRouter;
import org.pmg.jms.genclient.Messenger;
import org.pmg.jms.genclient.ServicePeer;
import org.pmg.jms.genconnect.ActiveMQConfig;
import org.pmg.jms.genconnect.SessionConfig;
import org.pmg.jms.gendirector.ClientDirector;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.sudoku.genmonitor.ResolveUnitB2;
import org.pmg.jms.sudoku.genmonitor.ResponseUnitB2;
import org.pmg.jms.sudoku.genmonitor.TrialMonitor;
import org.pmg.jms.sudoku.genmonitor.TrialGovernor;
import org.pmg.jms.sudoku.genresolvar.Reductive;
import org.pmg.jms.sudoku.genresolvar.ReductorBean;
import org.pmg.jms.sudoku.genresolvar.ResolveUnitB1;
import org.pmg.jms.sudoku.genresolvar.ResponseUnitB1;

/**
 *
 * @author peter
 */
public class SudokuModule extends AbstractModule {

    @Override
    protected void configure() {

        final SudokuModelScope sudokuModelScope = new SudokuModelScope();
        bindScope(SudokuModelScoped.class, sudokuModelScope);
        bind(SudokuModelScope.class)
            .annotatedWith(Names.named("SudokuModelScoped"))
                .toInstance(sudokuModelScope);        

        bind(Reductive.class).to(ReductorBean.class);
        bind(Statement.class).to(ClientState.class);
        bind(TrialMonitor.class).to(TrialGovernor.class);
        install(new FactoryModuleBuilder().
            implement(ServicePeer.class, ClientPeerB1.class).
            implement(MessageRouter.class,
                new TypeLiteral<Messenger<MessageProvider,GamePropogate>>() {}).
            implement(ClientDirector.class,Names.named("monitor"),
                new TypeLiteral<Director<ResolveUnitB2,ResponseUnitB2>>() {}).
            implement(ClientDirector.class,Names.named("game"),
                new TypeLiteral<Director<ResolveUnitB1,ResponseUnitB1>>() {}).
            build(PeerFactory.class));
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
