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
 * Profile for SQUC1
 * @author peter
 */
public class ProfileSquC1 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "G1" :
                return new String[]{"ROWG","COL1","SQUC1"};
            case "G2" :
                return new String[]{"ROWG","COL2","SQUC1"};
            case "G3" :
                return new String[]{"ROWG","COL3","SQUC1"};
            case "H1" :
                return new String[]{"ROWH","COL1","SQUC1"};
            case "H2" :
                return new String[]{"ROWH","COL2","SQUC1"};
            case "H3" :
                return new String[]{"ROWH","COL3","SQUC1"};
            case "I1" :
                return new String[]{"ROWI","COL1","SQUC1"};
            case "I2" :
                return new String[]{"ROWI","COL2","SQUC1"};
            case "I3" :
                return new String[]{"ROWI","COL3","SQUC1"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"G1","G2","G3","H1","H2","H3","I1","I2","I3"};
    }
}
