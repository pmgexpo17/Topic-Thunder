/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pmg.jms.gensudoku.director;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;

/**
 *
 * @author peter
 */
public class StartMapBean {

    private HashMap<String,String> startMap;
    public String gameId = "";

    public void configure(Routable delegate) throws JMSException {
        
        startMap = (HashMap) delegate.getObject("startMap");
        gameId = delegate.getString("gameId");
    }

    private String[] getUnitList() {

        return new String[]        
    {"ROWA","ROWB","ROWC","ROWD","ROWE","ROWF","ROWG","ROWH","ROWI",
     "COL1","COL2","COL3","COL4","COL5","COL6","COL7","COL8","COL9",
       "SQUA1","SQUA2","SQUA3","SQUB1","SQUB2","SQUB3","SQUC1","SQUC2","SQUC3"};
    }
    
    public String getSolvent(String unitId) {
        
        return startMap.get(unitId);
    }

    public Iterator<String> getIterator() {

        // get a randomized unit list
        return new HashSet<>(Arrays.asList( getUnitList() )).iterator();
    }

}
