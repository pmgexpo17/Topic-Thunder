/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gilmarie.web.jmsclient;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A useful base class for any JMS related servlet; there are various ways to
 * map JMS operations to web requests so we put most of the common behaviour in
 * a reusable base class. This servlet can be configured with the following init
 * paramters
 * <dl>
 * <dt>topic</dt>
 * <dd>Set to 'true' if the servle should default to using topics rather than
 * channels</dd>
 * <dt>destination</dt>
 * <dd>The default destination to use if one is not specifiied</dd>
 * <dt></dt>
 * <dd></dd>
 * </dl>
 * 
 * 
 */
public abstract class MessageServletSupport extends HttpServlet {

    private static final transient Logger LOG = LoggerFactory.getLogger(MessageServletSupport.class);
    protected String className;    
    protected long defaultReadTimeout = -1;
    protected long maximumReadTimeout = 10000;
    protected int maximumMessages = 100;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        
        super.init(servletConfig);
        className = getClass().getSimpleName();        

        String name = servletConfig.getInitParameter("defaultReadTimeout");
        if (name != null) {
            defaultReadTimeout = asLong(name);
        }
        name = servletConfig.getInitParameter("maximumReadTimeout");
        if (name != null) {
            maximumReadTimeout = asLong(name);
        }
        name = servletConfig.getInitParameter("maximumMessages");
        if (name != null) {
            maximumMessages = (int)asLong(name);
        }
        
        // lets check to see if there's a connection factory set
        JmsAjaxClient.initConnectionFactory(getServletContext());
    }

    public static boolean asBoolean(String param) {
        return asBoolean(param, false);
    }

    public static boolean asBoolean(String param, boolean defaultValue) {
        if (param == null) {
            return defaultValue;
        } else {
            return param.equalsIgnoreCase("true");
        }
    }

    protected Integer asInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value instanceof String) {
            return Integer.valueOf((String)value);
        }
        if (value instanceof String[]) {
            return Integer.valueOf(((String[])value)[0]);
        }
        return null;
    }

    protected Long asLong(Object value) {
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof String) {
            return Long.valueOf((String)value);
        }
        if (value instanceof String[]) {
            return Long.valueOf(((String[])value)[0]);
        }
        return null;
    }

    protected long asLong(String name) {
        return Long.parseLong(name);
    }

    protected int asInt(String name) {
        return Integer.parseInt(name);
    }

    protected String asString(Object value) {
        if (value instanceof String[]) {
            return ((String[])value)[0];
        }

        if (value != null) {
            return value.toString();
        }

        return null;
    }
}
