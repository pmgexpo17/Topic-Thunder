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
 * Profile for ROWH
 * @author peter
 */
public class ProfileRowH implements Profile {

    @Override
    public String[] getAllPeers(String key) {
        
        switch (key) {
            case "H1" :
                return new String[]{"COL1","SQUC1","ROWH"};
            case "H2" :
                return new String[]{"COL2","SQUC1","ROWH"};
            case "H3" :
                return new String[]{"COL3","SQUC1","ROWH"};
            case "H4" :
                return new String[]{"COL4","SQUC2","ROWH"};
            case "H5" :
                return new String[]{"COL5","SQUC2","ROWH"};
            case "H6" :
                return new String[]{"COL6","SQUC2","ROWH"};
            case "H7" :
                return new String[]{"COL7","SQUC3","ROWH"};
            case "H8" :
                return new String[]{"COL8","SQUC3","ROWH"};
            case "H9" :
                return new String[]{"COL9","SQUC3","ROWH"};
        }
        return new String[]{};
    }

    @Override
    public String[] getUnitKeys() {
        
        return new String[]{"H1","H2","H3","H4","H5","H6","H7","H8","H9"};
    }
}
