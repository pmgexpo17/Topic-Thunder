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
 * Profile for SQUB2
 * @author peter
 */
public class ProfileSquB2 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "D4" :
                return new String[]{"ROWD","COL4","SQUB2"};
            case "D5" :
                return new String[]{"ROWD","COL5","SQUB2"};
            case "D6" :
                return new String[]{"ROWD","COL6","SQUB2"};
            case "E4" :
                return new String[]{"ROWE","COL4","SQUB2"};
            case "E5" :
                return new String[]{"ROWE","COL5","SQUB2"};
            case "E6" :
                return new String[]{"ROWE","COL6","SQUB2"};
            case "F4" :
                return new String[]{"ROWF","COL4","SQUB2"};
            case "F5" :
                return new String[]{"ROWF","COL5","SQUB2"};
            case "F6" :
                return new String[]{"ROWF","COL6","SQUB2"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"D4","D5","D6","E4","E5","E6","F4","F5","F6"};
    }
}
