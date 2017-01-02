/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 *
 * @author peter
 */
public class ReductorPrvdr implements Provider<Reduction> {
 
    private final Provider<RectangleReducer> rReducerPrvder;
    private final Provider<SquareReducer> sReducerPrvder;
    private String peerId;
    
    @Inject
    public ReductorPrvdr(Provider<RectangleReducer> rReducerPrvder,
                                Provider<SquareReducer> sReducerPrvder) {
        
        this.rReducerPrvder = rReducerPrvder;
        this.sReducerPrvder = sReducerPrvder;
    }
    
    public void configure(String peerId) {

        this.peerId = peerId;
    }
        
    @Override
    public Reduction get() {
        
        switch (peerId) {
            case "COL1" : return rReducerPrvder.get();
            case "COL2" : return rReducerPrvder.get();
            case "COL3" : return rReducerPrvder.get();
            case "COL4" : return rReducerPrvder.get();
            case "COL5" : return rReducerPrvder.get();
            case "COL6" : return rReducerPrvder.get();
            case "COL7" : return rReducerPrvder.get();
            case "COL8" : return rReducerPrvder.get();
            case "COL9" : return rReducerPrvder.get();
            case "ROWA" : return rReducerPrvder.get();
            case "ROWB" : return rReducerPrvder.get();
            case "ROWC" : return rReducerPrvder.get();
            case "ROWD" : return rReducerPrvder.get();
            case "ROWE" : return rReducerPrvder.get();
            case "ROWF" : return rReducerPrvder.get();
            case "ROWG" : return rReducerPrvder.get();
            case "ROWH" : return rReducerPrvder.get();
            case "ROWI" : return rReducerPrvder.get();
            case "SQUA1" : return sReducerPrvder.get();
            case "SQUA2" : return sReducerPrvder.get();
            case "SQUA3" : return sReducerPrvder.get();
            case "SQUB1" : return sReducerPrvder.get();
            case "SQUB2" : return sReducerPrvder.get();
            case "SQUB3" : return sReducerPrvder.get();
            case "SQUC1" : return sReducerPrvder.get();
            case "SQUC2" : return sReducerPrvder.get();
            case "SQUC3" : return sReducerPrvder.get();
            default : return null;
        }
    }
}
