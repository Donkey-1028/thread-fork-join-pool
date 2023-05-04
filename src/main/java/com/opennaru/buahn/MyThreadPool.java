package com.opennaru.buahn;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/threadpool.do")
public class MyThreadPool extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		long start_time = System.currentTimeMillis();
		
		List<String> urls = Arrays.asList(
                "http://localhost:8080/ahn-sync-async/responser.do",
                "http://localhost:8080/ahn-sync-async/responser.do",
                "http://localhost:8080/ahn-sync-async/responser.do",
                "http://localhost:8080/ahn-sync-async/responser.do"
        );
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < urls.size(); i++) {
            Runnable worker = new WorkerThread(urls.get(i));
            executor.execute(worker);
          }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        
        long end_time = System.currentTimeMillis();
        
        System.out.println("Finished all threads, ");
        System.out.println("total duration : " + (end_time-start_time));
        
    }
}