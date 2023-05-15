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
import java.util.concurrent.RecursiveTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
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
			urls.add("http://localhost:8080/ahn-sync-async/response");
			urls.add("http://localhost:8080/ahn-sync-async/responser");
		} else {
			urls.add("http://" + System.getenv("NEWS_URL"));
			urls.add("http://" + System.getenv("ACCOUNTS_URL"));
			urls.add("http://" + System.getenv("WEATHERS_URL"));
			urls.add("http://" + System.getenv("STOCK_URL"));
		}
		
		// Create a ForkJoinPool with the default parallelism level
		ForkJoinPool pool = new ForkJoinPool();

        // Invoke the task and get the results
        // 메인 스레드도 compute 행위에 사용될 거면 invoke 사용
		//ApiRequestTask task = new ApiRequestTask(urls);
        //List<String> responses = pool.invoke(task);
        
        // Submit the task
        // 메인 스레드가 compute 행위를 하지 않으려면 submit 사용
        ApiRequestTask task = new ApiRequestTask(urls);
        pool.submit(task);
        
        try {
        	// task 결과 받아오기
        	// get method가 task가 완료될 때 까지 기다린 후 결과 값 반환
			List<String> responses = task.get();
			System.out.println(responses);
			req.setAttribute("result", responses);
			req.getRequestDispatcher("/portal.jsp").forward(req, resp);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
        
        long end_time = System.currentTimeMillis();

        System.out.println("total duration : " + (end_time-start_time));
        System.out.println(pool.getPoolSize());
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

      private List<String> sendRequest(String url) {
	      try {
	    	  // timeout set
	    	  int timeout = 5;
	    	  RequestConfig config = RequestConfig.custom()
	    	    .setConnectTimeout(timeout * 1000)
	    	    .setConnectionRequestTimeout(timeout * 1000)
	    	    .setSocketTimeout(timeout * 1000).build();
	    	  
	    	  URI uri = new URIBuilder(url).build();
	    	  
	    	  CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	    	  
	    	  HttpGet httpGet = new HttpGet(uri);
	    	  
	    	  HttpResponse response = httpClient.execute(httpGet);
	    	  
              int statusCode = response.getStatusLine().getStatusCode();
              if (statusCode == HttpStatus.SC_OK) {
                  return Arrays.asList(uri + " response: " + statusCode);
              } else {
                  return Arrays.asList(uri + " Unexpected HTTP status code: " + statusCode);
              }
          } catch (IOException | URISyntaxException e) {
              e.printStackTrace();
              return null;
          }
	   }
    }
}
