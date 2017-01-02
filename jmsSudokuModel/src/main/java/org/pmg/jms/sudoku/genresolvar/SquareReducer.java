/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

import com.google.inject.Inject;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class SquareReducer extends AbstractReductor {

    private static final Logger LOG = LoggerFactory.getLogger(SquareReducer.class);

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
