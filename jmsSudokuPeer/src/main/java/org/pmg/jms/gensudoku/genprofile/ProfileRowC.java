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
 * Profile for ROWC
 * @author peter
 */
public class ProfileRowC implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "C1" :
                return new String[]{"COL1","SQUA1","ROWC"};
            case "C2" :
                return new String[]{"COL2","SQUA1","ROWC"};
            case "C3" :
                return new String[]{"COL3","SQUA1","ROWC"};
            case "C4" :
                return new String[]{"COL4","SQUA2","ROWC"};
            case "C5" :
                return new String[]{"COL5","SQUA2","ROWC"};
            case "C6" :
                return new String[]{"COL6","SQUA2","ROWC"};
            case "C7" :
                return new String[]{"COL7","SQUA3","ROWC"};
            case "C8" :
                return new String[]{"COL8","SQUA3","ROWC"};
            case "C9" :
                return new String[]{"COL9","SQUA3","ROWC"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"C1","C2","C3","C4","C5","C6","C7","C8","C9"};
    }
}
