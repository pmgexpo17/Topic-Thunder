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
 * Profile for SQUB1
 * @author peter
 */
public class ProfileSquB1 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "D1" :
                return new String[]{"ROWD","COL1","SQUB1"};
            case "D2" :
                return new String[]{"ROWD","COL2","SQUB1"};
            case "D3" :
                return new String[]{"ROWD","COL3","SQUB1"};
            case "E1" :
                return new String[]{"ROWE","COL1","SQUB1"};
            case "E2" :
                return new String[]{"ROWE","COL2","SQUB1"};
            case "E3" :
                return new String[]{"ROWE","COL3","SQUB1"};
            case "F1" :
                return new String[]{"ROWF","COL1","SQUB1"};
            case "F2" :
                return new String[]{"ROWF","COL2","SQUB1"};
            case "F3" :
                return new String[]{"ROWF","COL3","SQUB1"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"D1","D2","D3","E1","E2","E3","F1","F2","F3"};
    }
}
