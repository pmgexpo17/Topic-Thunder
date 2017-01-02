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
package org.pmg.jms.genhandler;

import com.thoughtworks.xstream.XStream;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream is an XML parser and java bean converter
 * Here we decorate XStream to deliver a JmsAjaxBean message protocol for input  
 * and output transactions. JmsAjaxBeanConvertar makes this achievable 
 * @author Peter A McGill
 */
public class XStreamHandler {
    private static final Logger LOG = LoggerFactory.getLogger(XStreamHandler.class);
    protected XStream xstream = getXstreamObject();

    private static XStream getXstreamObject() {

        XStream _xstream = new XStream();
        _xstream.registerConverter(new JmsAjaxBeanConvertar());
        _xstream.alias("message", JmsAjaxBean.class);
        return _xstream;
    }

    public JmsAjaxBean getAjaxBean(String ajaxXml) {

        if (ajaxXml == null || ajaxXml.isEmpty())
            return null;
        return (JmsAjaxBean) xstream.fromXML(ajaxXml);
    }

    public JmsAjaxBean getAjaxBean(TextMessage txtMessage) {

        try {
            if (txtMessage.getText().isEmpty())
                return null;
            String ajaxXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + txtMessage.getText();
            return (JmsAjaxBean) xstream.fromXML(ajaxXml);
        } catch (JMSException ex) {
            LOG.error("[XStreamHandler] Error converting XML to JmsAjaxBean",ex);
        }
        return null;        
    }
    
    public String getXmlText(JmsAjaxBean bean, boolean removeHeader) {

        String xmlText = xstream.toXML(bean);
        if (!removeHeader)
            return xmlText;
        if (xmlText .contains("<?xml"))
            return xmlText.substring(xmlText.indexOf("?>")+2);
        return xmlText;
    }    
    
}
