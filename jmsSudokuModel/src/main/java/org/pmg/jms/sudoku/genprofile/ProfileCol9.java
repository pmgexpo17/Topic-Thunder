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
 * Profile for COL9
 * @author peter
 */
public class ProfileCol9 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A9" :
                return new String[]{"ROWA","SQUA3","COL9"};
            case "B9" :
                return new String[]{"ROWB","SQUA3","COL9"};
            case "C9" :
                return new String[]{"ROWC","SQUA3","COL9"};
            case "D9" :
                return new String[]{"ROWD","SQUB3","COL9"};
            case "E9" :
                return new String[]{"ROWE","SQUB3","COL9"};
            case "F9" :
                return new String[]{"ROWF","SQUB3","COL9"};
            case "G9" :
                return new String[]{"ROWG","SQUC3","COL9"};
            case "H9" :
                return new String[]{"ROWH","SQUC3","COL9"};
            case "I9" :
                return new String[]{"ROWI","SQUC3","COL9"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A9","B9","C9","D9","E9","F9","G9","H9","I9"};
    }
}
