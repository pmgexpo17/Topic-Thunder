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
 * Profile for SQUA2
 * @author peter
 */
public class ProfileSquA2 implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "A4" :
                return new String[]{"ROWA","COL4","SQUA2"};
            case "A5" :
                return new String[]{"ROWA","COL5","SQUA2"};
            case "A6" :
                return new String[]{"ROWA","COL6","SQUA2"};
            case "B4" :
                return new String[]{"ROWB","COL4","SQUA2"};
            case "B5" :
                return new String[]{"ROWB","COL5","SQUA2"};
            case "B6" :
                return new String[]{"ROWB","COL6","SQUA2"};
            case "C4" :
                return new String[]{"ROWC","COL4","SQUA2"};
            case "C5" :
                return new String[]{"ROWC","COL5","SQUA2"};
            case "C6" :
                return new String[]{"ROWC","COL6","SQUA2"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"A4","A5","A6","B4","B5","B6","C4","C5","C6"};
    }
}
