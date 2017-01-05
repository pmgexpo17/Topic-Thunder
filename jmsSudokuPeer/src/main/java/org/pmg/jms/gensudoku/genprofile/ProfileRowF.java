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
 * Profile for ROWF
 * @author peter
 */
public class ProfileRowF implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "F1" :
                return new String[]{"COL1","SQUB1","ROWF"};
            case "F2" :
                return new String[]{"COL2","SQUB1","ROWF"};
            case "F3" :
                return new String[]{"COL3","SQUB1","ROWF"};
            case "F4" :
                return new String[]{"COL4","SQUB2","ROWF"};
            case "F5" :
                return new String[]{"COL5","SQUB2","ROWF"};
            case "F6" :
                return new String[]{"COL6","SQUB2","ROWF"};
            case "F7" :
                return new String[]{"COL7","SQUB3","ROWF"};
            case "F8" :
                return new String[]{"COL8","SQUB3","ROWF"};
            case "F9" :
                return new String[]{"COL9","SQUB3","ROWF"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"F1","F2","F3","F4","F5","F6","F7","F8","F9"};
    }
}
