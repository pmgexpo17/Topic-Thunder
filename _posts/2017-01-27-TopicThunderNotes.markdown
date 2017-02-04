---
layout: post
title:  "TopicThunder Synopsis"
date:   2017-01-27 21:13:00 +1000
categories: JMS container
---

TopicThunder is a JMS demo that showcases JMS container benefits for JMS integration

About a year ago, I was doing Sudoku newspaper puzzles every day on my train ride home from work

I made notes about solving patterns for hard puzzles but that quickly became too much bother! 

One day had an idea that a JMS Sudoku puzzle topic would create a resolving chain reaction

Each topic member (row,column,square) receives a solution, resolves and publishes ... repeat ... chain reaction!
<br/>

---
---
<br/> 
In developing the resolver engine, as expected for hard puzzles, the solution cycle would abruptly stop

For proving JMS integration, the resolver design has medium level solving capacity and finishes by trial and error

The resolver applies basic elimination rules including : hidden singles, naked pairs, hidden pairs and pointing pairs

---
---
<br/> 
The Sudoku resolver brain is a state machine (SM), and like a brain has 2 parts!

The right side : Resolvar, left side : Respondar

Resolver legend :  

* resolveUnitA1, sudoku-client package, game moderator X 1
* resolveUnitB1, sudoku-model package, sudoku resolver X 27
* resolveUnitB2, sudoku-model package, trial-error monitor X 1

{% highlight java %}

public class ResolveUnitB1 implements Resolvar {
    ...
}

public class ResponseUnitB1 extends AbstractLifeCycle implements Respondar {
    ...
}
{% endhighlight %}

Diagram_1 shows :  

* Resolvar SM stall detection behaviour
* Resolvar SM trial and error behaviour

![stall detection]({{ site.baseurl }}/assets/stallTest01.png)
![trial and error]({{ site.baseurl }}/assets/trialError01.png)
![diagram_1]({{ site.baseurl }}/assets/diaLabel01.png)

Trial-error monitor, resolveUnitB2 depends on TrialGovernor methods  
Diagram_2 shows TrialGovernor method expansion  

* gameIsStalled
* hasTrialOption

![trialGovernor methods]({{ site.baseurl }}/assets/trialGovernor01.png)

---
---
<br/> 
Topic Thunder is an JMS integration app which means 2 or more ClientPeers are coupled together

Therefore, state machine lifecycle and behaviour depends on integration

With Java generics, create container installed objects intuitively like a real device

Let's review some installable state machine parts in the client-model package

Generic class Director iterates the SM lifecycle

{% highlight java %}

    @Override
    public synchronized void handle(Routable delegate) throws JMSException {

        try {
            state.setDelegate(delegate);        
            if (resolver.apply().hasNext())
                responder.apply().respond();
            state.iterate();
        } catch(Exception ex) {
            LOG.error("[Director] resolve exception",ex);
            throw new JMSException(ex.getMessage());
        }
    }    
{% endhighlight %}

Guice has a factory mechanism for building generic objects by declaring TypeLiteral bindings

{% highlight java %}

public interface PeerFactory {

    ClientPeerB1 getClientPeer();
    Messenger<MessageProvider,GamePropogate> getMessenger();
    MultiMessenger<MessageProvider,GamePropogate> getMultiMessenger();
    @Named("game") Director<ResolveUnitB1,ResponseUnitB1> getGameDirector();
    @Named("monitor") Director<ResolveUnitB2,ResponseUnitB2> 
                                            getAuxDirector(String sessionId);
    TrialAgent getTrialAgent();
}

public class SudokuModule extends AbstractModule {

  @Override
  protected void configure() {

...

    install(new FactoryModuleBuilder().
    implement(ServicePeer.class, ClientPeerB1.class).
    implement(MessageRouter.class,
        new TypeLiteral<Messenger<MessageProvider,GamePropogate>>() {}).
    implement(ClientDirector.class,Names.named("monitor"),
        new TypeLiteral<Director<ResolveUnitB2,ResponseUnitB2>>() {}).
    implement(ClientDirector.class,Names.named("game"),
        new TypeLiteral<Director<ResolveUnitB1,ResponseUnitB1>>() {}).
    build(PeerFactory.class));

    ...
}

public class SudokuModel extends AbstractLifeCycle {

    ...

