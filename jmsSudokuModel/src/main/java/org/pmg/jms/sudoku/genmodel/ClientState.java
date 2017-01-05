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

import org.pmg.jms.genclient.Routable;
import org.pmg.jms.gendirector.Statement;
import org.pmg.jms.genhandler.JmsAjaxBean;

/**
 * Holds state machine (SM) resources and status for containerized shared data
 * application. This class is required for the SudokuModelScope binding
 * @author peter
 */
@SudokuModelScoped
public class ClientState implements Statement {
    
    public volatile Routable delegate;
    public volatile JmsAjaxBean ajaxBean;
    public volatile String current;
    public volatile String next;
    public volatile String action;
    public volatile String transition;

    @Override
    public void init(String initState) {
        
        current = initState;
        next = initState;
    }

    @Override
    public void iterate(String next) {
        
        this.next = next;
        current = next;
    }
    
    @Override
    public void iterate() {
        
        current = next;
    }

    @Override
    public void setDelegate(Routable delegate) {
        
        this.delegate = delegate;
    }
}
