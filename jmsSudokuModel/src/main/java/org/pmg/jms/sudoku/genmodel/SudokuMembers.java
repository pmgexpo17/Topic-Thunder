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
package org.pmg.jms.sudoku.genmodel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Convenience class to wrap the sudoku member list and iterator
 * @author peter
 */
public class SudokuMembers {

    private String[] getUnitList() {

        return new String[]        
    {"ROWA","ROWB","ROWC","ROWD","ROWE","ROWF","ROWG","ROWH","ROWI",
     "COL1","COL2","COL3","COL4","COL5","COL6","COL7","COL8","COL9",
       "SQUA1","SQUA2","SQUA3","SQUB1","SQUB2","SQUB3","SQUC1","SQUC2","SQUC3"};
    }
    
    public Iterator<String> getIterator() {

        // get a randomized unit list
        return new HashSet<>(Arrays.asList( getUnitList() )).iterator();
    }
}
