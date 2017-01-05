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
 * Profile for COL7
 * @author peter
 */
public class ProfileCol7 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A7" :
                return new String[]{"ROWA","SQUA3","COL7"};
            case "B7" :
                return new String[]{"ROWB","SQUA3","COL7"};
            case "C7" :
                return new String[]{"ROWC","SQUA3","COL7"};
            case "D7" :
                return new String[]{"ROWD","SQUB3","COL7"};
            case "E7" :
                return new String[]{"ROWE","SQUB3","COL7"};
            case "F7" :
                return new String[]{"ROWF","SQUB3","COL7"};
            case "G7" :
                return new String[]{"ROWG","SQUC3","COL7"};
            case "H7" :
                return new String[]{"ROWH","SQUC3","COL7"};
            case "I7" :
                return new String[]{"ROWI","SQUC3","COL7"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A7","B7","C7","D7","E7","F7","G7","H7","I7"};
    }
}
