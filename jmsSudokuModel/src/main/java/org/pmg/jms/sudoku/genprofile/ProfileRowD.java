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
public class ProfileRowD implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "D1" :
                return new String[]{"COL1","SQUB1","ROWD"};
            case "D2" :
                return new String[]{"COL2","SQUB1","ROWD"};
            case "D3" :
                return new String[]{"COL3","SQUB1","ROWD"};
            case "D4" :
                return new String[]{"COL4","SQUB2","ROWD"};
            case "D5" :
                return new String[]{"COL5","SQUB2","ROWD"};
            case "D6" :
                return new String[]{"COL6","SQUB2","ROWD"};
            case "D7" :
                return new String[]{"COL7","SQUB3","ROWD"};
            case "D8" :
                return new String[]{"COL8","SQUB3","ROWD"};
            case "D9" :
                return new String[]{"COL9","SQUB3","ROWD"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"D1","D2","D3","D4","D5","D6","D7","D8","D9"};
    }
}
