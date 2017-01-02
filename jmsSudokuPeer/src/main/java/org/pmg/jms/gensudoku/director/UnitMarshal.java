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
package org.pmg.jms.gensudoku.director;

import org.pmg.jms.gensudoku.genprofile.*;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class UnitMarshal {

    private final Profile profile;
    private final ArrayList<String> iterations = new ArrayList<>();
    protected String resolved = "";
    
    public UnitMarshal(String unitId) {
        
        profile = getUnitProfile(unitId);
    }
    
    public void addSnapshot() {
        
        iterations.add(0,resolved);
    }
    
    public void addSolvent(String solvent) {
        
        resolved += solvent;
    }
    
    public boolean contains(String solvent) {
        
        return resolved.contains(solvent);
    }

    public String[] getAllPeers(String key) {
        
        return profile.getAllPeers(key);
    }
    
    public String getResolved() {
        
        return resolved;
    }
    
    public void reset() {
        
        resolved = "";
        iterations.clear();
    }
    
    public void rollback(int retroIndex) {
        
        while (retroIndex > 0) {
            iterations.remove(0);
            retroIndex--;
        }
        resolved = iterations.get(0);
    }
    
    private static Profile getUnitProfile(String unitId) {
        
        switch (unitId) {
            case "ROWA" : return new ProfileRowA();
            case "ROWB" : return new ProfileRowB();
            case "ROWC" : return new ProfileRowC();
            case "ROWD" : return new ProfileRowD();
            case "ROWE" : return new ProfileRowE();
            case "ROWF" : return new ProfileRowF();
            case "ROWG" : return new ProfileRowG();
            case "ROWH" : return new ProfileRowH();
            case "ROWI" : return new ProfileRowI();
            case "COL1" : return new ProfileCol1();
            case "COL2" : return new ProfileCol2();
            case "COL3" : return new ProfileCol3();
            case "COL4" : return new ProfileCol4();
            case "COL5" : return new ProfileCol5();
            case "COL6" : return new ProfileCol6();
            case "COL7" : return new ProfileCol7();
            case "COL8" : return new ProfileCol8();
            case "COL9" : return new ProfileCol9();
            case "SQUA1" : return new ProfileSquA1();
            case "SQUA2" : return new ProfileSquA2();
            case "SQUA3" : return new ProfileSquA3();
            case "SQUB1" : return new ProfileSquB1();
            case "SQUB2" : return new ProfileSquB2();
            case "SQUB3" : return new ProfileSquB3();
            case "SQUC1" : return new ProfileSquC1();
            case "SQUC2" : return new ProfileSquC2();
            case "SQUC3" : return new ProfileSquC3();
        }
        return null;
    }

}
