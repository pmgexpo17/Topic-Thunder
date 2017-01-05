# Topic-Thunder
A JMS integration demo application that implements a Sudoku puzzle solving webapp service

System Requirements :

1. JDK 1.8
  a. required lambda expresssions were introduced in Java 8
  
2. ActiveMQ 
  a. minimum version 5.10.0
  
3. Java project IDE with Maven module extension
  b. recommended : Netbeans 8.0.2 or 8.1

Setup Instructions :

1. Install ActiveMQ 
  a. for linux refer to http://servicebus.blogspot.com.au/2011/02/installing-apache-active-mq-on-ubuntu.html

2. Install Netbeans IDE
  a. refer to https://netbeans.org/community/releases/80/install.html

3. Topic Thunder webapp installation
  a. make sure activemq is not running, run : sudo service activemq status
    i. to stop run : sudo service activemq stop
  b. copy Jms-WebClient-Model-V1.jar to /opt/activemq/lib
  b. in /opt/activemq/webapps create a new folder called TopicThunder
  c. copy the TopicThunder webapp package content into this folder
    i. apply the same owner and group as the activemq root folder
  d. update the jetty conf file : /opt/activemq/conf/jetty.xml, see Appendix A
  e. start activemq : sudo service activemq start
  f. at this point, make sure Topic Thunder Game Station is running
    i. see Netbeans project setup instructions
  g. point your web browser to : http://localhost:8161/TopicThunder/
    i. the home page provides sudoku puzzle samples which have a predefined format
    ii. paste your start map into the text box and click 'Load Start Pattern'
    iii. click 'Start Game', then wait for the solution to appear
    iv. if a 'sudoku server is offline' dialog appears click refresh and try again

4. Netbeans project setup and build
  a. copy the Topic Thunder java source to a new Netbeans dev region
  b. create a new Netbeans project for each Topic Thunder packaged component    
    i New Project --> Maven --> Project with existing POM
  c. build all projects
  d. focus on the Topic Thunder Game Station project, and run it
    i. alternatively, you can run the related jar in a terminal (shows shutdown thread behaviour)

Appendix A

    <property name="handler">
      <bean id="sec" class="org.eclipse.jetty.server.handler.HandlerCollection">
        <property name="handlers">
          <list>
          ...
            <bean class="org.eclipse.jetty.webapp.WebAppContext">
              <property name="contextPath" value="/TopicThunder" />
              <property name="resourceBase" value="${activemq.home}/webapps/TopicThunder" />
              <property name="logUrlOnStart" value="true" />
            </bean>
 
    
