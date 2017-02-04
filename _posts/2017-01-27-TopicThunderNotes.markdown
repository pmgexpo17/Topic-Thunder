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

With Java generics, create container installed objects just like a real device

Let's review some installable state machine parts in the client-model package

Generic class Director iterates the SM lifecycle

