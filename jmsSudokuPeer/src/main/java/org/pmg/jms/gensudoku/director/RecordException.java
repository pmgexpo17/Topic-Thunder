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
package org.pmg.jms.gensudoku.director;

/**
 *
 * @author peter
 */
public class RecordException extends Exception {

    private final String message;
    
    public RecordException(String key, String solvent, String _solvent) {
        
        String errorTxt = 
             "[%s] new solution for cell[%s] : %s is different to current : %s";
        this.message = String.format(errorTxt,"RecordException",key,solvent,
                                                                      _solvent);
    }
    
    public RecordException(String peerId, String key, String solvent, 
                                                             String resolved) {
        String errorTxt = 
             "[%s] duplicate solvent for unit[%s[%s] : %s in %s";
            this.message = String.format(errorTxt,"RecordException",peerId,key,
                                                              solvent,resolved);            
    }

    public RecordException(String peerId, String solvant) {
        
        String errorTxt = 
        "[ResolveException] last key does not have a paired option : %s";
        this.message = String.format(errorTxt,solvant);
    }

    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
