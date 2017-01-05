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
 * Profile for ROWA
 * @author peter
 */
public class ProfileRowA implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A1" :
                return new String[]{"COL1","SQUA1","ROWA"};
            case "A2" :
                return new String[]{"COL2","SQUA1","ROWA"};
            case "A3" :
                return new String[]{"COL3","SQUA1","ROWA"};
            case "A4" :
                return new String[]{"COL4","SQUA2","ROWA"};
            case "A5" :
                return new String[]{"COL5","SQUA2","ROWA"};
            case "A6" :
                return new String[]{"COL6","SQUA2","ROWA"};
            case "A7" :
                return new String[]{"COL7","SQUA3","ROWA"};
            case "A8" :
                return new String[]{"COL8","SQUA3","ROWA"};
            case "A9" :
                return new String[]{"COL9","SQUA3","ROWA"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A1","A2","A3","A4","A5","A6","A7","A8","A9"};
    }
}
