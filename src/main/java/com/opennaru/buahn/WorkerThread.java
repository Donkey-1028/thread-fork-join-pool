package com.opennaru.buahn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;


public class WorkerThread implements Runnable {
	  
    private String url;
    
    public WorkerThread(String url){
        this.url=url;
    }

// java11 httpclient    
//    @Override
//    public void run() {   
//        HttpClient httpClient = HttpClient.newHttpClient();
//        HttpRequest httpRequest = null;
//        try {
//            httpRequest = HttpRequest.newBuilder()
//                    .uri(new URI(url))
//                    .build();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        HttpResponse<String> httpResponse = null;
//        try {
//            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(Arrays.asList(url + " response: " + httpResponse.statusCode()));
//    }
    
    @Override
    public void run() {   
      try {
    	  URI uri = new URIBuilder(url).build();
    	  
    	  HttpClient httpClient = HttpClientBuilder.create().build();
    	  
    	  HttpGet httpGet = new HttpGet(uri);
    	  
    	  HttpResponse response = httpClient.execute(httpGet);
    	  
          int statusCode = response.getStatusLine().getStatusCode();
          if (statusCode == HttpStatus.SC_OK) {
        	  System.out.println(Arrays.asList(uri + " response: " + statusCode));
          } else {
              throw new RuntimeException("Unexpected HTTP status code: " + statusCode);
          }
      } catch (IOException | URISyntaxException e) {
          throw new RuntimeException("Error while executing API request: " + e.getMessage(), e);
          
      }
    }
    
	  

    @Override
    public String toString(){
        return this.url;
    }
}
