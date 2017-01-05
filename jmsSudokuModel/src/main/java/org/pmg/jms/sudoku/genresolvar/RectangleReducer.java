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

import com.google.inject.Inject;

/**
 * Sudoku resolver for rectangular units
 * @author peter
 */
public class RectangleReducer extends AbstractReductor {
    
    @Inject
    public RectangleReducer(ReductorBean bean) {
        
        super(bean);
        LOG.debug("[{}[{}] reductor bean : {}",className,bean.peerId,
                                                            bean.toString());
    }
        
    @Override
    public void evalPointingPair(String keyz, String option) 
                                                      throws ResolveException {

        LOG.debug("[{}[{}] eval pointing pair {},{}",className,bean.peerId,
                                                                   keyz,option);
        for( String key: bean.remnantKeys) {
            if (!keyz.contains(key))
                removeCellOption(key,option);
        }
    }
    
    @Override
    public void evalExclusivePairs() throws ResolveException {
            
        for( String keyz: hiddenPairs.keySet() )
            applyNakedPair(keyz);
    }
        
    private void applyNakedPair(String keyz) throws ResolveException {

        String options = hiddenPairs.get(keyz);
        if (options.length() != 2)
            return;
        LOG.debug("[{}[{}] naked pair found {}:{}",className,bean.peerId,
                                                                  keyz,options);
        exclusivePairs.put(keyz,options);
        applyExclusivePair(keyz,options);                
        for( String key: keyz.split(":") ) {
            bean.unit.put(key, options);
            bean.resolved.put(key, options);
        }
    }

}
