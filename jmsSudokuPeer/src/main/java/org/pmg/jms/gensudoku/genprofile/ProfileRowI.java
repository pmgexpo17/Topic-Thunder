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
 *
 * @author peter
 */
public class ProfileRowI implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "I1" :
                return new String[]{"COL1","SQUC1","ROWI"};
            case "I2" :
                return new String[]{"COL2","SQUC1","ROWI"};
            case "I3" :
                return new String[]{"COL3","SQUC1","ROWI"};
            case "I4" :
                return new String[]{"COL4","SQUC2","ROWI"};
            case "I5" :
                return new String[]{"COL5","SQUC2","ROWI"};
            case "I6" :
                return new String[]{"COL6","SQUC2","ROWI"};
            case "I7" :
                return new String[]{"COL7","SQUC3","ROWI"};
            case "I8" :
                return new String[]{"COL8","SQUC3","ROWI"};
            case "I9" :
                return new String[]{"COL9","SQUC3","ROWI"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"I1","I2","I3","I4","I5","I6","I7","I8","I9"};
    }
}
