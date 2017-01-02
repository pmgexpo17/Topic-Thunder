/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gensudoku.director;

import java.util.HashSet;

/**
 *
 * @author peter
 */
public class Acknowledgement {

    private final HashSet<String> peersAll = new HashSet<>();
    
    public synchronized boolean allAcknowledged(String peerId, int testCount) {
            
        peersAll.add(peerId);
        if (peersAll.size() == testCount) {
            peersAll.clear(); 
            return true;
        }
        return false;
    }
}
