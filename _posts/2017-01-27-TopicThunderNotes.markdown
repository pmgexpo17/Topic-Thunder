---
layout: post
title:  "TopicThunder Synopsis"
date:   2017-01-27 21:13:00 +1000
categories: JMS container
---

TopicThunder is a JMS demo that showcases JMS container benefits for JMS integration

About a year ago, I was doing Sudoku newspaper puzzles every day on my train ride home from work

I made notes about solving patterns for hard puzzles but soon discovered there was no
fun in that drudgery! 

One day had an idea that a JMS Sudoku puzzle topic would create a resolving chain reaction

Each topic member unit (row,column,square) receives a solution, resolves and publishes ... repeat ... chain reaction!

---
---
<br/> 

In developing my solving engine, as expected for hard puzzles, the solution cycle would abruptly stop

My strategy is : build resolver with medium level solving capacity and finish by trial and error

The resolver applies basic elimination rules including : hidden singles, naked pairs, hidden pairs and pointing pairs

---
---
<br/> 
The Sudoku resolver brain is a state machine (SM), and like a brain has 2 parts!

The right side : Resolvar, left side : Respondar

Resolver legend :
+ resolveUnitA1, sudoku-client package, game moderator X 1
+ resolveUnitB1, sudoku-model package, sudoku resolver X 27
+ resolveUnitB2, sudoku-model package, stall monitor X 1

{% highlight java %}

public class ResolveUnitB1 implements Resolvar {
    ...
}

public class ResponseUnitB1 extends AbstractLifeCycle implements Respondar {
    ...
}
{% endhighlight %}

Resolvar SM stall detection behaviour : 

+ each resolveUnitB1 has a resolve-cache
    + sodoku-model has 27 resolve units, 1 for each row, column and square
+ for each resolveUnitB1, if a reduction is found, resolve-cache contains the solution
    + the solution is posted only to related unit peers AND to itself
	    + the resolve-cache is not cleared
	+ when the self-posted solution is received then 
	    + resolve-cache is cleared
	    + resolveUnitB2 is signalled to test stall condition

Resolvar SM stall detection method :

+ for resolveUnitB2 in resolve state, if stall-test action is received then
    + for all resolveUnitB1, test stall-condition
        + stall condition : resolve-cache is empty
    + if combined AND stall-condition = TRUE then
        + bounce stall-retest action off resolveUnitA1 AND retest stall-condition
        + if combined AND stall-condition = TRUE then
            + post signal to resolveUnitA1 for trial state transition
        

Resolvar SM trial and error method :

+ resolveUnitB2 receives start trial signal from resolveUnitA1
    + for all resolveUnitB1, trialGovernor evals the best trial candidate
    + for all resolveUnitB1, store a unit snapshot
    + resolveUnitA1 stores a solve-cache snapshot
    + resolveUnitB2 posts the trial-option, selected by the trialGovernor

+ keep resolving UNTIL

+ the resolveUnitA1 or any resolveUnitB1 find an error then
    + all resolveUnits rollback their cache
	    + if trialGovernor options are exhausted then rollback another iteration
    + resolveUnitB2 posts the trial-option, selected by the trialGovernor

OR
+ if resolveUnitB2 detects resolver stall then
    + resolveUnitB2 posts the trial-option, selected by the trialGovernor

+ repeat until solved or game timeout happens
   
---
---
<br/> 

Topic Thunder is an JMS integration app which means 2 or more ClientPeers are coupled together

Therefore, state machine lifecycle and behaviour depends on integration

With Java generics, create objects that are installable just like a real device

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

Guice is essential for achieving a generic SM pattern\\
The Sudoku model has 1 aux-director (stall-monitor) and 27 game-directors, 1 for each row, column and square

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
{% endhighlight %}

The heart of game-director is ResolveUnitB1 and ResponseUnitB1

The B1 suffix serves to indicate integration component scope

Partner package sudoku-client has similar components with A1 suffix

Topic Thunder Director inventory is : 1 X A1 director, 27 X B1 director, 1 X B2 director

Therefore, Sudoku resolution depends on A1, B1 and B2 behaviour and lifecycle integration

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
