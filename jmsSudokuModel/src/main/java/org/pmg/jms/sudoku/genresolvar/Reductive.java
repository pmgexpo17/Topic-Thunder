/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

/**
 *
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
