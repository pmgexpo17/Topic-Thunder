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
package org.pmg.jms.gensudoku.genprofile;

/**
 *
 * @author peter
 */
public class ProfileSquA1 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A1" :
                return new String[]{"ROWA","COL1","SQUA1"};
            case "A2" :
                return new String[]{"ROWA","COL2","SQUA1"};
            case "A3" :
                return new String[]{"ROWA","COL3","SQUA1"};
            case "B1" :
                return new String[]{"ROWB","COL1","SQUA1"};
            case "B2" :
                return new String[]{"ROWB","COL2","SQUA1"};
            case "B3" :
                return new String[]{"ROWB","COL3","SQUA1"};
            case "C1" :
                return new String[]{"ROWC","COL1","SQUA1"};
            case "C2" :
                return new String[]{"ROWC","COL2","SQUA1"};
            case "C3" :
                return new String[]{"ROWC","COL3","SQUA1"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A1","A2","A3","B1","B2","B3","C1","C2","C3"};
    }
}
