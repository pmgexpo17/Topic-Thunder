package org.gilmarie.web.jmsclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebClientNumerator extends HttpServlet
{
	private static final transient Logger LOG = LoggerFactory.getLogger(WebClientNumerator.class);
	
    private int sequenceNum;
    private Object seqNumLock = new Object();

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
		
		sequenceNum = 0;
		LOG.info("[WebClientNumerator] sequence number initialized : " + sequenceNum);
    }

	private String getNextSeqNumber() {
		
		synchronized (seqNumLock) {
			
			sequenceNum = (sequenceNum == 999999) ? 1 : sequenceNum + 1;			
			return String.format("%06d",sequenceNum);
		}
	}
		
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // lets turn the HTTP post into a JMS Message
       response.setContentType("text/xml");
       response.setHeader("Cache-Control", "no-cache");

       response.setStatus(HttpServletResponse.SC_OK);
       StringWriter swriter = new StringWriter();
       PrintWriter writer = new PrintWriter(swriter);
       String seqNum = getNextSeqNumber();
       LOG.info("[WebClientNumerator] sequence number for session id : " + seqNum);
       writer.println("<ajax-response>");
       writer.println(String.format("<response seqnum='%s'/>",seqNum));
	   writer.print("</ajax-response>");
       writer.flush();
       String m = swriter.toString();
       response.getWriter().println(m); 
   }

}
