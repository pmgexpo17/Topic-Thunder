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
package org.pmg.jms.sudoku.genresolvar;

/**
 * Reductor bean interface
 * @author peter
 */
public interface Reductive {
    
    public void init(String peerId, String sessionId);
    public void clear();
    public boolean isIdle();
    public void reset();
    public boolean gameIsComplete();
    public boolean hasReduction();
    public Snapshot getSnapshot();
    public void rollback(Snapshot snapshot);
    public String getSolvent(String peerId);
    public void fillSolveMap();
    public void putRemnantLog();
    public void putResolveLog();
    public void putStartLog();
}
