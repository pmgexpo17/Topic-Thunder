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
 *
 * @author peter
 */
public class SquareReducer extends AbstractReductor {

    @Inject
    public SquareReducer(ReductorBean bean) {
        
        super(bean);
        LOG.debug("[{}[{}] reductor bean : {}",className, bean.peerId,
                                                            bean.toString());
    }
    
    @Override
    public void evalPointingPair(String keyCode, String option) {} 
    
    @Override
    public void evalExclusivePairs() throws ResolveException {
            
        String options;
        for( String keyz: hiddenPairs.keySet() ) {
            options = hiddenPairs.get(keyz);
            applyPointingPair(keyz,options);
            applyNakedPair(keyz,options);
        }
    }
        
    private void applyNakedPair(String keyz, String options) 
                                                      throws ResolveException {

        if (options.length() == 2) {
            exclusivePairs.put(keyz,options);
            applyExclusivePair(keyz,options);                
            for( String key: keyz.split(":") ) {
                bean.unit.put(key, options);
                bean.resolved.put(key, options);
            }
        }
    }

    private void applyPointingPair(String keyz, String options) {

        if (options.length() != 1)
            return;
        String _peerId;
        String[] parts = keyz.split("");
        // parts format : RowId1|ColId1|:|RowId2|ColId2
        if (parts[0].equals(parts[3])) {
            // peer is a row
            _peerId = "ROW" + parts[0];
        }
        else if (parts[1].equals(parts[4]))
            _peerId = "COL" + parts[1];
        else
            return;
        LOG.debug("[{}[{}] pointing pair found {},{},{}",className,bean.peerId,
                                                          _peerId,keyz,options);
        exclusivePairs.put(keyz,options);            
        bean.resolved.put(_peerId + "," + keyz, options);
    }

}
