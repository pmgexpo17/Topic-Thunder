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
package org.pmg.jms.sudoku.genresolvar;

/**
 *
 * @author peter
 */
public class ResolveException extends Exception {
    
    private final String message;
    
    public ResolveException(String peerId, String key,String option,
                                                            String unitText) {
        
        String errorTxt = 
        "[ResolveException] %s has duplicate option[%s[%s] in unit : %s";
        this.message = String.format(errorTxt,peerId,key,option,unitText);
    }

    public ResolveException(String peerId, String key,String option) {
        
        String errorTxt = 
        "[ResolveException] %s in %s resolvant includes 1 or more non-digits : %s";
        this.message = String.format(errorTxt,key,peerId,option);
    }

    public ResolveException(String peerId, String solvant) {
        
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
