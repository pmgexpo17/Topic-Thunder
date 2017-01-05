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
 * Profile for COL1
 * @author peter
 */
public class ProfileCol1 implements Profile {
   
    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A1" :
                return new String[]{"ROWA","SQUA1","COL1"};
            case "B1" :
                return new String[]{"ROWB","SQUA1","COL1"};
            case "C1" :
                return new String[]{"ROWC","SQUA1","COL1"};
            case "D1" :
                return new String[]{"ROWD","SQUB1","COL1"};
            case "E1" :
                return new String[]{"ROWE","SQUB1","COL1"};
            case "F1" :
                return new String[]{"ROWF","SQUB1","COL1"};
            case "G1" :
                return new String[]{"ROWG","SQUC1","COL1"};
            case "H1" :
                return new String[]{"ROWH","SQUC1","COL1"};
            case "I1" :
                return new String[]{"ROWI","SQUC1","COL1"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A1","B1","C1","D1","E1","F1","G1","H1","I1"};
    }
}