    public void configure(String sessionId) throws JMSException {

        ...
        Director<ResolveUnitB2,ResponseUnitB2> director = 
                                        peerFactory.getAuxDirector(sessionId);
        director.init();
        String route = "/director/trial/monitor";        
        clientPeer.addHandler(route,director);        
        clientPeer.addBean(director);
        ...
    }
    
    private void addComponent(String peerId) throws JMSException {

        ...
        // ResponseUnitB1 producer internally configured
        Director<ResolveUnitB1,ResponseUnitB1> director = 
                                                peerFactory.getGameDirector();
        director.init();
        clientPeer.addBean(director);

        String route = "/director/resolve/reduce/" + peerId;
        clientPeer.addHandler(route,director);
        ...
    }
    
    ...
}

{% endhighlight %}

Topic Thunder director inventory is : 1 X director[A1], 27 X director[B1], 1 X director[B2]

Sudoku puzzle resolution depends on A1, B1 and B2 SM and lifecyle integration

Resolvar SM behavior is stored in a 2D map of Java 8 lambda code samples

Resolve is a functional interface, declaring 1 boolean method : hasNext

Resolvar combines state.current and delegate.action to apply Resolve behaviour

Respondar SM behavior is stored in a 1D map of Java 8 lambda code samples

Quicken is a functional interface, declaring 1 method : respond

Respondar depends on state.transtion to apply Quicken response behaviour

{% highlight java %}

public class ResolveUnitB1 implements Resolvar {

    ...
    
    @Override
    public Resolve apply() throws JMSException {
        
        state.action = state.delegate.getString("action");
        state.delegate.setStatus(true);
        return get();
    }

    @Override
    public Resolve get() {

        if (rcache.get(state.current).containsKey(state.action))
            return rcache.get(state.current).get(state.action);
        return unknown_action;
    }

    ...

    /*
     - state = trial-error
     */
    Resolve trial_retrial = () -> {
     
        LOG.debug("[{}[{}] got retrial notice",className,bean.peerId);
        state.next = "trial";
        state.transition = "trial_retrial";
        return true;
    };

	...
        
    private HashMap<String,HashMap<String,Resolve>> loadCache() {

        // state = ready
        HashMap<String,HashMap<String,Resolve>> _rcache = new HashMap<>(4,1);
        HashMap<String,Resolve> rmap = new HashMap<>(1,1);
        rmap.put("preset",ready_preset);
        _rcache.put("ready",rmap);
        // state = resolve
        rmap = new HashMap<>(6,1);
        rmap.put("monitor",default_monitor);
        rmap.put("query",default_query);
        rmap.put("reset",default_reset);
        rmap.put("resolve",resolve_resolve);
        rmap.put("start",resolve_start);
        rmap.put("trial",resolve_trial);
        _rcache.put("resolve",rmap);
        // state = trial
        rmap = new HashMap<>(6,1);
        rmap.put("error",trial_error);
        rmap.put("monitor",default_monitor);
        rmap.put("query",default_query);
        rmap.put("reset",default_reset);
        rmap.put("start",trial_start);
        rmap.put("resolve",trial_resolve);
        _rcache.put("trial",rmap);
        // state = trial-error
        rmap = new HashMap<>(5,1);
        rmap.put("query",default_query);
        rmap.put("reset",default_reset);
        rmap.put("retrial",trial_retrial);
        _rcache.put("trial-error",rmap);
        return _rcache;
    }
}

public class ResponseUnitB1 extends AbstractLifeCycle implements Respondar {

	...

    @Override
    public Quicken apply() throws JMSException {

        // state-transition is either self descriptive or it describes
        // the action to do in the current state
        LOG.debug("[{}] state transition : {}",className,state.transition);
        return get();
    }

    @Override
    public Quicken get() {
        
        if (rcache.containsKey(state.transition))
            return rcache.get(state.transition);
        return unknown_action;
    }

	...
	
    Quicken trial_retrial = () -> {
        
        responder.putControlAction("retrial");
    };

	...
	
    private HashMap<String,Quicken> loadCache() {

        // state = ready
        HashMap<String,Quicken> rmap = new HashMap<>(6,1);
        rmap.put("default_error",default_error);
        rmap.put("default_ready",default_ready);
        rmap.put("default_resolve",default_resolve);
        rmap.put("ready_resolve",ready_resolve);
        rmap.put("stall_test",stall_test);
        rmap.put("trial_error",trial_error);
        rmap.put("trial_retrial",trial_retrial);
        return rmap;
    }
}
{% endhighlight %}
