package com.opennaru.buahn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/forkjoinpool")
public class MyForkJoinPool extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		long start_time = System.currentTimeMillis();
		
		List<String> urls = new ArrayList<String>();
		
		if(System.getenv("REMOTE_URL") == null ) {
			urls.add("http://localhost:8080/ahn-sync-async/responser");
			urls.add("http://localhost:8080/ahn-sync-async/responser");
		} else {
			urls.add("http://" + System.getenv("NEWS_URL"));
			urls.add("http://" + System.getenv("ACCOUNTS_URL"));
			urls.add("http://" + System.getenv("WEATHERS_URL"));
			urls.add("http://" + System.getenv("STOCK_URL"));
		}
		
		// Create a ForkJoinPool with the default parallelism level
        //ForkJoinPool pool = ForkJoinPool.commonPool();
		ForkJoinPool pool = new ForkJoinPool();

        // Create a task to send requests to the APIs
        List<ForkJoinTask<List<String>>> tasks = new ArrayList<>();
        //ApiRequestTask task = new ApiRequestTask(urls);

        // Invoke the task and get the results
        //List<String> responses = pool.invoke(task);
        

        ApiRequestTask task = new ApiRequestTask(urls);
        tasks.add(pool.submit(task));
        
        try {
			List<String> response = task.get();
			System.out.println(response);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        long end_time = System.currentTimeMillis();

        System.out.println("total duration : " + (end_time-start_time));
        System.out.println(pool.getPoolSize());
        
        req.setAttribute("type", "Fork Join Pool");
        req.getRequestDispatcher("/result.jsp").forward(req, resp);
	}

    private static class ApiRequestTask extends RecursiveTask<List<String>> {
        private final List<String> urls;

        public ApiRequestTask(List<String> urls) {
            this.urls = urls;
        }

        @Override
        protected List<String> compute() {
            if (urls.size() <= 1) {
                return sendRequest(urls.get(0));
            } else {
                int mid = urls.size() / 2;
                ApiRequestTask leftTask = new ApiRequestTask(urls.subList(0, mid));
                ApiRequestTask rightTask = new ApiRequestTask(urls.subList(mid, urls.size()));

                // execute the subtasks in parallel
                leftTask.fork();
                
                List<String> rightResponses = rightTask.compute();
                List<String> leftResponses = leftTask.join();

                // merge the responses
                List<String> responses = new ArrayList<>(leftResponses);
                responses.addAll(rightResponses);
                return responses;
            }
        }

// java11 httpclient        
//        private List<String> sendRequest(String url) {        
//            HttpClient httpClient = HttpClient.newHttpClient();
//            HttpRequest httpRequest = null;
//            try {
//                httpRequest = HttpRequest.newBuilder()
//                        .uri(new URI(url))
//                        .build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            HttpResponse<String> httpResponse = null;
//            try {
//                httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//            return Arrays.asList(url + " response: " + httpResponse.statusCode());
//        }

      private List<String> sendRequest(String url) {
	      try {
	    	  URI uri = new URIBuilder(url).build();
	    	  
	    	  HttpClient httpClient = HttpClientBuilder.create().build();
	    	  
	    	  HttpGet httpGet = new HttpGet(uri);
	    	  
	    	  HttpResponse response = httpClient.execute(httpGet);
	    	  
              int statusCode = response.getStatusLine().getStatusCode();
              if (statusCode == HttpStatus.SC_OK) {
                  return Arrays.asList(uri + " response: " + statusCode);
              } else {
                  throw new RuntimeException("Unexpected HTTP status code: " + statusCode);
              }
          } catch (IOException | URISyntaxException e) {
              throw new RuntimeException("Error while executing API request: " + e.getMessage(), e);
              
          }
	   }
    }
}
