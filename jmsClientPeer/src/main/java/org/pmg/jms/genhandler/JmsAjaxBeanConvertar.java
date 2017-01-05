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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * XStreamHandler converter component for XML to JmsAjaxBean conversion
 * XStream is an XML parser and java bean converter
 * @author Peter A McGill
 */
public class JmsAjaxBeanConvertar implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        return JmsAjaxBean.class.isAssignableFrom(clazz);
    }

        @Override
        public void marshal(Object value, HierarchicalStreamWriter writer, 
                                                MarshallingContext context) {

            JmsAjaxBean bean = (JmsAjaxBean) value;
            writer.addAttribute("from",bean.getFrom());
            writer.addAttribute("action",bean.getAction());
            writer.addAttribute("method",bean.getMethod());
            writer.addAttribute("destName",bean.getDestName());
            writer.addAttribute("state",bean.getState());
            AbstractMap map = (AbstractMap) bean.getMessage();
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                writer.startNode(entry.getKey().toString());
                Object val = entry.getValue();
                if ( null != val ) {
                    writer.setValue(val.toString());
                }
                writer.endNode();
            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, 
                                                UnmarshallingContext context) {

            JmsAjaxBean bean = new JmsAjaxBean();            
            String key = null;
            String value = null;

            value = reader.getAttribute("from");
            bean.setFrom(value);

            value = reader.getAttribute("action");
            bean.setAction(value);

            value = reader.getAttribute("method");
            bean.setMethod(value);

            value = reader.getAttribute("destName");
            bean.setDestName(value);

            value = reader.getAttribute("state");
            bean.setState(value);
            
            HashMap<String, String> map = new HashMap<>();
            while(reader.hasMoreChildren()) {
                reader.moveDown();
                key = reader.getNodeName(); // nodeName aka element's name
                value = reader.getValue();
                map.put(key, value);
                reader.moveUp();
            }
            bean.setMessage(map);
            return bean;
        }    
}
