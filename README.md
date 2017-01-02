# Topic-Thunder
A Sudoku solver JMS integration application that applies a JMS topic to propogate solutions by chain reaction. 
The main design features are :
1. ActiveMQ JMS message broker
  a. hosts a Jetty web server where TopicThunder webapp is hosted
  b. JmsWebClientUtil is a servlet package for webapp client JMS integration 
2. JMS container - manages JMS lifecycle of added components such as start and stop
  a. Lifecycle, Container and Handler model is forked from a Jetty web server package.
  b. see org.pmg.jms.genclient.ClientPeer
3. Guice dependency injection and scoping creates sharable container beans
4. Handles JMS transport components generically (i don't mean aka Java)
  a. Guice injects the specific transport version by annotation
  b. OpenWire transport is the standard in this package
5. Employs Java generics to simplify the design scope
  a. see org.pmg.jms.genclient.Messenger / MultiMessenger
    i. param1 example : ClientMember - JMS consumer and MessageAvailableListener
      - see org.pmg.jms.genclient.ClientMember
    ii param2 example : GamePropogate - message router invoking controller
      - see org.pmg.jms.gensudoku.GamePropogate
      - see org.pmg.jms.gendirector.AppController
  b. see org.pmg.jms.gendirector.AppDirector
    i. param1 example : see org.pmg.jms.gendirectorResolveUnitA1
    ii. param2 example : see org.pmg.jms.gendirectorResponseUnitA1
6. JmsSudokuPeer has 2 ClientPeer (container) components
  a. A sudoku game server that handles web client sudoku solver requests
    i. see org.pmg.jms.gensudoku.server.GameServer
  b. A game controller that manages game resolution by a state machine
    i. Java 8 lambda expressions enable code as data SM application
    ii. see org.pmg.jms.gensudoku.GameModule for Guice DI bindings
7. JmsSudokuModel has 1 ClientPeer component
    
