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

import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sudoku reductor engine
 * @author peter
 */
public abstract class AbstractReductor implements Reduction {

    protected static final Logger LOG = 
                                LoggerFactory.getLogger(AbstractReductor.class);
    protected final String className = getClass().getSimpleName();
    protected final HashMap<String,String> exclusivePairs = new HashMap<>();
    protected final HashMap<String,String> hiddenPairs = new HashMap<>();
    protected final HashSet<String> spontaneous = new HashSet<>();    
    protected final ReductorBean bean;
    
    public AbstractReductor(ReductorBean bean) {

        this.bean = bean;
    }

    @Override
    public abstract void evalExclusivePairs() throws ResolveException;
    @Override
    public abstract void evalPointingPair(String keyCode, String option) 
                                                        throws ResolveException; 

    /*
     - interface methods
    */
    
    @Override
    public void parse(String solvant) throws ResolveException {

        String key, options;
        StringTokenizer tokenz = new StringTokenizer(solvant, ",");
        while( tokenz.hasMoreTokens() ) {
            key = tokenz.nextToken();
            if (!tokenz.hasMoreTokens())
                throw new ResolveException(bean.peerId,solvant);
            options = tokenz.nextToken();
            try {
                Integer.parseInt(options);
            } catch(NumberFormatException e){
                throw new ResolveException(bean.peerId,key,options);
            }                        
            if (key.contains(":")) {
                evalPointingPair(key,options);
                continue;
            }
            if (!bean.remnantKeys.contains(key))
                continue;
            // merge the options that potentially reduced
            mergeResolved(key,options);
        }
    }

    @Override
    public synchronized void reset() {

        exclusivePairs.clear();
    }

    @Override
    public void reduce(String solvent) throws ResolveException {
        
        bean.resolved.clear();
        parse(solvent);
        // if parsing has no reducing effect then no need to resolve
        if (bean.resolved.isEmpty())
            return;
        reduce();
    }

    /*
     - End interface methods
    */
    
    /*
     - Parse methods
    */
    protected void applyExclusivePair(String keyz, String option) throws ResolveException {
        
        if (option == null)
            option = exclusivePairs.get(keyz);
        for( String key1: bean.remnantKeys) {
            if (!keyz.contains(key1))
                removeMultiOption(key1,option);
        }
    }

    private void applyResolved(String key, String option) throws ResolveException {

        if (key.contains(":")) {
            applyExclusivePair(key,option);
            return;
        }
        bean.resolved.put(key,option);
        bean.remnantKeys.remove(key);
        bean.unit.put(key,option);

        for(String key1: bean.remnantKeys)
            removeCellOption(key1,option);
    }

    protected void applySolved(String key, String option) throws ResolveException {

        if (!bean.remnantKeys.contains(key)) {
            LOG.debug("[{}[{}] {} duplicate discarded : {}",className,bean.peerId,
                                                            key,option);
            return;
        }
        if (!bean.unit.get(key).contains(option))
            throw new ResolveException(bean.peerId,key,option,bean.unit.toString());

        // we don't want to propogate solvent cells
        // if this solvent key is not first in the solveMap list then
        // the previous merge has put it in resolved, so remove it
        bean.resolved.remove(key);
        bean.remnantKeys.remove(key);
        bean.unit.put(key,option);

        for(String key1: bean.remnantKeys)
           removeCellOption(key1,option);
    }

    protected void mergeResolved(String key, String new_options) throws ResolveException {

        // here we have received a new solution from the topic
        // we expect new_options to possibly have a reduced option list
        // so check if each option of curr_options exists in new_options
        // if not then reduce curr_options
        if (new_options.length() == 1) {
            applySolved(key,new_options);
            return;
        }
        String curr_options = bean.unit.get(key);
        if (curr_options.equals(new_options))
            return;
        String mod_options = bean.unit.get(key);        
        for( String option: curr_options.split("") ) {
            if (!new_options.contains(option))
                mod_options = mod_options.replace(option,"");
        }
        if (mod_options.equals(curr_options))
            return;
        bean.unit.put(key,mod_options);
        bean.resolved.put(key,mod_options);
    }
    /*
     - End parse methods
    */
    
    /*
     - Resolve methods 
    */
    private void parseNakedPair(String key1, String options) {

        if (spontaneous.toString().contains(key1))
            return;
        // if naked pair is already registered then return
        if (exclusivePairs.containsValue(options))
            return;
        // test for naked pairs that may have spontaneously appeared
        for(String key2: bean.remnantKeys)
            if (!key2.equals(key1)) {
                String keyz = key1 + ":" + key2;
                if ( bean.unit.get(key2).equals(options) ) {
                    spontaneous.add(keyz);
                    exclusivePairs.put(keyz,options);
                }
            }       
    }

    private void parseSpontaneous() {
       
        String options;
        for(String key: bean.remnantKeys) {
            options = bean.unit.get(key);
            if (options.length() == 1) // spontaneous solution
                spontaneous.add(key);
            else if (options.length() == 2)
                parseNakedPair(key,options);
        }
    }            

    private void putHiddenPair(String key, String option) {
            
        if (hiddenPairs.containsKey(key))
            hiddenPairs.put(key,hiddenPairs.get(key) + option);
        else 
            hiddenPairs.put(key,option);
    }

    public void removeCellOption(String key, String _option) throws ResolveException {

        String curr_options = bean.unit.get(key);
        if (curr_options.contains(_option)) {
            curr_options = curr_options.replace(_option,"");
            if (curr_options.isEmpty())
                throw new ResolveException(bean.peerId,key,_option,bean.unit.toString());
            bean.unit.put(key,curr_options);
            bean.resolved.put(key,curr_options);
        }
    }    
    
    public void removeMultiOption(String key, String value) throws ResolveException {

        for( String _option: value.split(""))
            removeCellOption(key,_option);
    }
    
    // resolve section
    protected void autoResolve() throws ResolveException {

        spontaneous.clear();
        int limit = bean.remnantKeys.size();
        while (limit > 0) {
            parseSpontaneous();
            if (spontaneous.isEmpty())
                break;
            for(String key: spontaneous)
                applyResolved(key,bean.unit.get(key));
            limit--;
        }
    }

    protected void evalHiddenSolvent() throws ResolveException {
        // evaluates the frequency of each option in the unsolved remnant
        // if freq = 1 then a solution exists in that cell
        // if freq = 2 AND the option count = 2 then only those options can 
        // exist in those cells. the hidden pair is then converted to a naked pair
        String[] options = {"1","2","3","4","5","6","7","8","9"};
        String keyz;
        for( String option : options ) {
            int freq = 0;
            keyz = "";
            for(String key: bean.remnantKeys) {
                //if (remnant.get(key).contains(option)) {
                if (bean.unit.get(key).contains(option)) {
                    keyz = (freq == 0) ? key : keyz + ":" + key;
                    freq++;
                }
            }
            if (freq == 1) // hidden single
                applyResolved(keyz,option);
            else if ((bean.remnantKeys.size() > 2) && (freq == 2) && 
                                            (!exclusivePairs.containsKey(keyz)))
                putHiddenPair(keyz,option);
        }
    }

    // main sub-method
    protected void reduce() throws ResolveException {

        hiddenPairs.clear();
        evalHiddenSolvent();
        // test hidden pairs, if found convert to a naked pair
        evalExclusivePairs();
        autoResolve();
    }    

}
