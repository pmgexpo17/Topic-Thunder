---
layout: post
title:  "JmsClientPeer Base Package code review and touchups"
date:   2017-01-22 21:13:00 +1000
categories: JMS container
---

* Added addConnector method to ServicePeer interface to support different transport options

{% highlight java %}
public void addConnector(Connector connector);
{% endhighlight %}

* Added addConnector method to ClientPeer
* Removed ClientPeer constructor @Inject annotation as descendent class does the injection

{% highlight java %}
protected final Map<String,Connector> connectors = new HashMap<>();    
    
public ClientPeer(Connector connector, Controller controller) {

    addConnector(connector);
    this.controller = controller;
    addBean(controller);        
}

@Override
public final void addConnector(Connector connector) {
        
    String transportName = connector.getTransportName().toUpperCase();
    connectors.put(transportName,connector);                
}
{% endhighlight %}

* Removed Connection parameter from ConnectionProvider since it is implied by extending CheckedProvider\<Connection\>

{% highlight java %}

public interface ConnectionProvider extends CheckedProvider<Connection> {

@Override
public Connection get() throws JMSException;
    
}
{% endhighlight %}

 * Updated ConnectionProvider dependent classes :

	* OpenWireConnectPrvdr
	* OpenWireConnector
 
