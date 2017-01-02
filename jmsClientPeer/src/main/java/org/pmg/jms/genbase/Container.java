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
package org.pmg.jms.genbase;

import java.util.Collection;

/**
 * A container of java beans for lifecycle management
 * Sourced from https://github.com/eclipse/jetty.project 
 */
public interface Container
{
    /* ------------------------------------------------------------ */
    /**
     * Add a bean.  If the bean is-a {@link Listener}, then also do an implicit {@link #addEventListener(Listener)}.
     * @param o the bean object to add
     * @return true if the bean was added, false if it was already present
     */
    public boolean addBean(Object o);

    /**
     * @return the list of beans known to this aggregate
     * @see #getBean(Class)
     */
    public Collection<Object> getBeans();

    /**
     * @param clazz the class of the beans
     * @return the list of beans of the given class (or subclass)
     * @see #getBeans()
     */
    public <T> Collection<T> getBeans(Class<T> clazz);

    /**
     * @param clazz the class of the bean
     * @return the first bean of a specific class (or subclass), or null if no such bean exist
     */
    public <T> T getBean(Class<T> clazz);

    /**
     * Removes the given bean.
     * If the bean is-a {@link Listener}, then also do an implicit {@link #removeEventListener(Listener)}.
     * @return whether the bean was removed
     */
    public boolean removeBean(Object o);
    
    /**
     * Add an event listener. 
     * @see Container#addBean(Object)
     * @param listener
     */
    public void addEventListener(Listener listener);
    
    /**
     * Remove an event listener. 
     * @see Container#removeBean(Object)
     * @param listener
     */
    public void removeEventListener(Listener listener);

    /**
     * A listener for Container events.
     * If an added bean implements this interface it will receive the events
     * for this container.
     */
    public interface Listener
    {
        void beanAdded(Container parent,Object child);
        void beanRemoved(Container parent,Object child);
    }
    
    /**
     * Inherited Listener.
     * If an added bean implements this interface, then it will 
     * be added to all contained beans that are themselves Containers
     */
    public interface InheritedListener extends Listener
    {
    }
}
