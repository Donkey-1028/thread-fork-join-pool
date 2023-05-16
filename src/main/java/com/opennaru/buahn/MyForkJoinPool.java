package com.opennaru.buahn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/forkjoinpool")
public class MyForkJoinPool extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Map<String, String> apiInfos = new HashMap<String, String>();
		
		if(System.getenv("REMOTE_URL") == null ) {
			apiInfos.put("test01", "http://localhost:8080/ahn-sync-async/responser");
			apiInfos.put("test02", "http://localhost:8080/ahn-sync-async/responser");
		} else {
			apiInfos.put("news", "http://" + System.getenv("NEWS_URL"));
			apiInfos.put("accounts", "http://" + System.getenv("ACCOUNTS_URL"));
			apiInfos.put("weathers", "http://" + System.getenv("WEATHERS_URL"));
			apiInfos.put("stock", "http://" + System.getenv("STOCK_URL"));
		}
		
		// Create a ForkJoinPool with the default parallelism level
		ForkJoinPool pool = new ForkJoinPool();

        // Invoke the task and get the results
        // 메인 스레드도 compute 행위에 사용될 거면 invoke 사용
		//ApiRequestTask task = new ApiRequestTask(apiInfos);
        //List<String> responses = pool.invoke(task);
        
        // Submit the task
        // 메인 스레드가 compute 행위를 하지 않으려면 submit 사용
        ApiRequestTask task = new ApiRequestTask(apiInfos);
        pool.submit(task);
        
        try {
        	// task 결과 받아오기
        	// get method가 task가 완료될 때 까지 기다린 후 결과 값 반환
			Map<String, String> apiResponses = task.get();
			
			for(Map.Entry<String, String> entry : apiResponses.entrySet()) {
				req.setAttribute(entry.getKey(), entry.getValue());
			}
			
			req.getRequestDispatcher("/portal.jsp").forward(req, resp);
			
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
	}

    private static class ApiRequestTask extends RecursiveTask<Map<String, String>> {
        private final Map<String, String> apiInfos;

        public ApiRequestTask(Map<String, String> apiInfos) {
            this.apiInfos = apiInfos;
        }

        @Override
        protected Map<String, String> compute() {
            if (apiInfos.size() <= 1) {
                return sendRequest(apiInfos);
            } else {
                int mid = apiInfos.size() / 2;
                Map<String, String> leftTaskMap = new HashMap<>();
                Map<String, String> rightTaskMap = new HashMap<>();
                
                int index = 0;
				for(Map.Entry<String, String> entry : apiInfos.entrySet()) {
					if(index < mid) {
						leftTaskMap.put(entry.getKey(), entry.getValue());
					} else {
						rightTaskMap.put(entry.getKey(), entry.getValue());
					}
					index++;
                }
                
                ApiRequestTask leftTask = new ApiRequestTask(leftTaskMap);
                ApiRequestTask rightTask = new ApiRequestTask(rightTaskMap);

                // execute the subtasks in parallel
                leftTask.fork();
                
                Map<String, String> rightResponses = rightTask.compute();
                Map<String, String> leftResponses = leftTask.join();

                // merge the responses
                Map<String, String> responses = new HashMap<>(leftResponses);
                responses.putAll(rightResponses);
                return responses;
            }
        }

      private Map<String, String> sendRequest(Map<String, String> apiInfos) {
    	  
    	  Map<String, String> apiResponse = new HashMap<>();
    	  
    	  int timeout = 10;
    	  RequestConfig config = RequestConfig.custom()
    	    .setConnectTimeout(timeout * 1000)
    	    .setConnectionRequestTimeout(timeout * 1000)
    	    .setSocketTimeout(timeout * 1000).build();
    	  
    	  String apiName = String.join(", ", apiInfos.keySet());
    	  String url = String.join(", ", apiInfos.values());
    	  
	      try {
	    	  URI uri = new URIBuilder(url).build();
	    	  
	    	  CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	    	  
	    	  HttpGet httpGet = new HttpGet(uri);
	    	  
	    	  HttpResponse response = httpClient.execute(httpGet);
	    	  
              int statusCode = response.getStatusLine().getStatusCode();
              if (statusCode == HttpStatus.SC_OK) {
            	  HttpEntity entity = response.getEntity();
            	  String responseString = EntityUtils.toString(entity, "UTF-8");
            	  apiResponse.put(apiName, responseString);
            	  return apiResponse;
                  //return Arrays.asList(uri + " response: " + statusCode);
              } else {
            	  apiResponse.put(apiName, " Unexpected HTTP status code: " + statusCode);
                  return apiResponse;
              }
          } catch (IOException | URISyntaxException e) {
              e.printStackTrace();
              apiResponse.put(apiName, " Unexpected HTTP Error");
              return apiResponse;
          }
	   }
    }
}
