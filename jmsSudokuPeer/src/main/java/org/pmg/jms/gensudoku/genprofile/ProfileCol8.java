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
 * Profile for COL8
 * @author peter
 */
public class ProfileCol8 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A8" :
                return new String[]{"ROWA","SQUA3","COL8"};
            case "B8" :
                return new String[]{"ROWB","SQUA3","COL8"};
            case "C8" :
                return new String[]{"ROWC","SQUA3","COL8"};
            case "D8" :
                return new String[]{"ROWD","SQUB3","COL8"};
            case "E8" :
                return new String[]{"ROWE","SQUB3","COL8"};
            case "F8" :
                return new String[]{"ROWF","SQUB3","COL8"};
            case "G8" :
                return new String[]{"ROWG","SQUC3","COL8"};
            case "H8" :
                return new String[]{"ROWH","SQUC3","COL8"};
            case "I8" :
                return new String[]{"ROWI","SQUC3","COL8"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A8","B8","C8","D8","E8","F8","G8","H8","I8"};
    }
}
