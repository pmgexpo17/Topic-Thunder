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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A lifeCycle implementation for a beans collection.
 * Sourced from https://github.com/eclipse/jetty.project 
 */

public class ContainerLifeCycle extends AbstractLifeCycle implements 
                                            Container, Destroyable, Dumpable {

    private final List<Bean> _beans = new CopyOnWriteArrayList<>();
    private final List<Container.Listener> _listeners = 
                                                new CopyOnWriteArrayList<>();
    private boolean _doStarted = false;

    /**
     * Starts the managed lifecycle beans in the order they were added.
     */
    @Override
    protected void doStart() throws Exception {
        
        // indicate that we are started, so that addBean will start other beans 
        // added.
        _doStarted = true;

        // start our managed and auto beans
        for (Bean b : _beans) {
            if (b._bean instanceof LifeCycle) {
                LifeCycle l = (LifeCycle) b._bean;
                switch(b._managed) {
                    case MANAGED:
                        if (!l.isRunning())
                            start(l);
                        break;
                    case AUTO:
                        if (l.isRunning())
                            unmanage(b);
                        else {
                            manage(b);
                            start(l);
                        }
                        break;
                }
            }
        }
        super.doStart();
    }

    /**
     * Starts the given lifecycle.
     *
     * @param l
     * @throws Exception
     */
    protected void start(LifeCycle l) throws Exception {
        
        l.start();
    }
    
    /**
     * Stops the given lifecycle.
     *
     * @param l
     * @throws Exception
     */
    protected void stop(LifeCycle l) throws Exception {
        
        l.stop();
    }

    /**
     * Stops the managed lifecycle beans in the reverse order they were added.
     */
    @Override
    protected void doStop() throws Exception {
        
        _doStarted = false;
        super.doStop();
        List<Bean> reverse = new ArrayList<>(_beans);
        Collections.reverse(reverse);
        for (Bean b : reverse) {
            if (b._managed==Managed.MANAGED && b._bean instanceof LifeCycle) {
                LifeCycle l = (LifeCycle)b._bean;
                if (l.isRunning())
                    stop(l);
            }
        }
    }

    /**
     * Destroys the managed Destroyable beans in the reverse order they were 
     * added.
     */
    @Override
    public void destroy() {
        
        List<Bean> reverse = new ArrayList<>(_beans);
        Collections.reverse(reverse);
        for (Bean b : reverse) {
            if (b._bean instanceof Destroyable && 
                    (b._managed==Managed.MANAGED || b._managed==Managed.POJO)) {
                Destroyable d = (Destroyable)b._bean;
                d.destroy();
            }
        }
        _beans.clear();
    }


    /**
     * @param bean the bean to test
     * @return whether this aggregate contains the bean
     */
    public boolean contains(Object bean) {
        
        for(Bean b : _beans)
            if (b._bean == bean)
                return true;
        return false;
    }

    /**
     * @param bean the bean to test
     * @return whether this aggregate contains and manages the bean
     */
    public boolean isManaged(Object bean) {
        
        for(Bean b : _beans)
            if (b._bean == bean)
                return b.isManaged();
        return false;
    }

    /**
     * Adds the given bean, detecting whether to manage it or not.
     * If the bean is a {@link LifeCycle}, then it will be managed if it is not
     * already started and not managed if it is already started.
     * The {@link #addBean(Object, boolean)}
     * method should be used if this is not correct, or the 
     * {@link #manage(Object)} and {@link #unmanage(Object)}
     * methods may be used after an add to change the status.
     *
     * @param o the bean object to add
     * @return true if the bean was added, false if it was already present
     */
    @Override
    public boolean addBean(Object o) {
        
        if (o instanceof LifeCycle) {
            LifeCycle l = (LifeCycle)o;
            return addBean(o,l.isRunning()?Managed.UNMANAGED:Managed.AUTO);
        }
        return addBean(o,Managed.POJO);
    }

    /**
     * Adds the given bean, explicitly managing it or not.
     *
     * @param o       The bean object to add
     * @param managed whether to managed the lifecycle of the bean
     * @return true if the bean was added, false if it was already present
     */
    public boolean addBean(Object o, boolean managed) {
        
        if (o instanceof LifeCycle)
            return addBean(o,managed?Managed.MANAGED:Managed.UNMANAGED);
        return addBean(o,managed?Managed.POJO:Managed.UNMANAGED);
    }

    public boolean addBean(Object o, Managed managed) {
        
        if (contains(o))
            return false;

        Bean new_bean = new Bean(o);

        // if the bean is a Listener
        if (o instanceof Container.Listener)
            addEventListener((Container.Listener)o);

        // Add the bean
        _beans.add(new_bean);

        // Tell existing listeners about the new bean
        for (Container.Listener l:_listeners)
            l.beanAdded(this,o);

        try {
            switch (managed) {
                case UNMANAGED:
                    unmanage(new_bean);
                    break;

                case MANAGED:
                    manage(new_bean);

                    if (isStarting() && _doStarted) {
                        LifeCycle l = (LifeCycle)o;
                        if (!l.isRunning())
                            start(l);
                    }
                    break;

                case AUTO:
                    if (o instanceof LifeCycle) {
                        LifeCycle l = (LifeCycle)o;
                        if (isStarting()) {
                            if (l.isRunning())
                                unmanage(new_bean);
                            else if (_doStarted) {
                                manage(new_bean);
                                start(l);
                            }
                            else
                                new_bean._managed=Managed.AUTO;      
                        }
                        else if (isStarted())
                            unmanage(new_bean);
                        else
                            new_bean._managed=Managed.AUTO;
                    }
                    else
                        new_bean._managed=Managed.POJO;
                    break;

                case POJO:
                    new_bean._managed=Managed.POJO;
            }
        }
        catch (RuntimeException | Error e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (LOG.isDebugEnabled())
            LOG.debug("{} added {}",this,new_bean);

        return true;
    }

    
    /* ------------------------------------------------------------ */
    /** Add a managed lifecycle.
     * <p>This is a conveniance method that uses addBean(lifecycle,true)
     * and then ensures that the added bean is started iff this container
     * is running.  Exception from nested calls to start are caught and 
     * wrapped as RuntimeExceptions
     * @param lifecycle
     */
    public void addManaged(LifeCycle lifecycle) {
        
        addBean(lifecycle,true);
        try {
            if (isRunning() && !lifecycle.isRunning())
                start(lifecycle);
        }
        catch (RuntimeException | Error e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEventListener(Container.Listener listener) {
        
        if (_listeners.contains(listener))
            return;
        
        _listeners.add(listener);

        // tell it about existing beans
        for (Bean b:_beans) {
            listener.beanAdded(this,b._bean);

            // handle inheritance
            if (listener instanceof InheritedListener && b.isManaged() && 
                                                b._bean instanceof Container) {
                if (b._bean instanceof ContainerLifeCycle)
                     ((ContainerLifeCycle)b._bean).addBean(listener, false);
                 else
                     ((Container)b._bean).addBean(listener);
            }
        }
    }

    /**
     * Manages a bean already contained by this aggregate, so that it is 
     * started/stopped/destroyed with this aggregate.
     *
     * @param bean The bean to manage (must already have been added).
     */
    public void manage(Object bean) {
        
        for (Bean b : _beans) {
            if (b._bean == bean) {
                manage(b);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown bean " + bean);
    }

    private void manage(Bean bean) {
        
        if (bean._managed!=Managed.MANAGED) {
            bean._managed=Managed.MANAGED;

            if (bean._bean instanceof Container) {
                for (Container.Listener l:_listeners) {
                    if (l instanceof InheritedListener) {
                        if (bean._bean instanceof ContainerLifeCycle)
                            ((ContainerLifeCycle)bean._bean).addBean(l,false);
                        else
                            ((Container)bean._bean).addBean(l);
                    }
                }
            }

            if (bean._bean instanceof AbstractLifeCycle)
               ((AbstractLifeCycle)bean._bean).setStopTimeout(getStopTimeout());
        }
    }

    /**
     * Unmanages a bean already contained by this aggregate, so that it is not 
     * started/stopped/destroyed with this aggregate.
     *
     * @param bean The bean to unmanage (must already have been added).
     */
    public void unmanage(Object bean) {
        
        for (Bean b : _beans) {
            if (b._bean == bean) {
                unmanage(b);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown bean " + bean);
    }

    private void unmanage(Bean bean) {
        
        if (bean._managed!=Managed.UNMANAGED) {
            if (bean._managed==Managed.MANAGED && 
                                            bean._bean instanceof Container) {
                for (Container.Listener l:_listeners) {
                    if (l instanceof InheritedListener)
                        ((Container)bean._bean).removeBean(l);
                }
            }
            bean._managed=Managed.UNMANAGED;
        }
    }

    @Override
    public Collection<Object> getBeans() {
        
        return getBeans(Object.class);
    }

    public void setBeans(Collection<Object> beans) {
        
        for (Object bean : beans)
            addBean(bean);
    }

    @Override
    public <T> Collection<T> getBeans(Class<T> clazz) {
        
        ArrayList<T> beans = new ArrayList<>();
        for (Bean b : _beans) {
            
            if (clazz.isInstance(b._bean))
                beans.add(clazz.cast(b._bean));
        }
        return beans;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        
        for (Bean b : _beans) {
            if (clazz.isInstance(b._bean))
                return clazz.cast(b._bean);
        }
        return null;
    }

    /**
     * Removes all bean
     */
    public void removeBeans() {
        
        ArrayList<Bean> beans= new ArrayList<>(_beans);
        for (Bean b : beans)
            remove(b);
    }

    private Bean getBean(Object o) {
        
        for (Bean b : _beans) {
            if (b._bean == o)
                return b;
        }
        return null;
    }

    @Override
    public boolean removeBean(Object o) {
        
        Bean b=getBean(o);
        return b!=null && remove(b);
    }

    private boolean remove(Bean bean) {
        
        if (_beans.remove(bean)) {
            boolean wasManaged = bean.isManaged();
            
            unmanage(bean);

            for (Container.Listener l:_listeners)
                l.beanRemoved(this,bean._bean);

            if (bean._bean instanceof Container.Listener)
                removeEventListener((Container.Listener)bean._bean);

            // stop managed beans
            if (wasManaged && bean._bean instanceof LifeCycle) {
                try {
                    stop((LifeCycle)bean._bean);
                }
                catch(RuntimeException | Error e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void removeEventListener(Container.Listener listener) {
        
        if (_listeners.remove(listener)) {
            
            // remove existing beans
            for (Bean b:_beans) {
                listener.beanRemoved(this,b._bean);

                if (listener instanceof InheritedListener && 
                                b.isManaged() && b._bean instanceof Container)
                    ((Container)b._bean).removeBean(listener);
            }
        }
    }

    @Override
    public void setStopTimeout(long stopTimeout) {
        
        super.setStopTimeout(stopTimeout);
        for (Bean bean : _beans) {
            
            if (bean.isManaged() && bean._bean instanceof AbstractLifeCycle)
                ((AbstractLifeCycle)bean._bean).setStopTimeout(stopTimeout);
        }
    }

    /**
     * Dumps to {@link System#err}.
     * @see #dump()
     */
    public void dumpStdErr() {
        
        try {
            
            dump(System.err, "");
        }
        catch (IOException e) {
            LOG.warn("[dumpStdErr]",e);
        }
    }

    @Override
    public String dump() {
        
        return dump(this);
    }

    public static String dump(Dumpable dumpable) {
        
        StringBuilder b = new StringBuilder();
        try {
            dumpable.dump(b, "");
        }
        catch (IOException e) {
            LOG.warn("[dump]",e);
        }
        return b.toString();
    }

    public void dump(Appendable out) throws IOException {
        
        dump(out, "");
    }

    protected void dumpThis(Appendable out) throws IOException {
        
        out.append(String.valueOf(this)).append(" - ").append(getState()).
                                                                append("\n");
    }

    public static void dumpObject(Appendable out, Object o) throws IOException {
        
        try {
            if (o instanceof LifeCycle)
                out.append(String.valueOf(o)).append(" - ").
                append((AbstractLifeCycle.getState((LifeCycle)o))).append("\n");
            else
                out.append(String.valueOf(o)).append("\n");
        }
        catch (Throwable th) {
            out.append(" => ").append(th.toString()).append('\n');
        }
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        
        dumpBeans(out,indent);
    }

    protected void dumpBeans(Appendable out, String indent, 
                            Collection<?>... collections) throws IOException {
        dumpThis(out);
        int size = _beans.size();
        for (Collection<?> c : collections)
            size += c.size();
        if (size == 0)
            return;
        int i = 0;
        for (Bean b : _beans) {
            i++;

            switch(b._managed) {
                case POJO:
                    out.append(indent).append(" +- ");
                    if (b._bean instanceof Dumpable)
                        ((Dumpable)b._bean).dump(out, indent + (i == size ? "    " : " |  "));
                    else
                        dumpObject(out, b._bean);
                    break;

                case MANAGED:
                    out.append(indent).append(" += ");
                    if (b._bean instanceof Dumpable)
                        ((Dumpable)b._bean).dump(out, indent + (i == size ? "    " : " |  "));
                    else
                        dumpObject(out, b._bean);
                    break;

                case UNMANAGED:
                    out.append(indent).append(" +~ ");
                    dumpObject(out, b._bean);
                    break;

                case AUTO:
                    out.append(indent).append(" +? ");
                    if (b._bean instanceof Dumpable)
                        ((Dumpable)b._bean).dump(out, indent + (i == size ? "    " : " |  "));
                    else
                        dumpObject(out, b._bean);
                    break;

            }
        }

        if (i<size)
            out.append(indent).append(" |\n");

        for (Collection<?> c : collections) {
            for (Object o : c) {
                i++;
                out.append(indent).append(" +> ");

                if (o instanceof Dumpable)
                    ((Dumpable)o).dump(out, indent + (i == size ? "    " : " |  "));
                else
                    dumpObject(out, o);
            }
        }
    }

    public static void dump(Appendable out, String indent, 
                            Collection<?>... collections) throws IOException {
        if (collections.length == 0)
            return;
        int size = 0;
        for (Collection<?> c : collections)
            size += c.size();
        if (size == 0)
            return;

        int i = 0;
        for (Collection<?> c : collections) {
            for (Object o : c) {
                i++;
                out.append(indent).append(" +- ");

                if (o instanceof Dumpable)
                    ((Dumpable)o).dump(out, indent + (i == size ? "    " : " |  "));
                else
                    dumpObject(out, o);
            }
        }
    }


    enum Managed { POJO, MANAGED, UNMANAGED, AUTO };

    private static class Bean {
        
        private final Object _bean;
        private volatile Managed _managed = Managed.POJO;

        private Bean(Object b) {
            _bean = b;
        }

        public boolean isManaged() {
            return _managed==Managed.MANAGED;
        }

        @Override
        public String toString() {
            return String.format("{%s,%s}", _bean, _managed);
        }
    }

    public void updateBean(Object oldBean, final Object newBean) {
        
        if (newBean!=oldBean) {
            if (oldBean!=null)
                removeBean(oldBean);
            if (newBean!=null)
                addBean(newBean);
        }
    }

    public void updateBeans(Object[] oldBeans, final Object[] newBeans) {
        // remove oldChildren not in newChildren
        if (oldBeans!=null) {
            loop: for (Object o:oldBeans) {
                if (newBeans!=null) {
                    for (Object n:newBeans)
                        if (o==n)
                            continue loop;
                }
                removeBean(o);
            }
        }

        // add new beans not in old
        if (newBeans!=null) {
            loop: for (Object n:newBeans) {
                if (oldBeans!=null) {
                    for (Object o:oldBeans)
                        if (o==n)
                            continue loop;
                }
                addBean(n);
            }
        }
    }
}
