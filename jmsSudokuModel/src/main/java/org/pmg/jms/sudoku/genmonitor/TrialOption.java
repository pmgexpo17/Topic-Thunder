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
package org.pmg.jms.sudoku.genmonitor;

/**
 *
 * @author peter
 */
public class TrialOption {

    public String peerId = "";
    public String[] allPeers;
    public String key = "";
    public String options = "";        
    public String solvent = "";
    public int size = 0;
    public int rank = 0;
        
    public TrialOption() {
        
        this("","");
    }
    
    public TrialOption(String key, String options) {
        
        this.key = key;
        this.options = options;
        size = options.length();
        rank = 9; // default rank
    }
    
    @Override
    public String toString() {
        
        return String.format("%s:%s:%d:%d",key,options,size,rank);
    }
    
    public boolean hasNextOption() {
        
        return !options.isEmpty();
    }    
    
    public TrialOption getNextOption() {
        
        String option = options.substring(0,1);
        solvent = key + "," + option; // used by director.resolver to resolve
        options = options.replace(option,"");
        size--;
        return this;
    }
}
