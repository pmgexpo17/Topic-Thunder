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
 * Profile for ROWG
 * @author peter
 */
public class ProfileRowG implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "G1" :
                return new String[]{"COL1","SQUC1","ROWG"};
            case "G2" :
                return new String[]{"COL2","SQUC1","ROWG"};
            case "G3" :
                return new String[]{"COL3","SQUC1","ROWG"};
            case "G4" :
                return new String[]{"COL4","SQUC2","ROWG"};
            case "G5" :
                return new String[]{"COL5","SQUC2","ROWG"};
            case "G6" :
                return new String[]{"COL6","SQUC2","ROWG"};
            case "G7" :
                return new String[]{"COL7","SQUC3","ROWG"};
            case "G8" :
                return new String[]{"COL8","SQUC3","ROWG"};
            case "G9" :
                return new String[]{"COL9","SQUC3","ROWG"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"G1","G2","G3","G4","G5","G6","G7","G8","G9"};
    }
}
