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
public class ProfileCol5 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A5" :
                return new String[]{"ROWA","SQUA2","COL5"};
            case "B5" :
                return new String[]{"ROWB","SQUA2","COL5"};
            case "C5" :
                return new String[]{"ROWC","SQUA2","COL5"};
            case "D5" :
                return new String[]{"ROWD","SQUB2","COL5"};
            case "E5" :
                return new String[]{"ROWE","SQUB2","COL5"};
            case "F5" :
                return new String[]{"ROWF","SQUB2","COL5"};
            case "G5" :
                return new String[]{"ROWG","SQUC2","COL5"};
            case "H5" :
                return new String[]{"ROWH","SQUC2","COL5"};
            case "I5" :
                return new String[]{"ROWI","SQUC2","COL5"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A5","B5","C5","D5","E5","F5","G5","H5","I5"};
    }
}
