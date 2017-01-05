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
 * Profile for COL6
 * @author peter
 */
public class ProfileCol6 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A6" :
                return new String[]{"ROWA","SQUA2","COL6"};
            case "B6" :
                return new String[]{"ROWB","SQUA2","COL6"};
            case "C6" :
                return new String[]{"ROWC","SQUA2","COL6"};
            case "D6" :
                return new String[]{"ROWD","SQUB2","COL6"};
            case "E6" :
                return new String[]{"ROWE","SQUB2","COL6"};
            case "F6" :
                return new String[]{"ROWF","SQUB2","COL6"};
            case "G6" :
                return new String[]{"ROWG","SQUC2","COL6"};
            case "H6" :
                return new String[]{"ROWH","SQUC2","COL6"};
            case "I6" :
                return new String[]{"ROWI","SQUC2","COL6"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A6","B6","C6","D6","E6","F6","G6","H6","I6"};
    }
}
