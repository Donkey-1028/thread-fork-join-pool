package com.opennaru.buahn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/accounts")
public class Accounts extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long random = Double.valueOf(Math.random() * 10 * 1000).longValue();
		//long random = 10000;
	    System.out.println("Accounts called, " + "duration: " + random);
	    try {
	        Thread.sleep(random);
	    } catch ( Exception e ) {
	        e.printStackTrace();
	    }
	}
}
