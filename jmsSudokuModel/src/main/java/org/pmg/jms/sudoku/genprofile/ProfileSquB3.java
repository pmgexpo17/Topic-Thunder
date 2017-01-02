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
public class ProfileSquB3 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "D7" :
                return new String[]{"ROWD","COL7","SQUB3"};
            case "D8" :
                return new String[]{"ROWD","COL8","SQUB3"};
            case "D9" :
                return new String[]{"ROWD","COL9","SQUB3"};
            case "E7" :
                return new String[]{"ROWE","COL7","SQUB3"};
            case "E8" :
                return new String[]{"ROWE","COL8","SQUB3"};
            case "E9" :
                return new String[]{"ROWE","COL9","SQUB3"};
            case "F7" :
                return new String[]{"ROWF","COL7","SQUB3"};
            case "F8" :
                return new String[]{"ROWF","COL8","SQUB3"};
            case "F9" :
                return new String[]{"ROWF","COL9","SQUB3"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"D7","D8","D9","E7","E8","E9","F7","F8","F9"};
    }
}
