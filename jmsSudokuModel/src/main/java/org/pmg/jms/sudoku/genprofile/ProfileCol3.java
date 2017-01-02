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
public class ProfileCol3 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A3" :
                return new String[]{"ROWA","SQUA1","COL3"};
            case "B3" :
                return new String[]{"ROWB","SQUA1","COL3"};
            case "C3" :
                return new String[]{"ROWC","SQUA1","COL3"};
            case "D3" :
                return new String[]{"ROWD","SQUB1","COL3"};
            case "E3" :
                return new String[]{"ROWE","SQUB1","COL3"};
            case "F3" :
                return new String[]{"ROWF","SQUB1","COL3"};
            case "G3" :
                return new String[]{"ROWG","SQUC1","COL3"};
            case "H3" :
                return new String[]{"ROWH","SQUC1","COL3"};
            case "I3" :
                return new String[]{"ROWI","SQUC1","COL3"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A3","B3","C3","D3","E3","F3","G3","H3","I3"};
    }
}
