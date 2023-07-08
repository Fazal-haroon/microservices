package com.fazaltuts4u.serviceregistry.clientServerCommunication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientTry {
    public static void main(String[] args) throws IOException, InterruptedException {
// API endpoint URL
        String url = "http://113.203.237.14:3010/api/all";

// Request body JSON
        String requestBody = "{\"Channel\":\"web\",\"phoneNumber\":\"9233220509030\",\"firstName\":\"visham\",\"lastName\":\"vhatri\",\"cnic\":\"4430374387717\",\"email\":\"vishamkhatri333@gmail.com\",\"tncAccepted\":\"true\",\"ppAccepted\":\"true\",\"FCMtoken\":\"123456\"}";

// Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

// Create HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

// Send the request and receive the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        if (statusCode == 200) {
// Successful request
            System.out.println("Request succeeded. Response Body: " + response.body());
        } else if (statusCode == 400) {
// Bad request
            System.out.println("Bad request. Error message: " + response.body());
        } else {
// Other response codes
            System.out.println("Unexpected response. Response Code: " + statusCode);
        }

        /*
         * // Print the response System.out.println("Response Code: " +
         * response.statusCode()); System.out.println("Response Body: " +
         * response.body());
         */
    }
}
