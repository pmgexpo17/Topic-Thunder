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
public class ProfileSquC3 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "G7" :
                return new String[]{"ROWG","COL7","SQUC3"};
            case "G8" :
                return new String[]{"ROWG","COL8","SQUC3"};
            case "G9" :
                return new String[]{"ROWG","COL9","SQUC3"};
            case "H7" :
                return new String[]{"ROWH","COL7","SQUC3"};
            case "H8" :
                return new String[]{"ROWH","COL8","SQUC3"};
            case "H9" :
                return new String[]{"ROWH","COL9","SQUC3"};
            case "I7" :
                return new String[]{"ROWI","COL7","SQUC3"};
            case "I8" :
                return new String[]{"ROWI","COL8","SQUC3"};
            case "I9" :
                return new String[]{"ROWI","COL9","SQUC3"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"G7","G8","G9","H7","H8","H9","I7","I8","I9"};
    }
}
