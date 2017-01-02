/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.sudoku.genresolvar;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author peter
 */
public class RectangleReducer extends AbstractReductor {
    
    private static final Logger LOG = 
                                LoggerFactory.getLogger(RectangleReducer.class);

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
