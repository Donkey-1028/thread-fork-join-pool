package com.opennaru.buahn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/forkjoinpool.do")
public class MyForkJoinPool extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		long start_time = System.currentTimeMillis();
		
		List<String> urls = Arrays.asList(
                "http://localhost:8080/ahn-sync-async/responser.do",
                "http://localhost:8080/ahn-sync-async/responser.do",
                "http://localhost:8080/ahn-sync-async/responser.do",
                "http://localhost:8080/ahn-sync-async/responser.do"
        );  
		
		// Create a ForkJoinPool with the default parallelism level
        ForkJoinPool pool = ForkJoinPool.commonPool();

        // Create a task to send requests to the APIs
        ApiRequestTask task = new ApiRequestTask(urls);

        // Invoke the task and get the results
        List<String> responses = pool.invoke(task);

        long end_time = System.currentTimeMillis();
        
        // Print the responses
        for (String response : responses) {
            System.out.println(response);
        }

        System.out.println("total duration : " + (end_time-start_time));
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
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = null;
            try {
                httpRequest = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .build();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            HttpResponse<String> httpResponse = null;
            try {
                httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(url + " response: " + httpResponse.statusCode());
        }
    }
}