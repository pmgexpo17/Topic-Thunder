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
package org.pmg.jms.sudoku.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.pmg.jms.gensudoku.GameModule;
import org.pmg.jms.gensudoku.SudokuPeer;
import org.pmg.jms.gensudoku.server.GameServer;
import org.pmg.jms.genconfig.JmsClientModule;
import org.pmg.jms.sudoku.genmodel.SudokuModel;
import org.pmg.jms.sudoku.genmodel.SudokuModule;

/**
 * Top level JMS integration with Guice DI making it all possible
 * @author peter
 */
public class GameStation {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        try {
            Injector injector1 = Guice.createInjector(new JmsClientModule(),
                                                        new SudokuModule());
            SudokuModel sudokuModel1 = injector1.getInstance(Key.get(SudokuModel.class));
            sudokuModel1.configure("session1");
            sudokuModel1.showAllRoutes();
            sudokuModel1.start();
/*
            SudokuModel sudokuModel2 = injector1.getInstance(Key.get(SudokuModel.class));
            sudokuModel2.configure("session2");
            sudokuModel2.showAllRoutes();
            sudokuModel2.start();
*/
            Injector injector2 = Guice.createInjector(new JmsClientModule(),
                                                        new GameModule());

            SudokuPeer sudokuPeer1 = injector2.getInstance(Key.get(SudokuPeer.class));
            sudokuPeer1.configure("session1");
            sudokuPeer1.start();
            sudokuPeer1.showAllRoutes();
/*
            SudokuPeer sudokuPeer2 = injector2.getInstance(Key.get(SudokuPeer.class));
            sudokuPeer2.configure("session2");
            sudokuPeer2.start();
            sudokuPeer2.showAllRoutes();
*/
            GameServer gameServer = injector2.getInstance(Key.get(GameServer.class));
            gameServer.configure();
            gameServer.addCapacity("session1");
  //          gameServer.addCapacity("session2");
            gameServer.start();  
            gameServer.showAllRoutes();
        } catch(Exception ex) {
            System.out.println(
                String.format("[Gamitron] build exception : %s",
                                                            ex.getMessage()));
        }
    }
}
