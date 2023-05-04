package com.opennaru.buahn;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class WorkerThread implements Runnable {
	  
    private String url;
    
    public WorkerThread(String url){
        this.url=url;
    }

    @Override
    public void run() {   
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
        System.out.println(Arrays.asList(url + " response: " + httpResponse.statusCode()));
    }

    @Override
    public String toString(){
        return this.url;
    }
}
