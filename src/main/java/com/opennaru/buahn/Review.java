package com.opennaru.buahn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/review")
public class Review extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String error_raised = System.getenv("ERROR_RAISED");
		String version = System.getenv("VERSION");
		
		if(error_raised != null) {
			if(error_raised.equals("true") == true) {
				resp.sendError(500);
			}
		} else {
			long random = Double.valueOf(Math.random() * 10 * 300).longValue();
			//long random = 10000;
		    System.out.println("review called, " + "duration: " + random);
		    try {
		        Thread.sleep(random);
		    } catch ( Exception e ) {
		        e.printStackTrace();
		    }
		    if(version != null) {
		    	if(version.equals("v1")) {
		    		req.setAttribute("result", "OPENMARU APM 리뷰");
		    	} else if (version.equals("v2")) {
			    	req.setAttribute("result", "OPENMARU Cluster 리뷰");
				}
		    } 
		    
	        req.getRequestDispatcher("/result.jsp").forward(req, resp);
		}
	}
}

