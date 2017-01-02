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
public interface Reduction {
    
    public void evalExclusivePairs() throws ResolveException;
    public void evalPointingPair(String keyCode, String option) 
                                                        throws ResolveException;
    public void parse(String solvant) throws ResolveException;
    public void reduce(String solvant) throws ResolveException;
    public void reset();
}
