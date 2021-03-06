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
package org.pmg.jms.sudoku.genprofile;

/**
 * Profile for COL2
 * @author peter
 */
public class ProfileCol2 implements Profile {
    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A2" :
                return new String[]{"ROWA","SQUA1","COL2"};
            case "B2" :
                return new String[]{"ROWB","SQUA1","COL2"};
            case "C2" :
                return new String[]{"ROWC","SQUA1","COL2"};
            case "D2" :
                return new String[]{"ROWD","SQUB1","COL2"};
            case "E2" :
                return new String[]{"ROWE","SQUB1","COL2"};
            case "F2" :
                return new String[]{"ROWF","SQUB1","COL2"};
            case "G2" :
                return new String[]{"ROWG","SQUC1","COL2"};
            case "H2" :
                return new String[]{"ROWH","SQUC1","COL2"};
            case "I2" :
                return new String[]{"ROWI","SQUC1","COL2"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A2","B2","C2","D2","E2","F2","G2","H2","I2"};
    }
}
