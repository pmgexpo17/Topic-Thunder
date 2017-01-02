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
public class ProfileSquC2 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "G4" :
                return new String[]{"ROWG","COL4","SQUC2"};
            case "G5" :
                return new String[]{"ROWG","COL5","SQUC2"};
            case "G6" :
                return new String[]{"ROWG","COL6","SQUC2"};
            case "H4" :
                return new String[]{"ROWH","COL4","SQUC2"};
            case "H5" :
                return new String[]{"ROWH","COL5","SQUC2"};
            case "H6" :
                return new String[]{"ROWH","COL6","SQUC2"};
            case "I4" :
                return new String[]{"ROWI","COL4","SQUC2"};
            case "I5" :
                return new String[]{"ROWI","COL5","SQUC2"};
            case "I6" :
                return new String[]{"ROWI","COL6","SQUC2"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"G4","G5","G6","H4","H5","H6","I4","I5","I6"};
    }
}
