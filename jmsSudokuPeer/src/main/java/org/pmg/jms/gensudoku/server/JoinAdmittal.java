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
package org.pmg.jms.gensudoku.server;

import java.util.HashMap;
import javax.jms.JMSException;
import org.pmg.jms.genclient.Routable;
import org.pmg.jms.genhandler.JmsAjaxBean;
import org.pmg.jms.genhandler.XStreamHandler;

/**
 *
 * @author peter
 */
public class JoinAdmittal {

    private final XStreamHandler xmlHandler = new XStreamHandler();
    private Integer startCount;
    public int joinStatus = 1;
    public String errText = "";
    public String clientId = "";
        
    public JoinAdmittal(Routable delegate) throws JMSException {

        joinStatus = getJoinStatus(delegate);
        switch (joinStatus) {
            case 2 :
                errText = "Error : startMap is null";
                break;
            case 3 :
                errText = "Error : startMap count is less than allowed mininum, 17";
                break;
            default :
                break;
        }
    }
        
    public boolean startMapIsValid(Routable delegate) {
            
        if (joinStatus == 1)
            return true;            
        return false;
    }

        
    private int getJoinStatus(Routable delegate) throws JMSException {

        clientId = delegate.getString("clientId");
        if (delegate.getObject("startMap") == null)
            return 2;
        HashMap<String,String> startMap = 
                                    (HashMap) delegate.getObject("startMap");
        startCount = Integer.valueOf(startMap.get("count"));
        if (startCount == null || startCount == 0)
            return 2;
        if (startCount < 17)
            return 3;
        return 1;
    }

    public String getXmlErrorText() {
            
        JmsAjaxBean ajaxBean = new JmsAjaxBean();
        ajaxBean.setFrom(clientId);
        ajaxBean.setAction("error2");
        HashMap<String,String> errorMsg = new HashMap<>();
        errorMsg.put("errText", errText);
        ajaxBean.setMessage(errorMsg);
        return xmlHandler.getXmlText(ajaxBean,true);
    }
}
