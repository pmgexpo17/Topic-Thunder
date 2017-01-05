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
 * Profile for ROWB
 * @author peter
 */
public class ProfileRowB implements Profile {
    
    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "B1" :
                return new String[]{"COL1","SQUA1","ROWB"};
            case "B2" :
                return new String[]{"COL2","SQUA1","ROWB"};
            case "B3" :
                return new String[]{"COL3","SQUA1","ROWB"};
            case "B4" :
                return new String[]{"COL4","SQUA2","ROWB"};
            case "B5" :
                return new String[]{"COL5","SQUA2","ROWB"};
            case "B6" :
                return new String[]{"COL6","SQUA2","ROWB"};
            case "B7" :
                return new String[]{"COL7","SQUA3","ROWB"};
            case "B8" :
                return new String[]{"COL8","SQUA3","ROWB"};
            case "B9" :
                return new String[]{"COL9","SQUA3","ROWB"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"B1","B2","B3","B4","B5","B6","B7","B8","B9"};
    }
}
