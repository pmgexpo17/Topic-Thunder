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
 * Profile for ROWE
 * @author peter
 */
public class ProfileRowE implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "E1" :
                return new String[]{"COL1","SQUB1","ROWE"};
            case "E2" :
                return new String[]{"COL2","SQUB1","ROWE"};
            case "E3" :
                return new String[]{"COL3","SQUB1","ROWE"};
            case "E4" :
                return new String[]{"COL4","SQUB2","ROWE"};
            case "E5" :
                return new String[]{"COL5","SQUB2","ROWE"};
            case "E6" :
                return new String[]{"COL6","SQUB2","ROWE"};
            case "E7" :
                return new String[]{"COL7","SQUB3","ROWE"};
            case "E8" :
                return new String[]{"COL8","SQUB3","ROWE"};
            case "E9" :
                return new String[]{"COL9","SQUB3","ROWE"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"E1","E2","E3","E4","E5","E6","E7","E8","E9"};
    }
}
