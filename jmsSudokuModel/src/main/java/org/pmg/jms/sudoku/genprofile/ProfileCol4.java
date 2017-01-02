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
 *
 * @author peter
 */
public class ProfileCol4 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A4" :
                return new String[]{"ROWA","SQUA2","COL4"};
            case "B4" :
                return new String[]{"ROWB","SQUA2","COL4"};
            case "C4" :
                return new String[]{"ROWC","SQUA2","COL4"};
            case "D4" :
                return new String[]{"ROWD","SQUB2","COL4"};
            case "E4" :
                return new String[]{"ROWE","SQUB2","COL4"};
            case "F4" :
                return new String[]{"ROWF","SQUB2","COL4"};
            case "G4" :
                return new String[]{"ROWG","SQUC2","COL4"};
            case "H4" :
                return new String[]{"ROWH","SQUC2","COL4"};
            case "I4" :
                return new String[]{"ROWI","SQUC2","COL4"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A4","B4","C4","D4","E4","F4","G4","H4","I4"};
    }
}
