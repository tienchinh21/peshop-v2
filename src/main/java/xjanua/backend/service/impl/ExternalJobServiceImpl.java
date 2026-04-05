package xjanua.backend.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import xjanua.backend.service.interfaces.ExternalJobService;

@Service
public class ExternalJobServiceImpl implements ExternalJobService {

    @Value("${backend.dotnet.url}")
    private String dotnetUrl;

    @Value("${spring.boot.url}")
    private String springBootUrl;

    @Value("${api.key}")
    private String apiKey;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean checkHandleProduct() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(dotnetUrl + "/CheckHandle/check-handle-product"))
                    .header("API-KEY", apiKey)
                    .build();

            String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return body.contains("\"isRunning\":true");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void callSetJob(String id, String apiName, String jsonData, Instant runTime) {
        try {
            Map<String, Object> payload = Map.of(
                    "id", id,
                    "apiName", springBootUrl + apiName,
                    "jsonData", mapper.readTree(jsonData),
                    "runTime", runTime.toString());

            String bodyJson = mapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(dotnetUrl + "/Job/set-job"))
                    .header("Content-Type", "application/json")
                    .header("API-KEY", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callDeleteJob(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(dotnetUrl + "/Job/delete-job/" + id))
                    .header("API-KEY", apiKey)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String callToDotnet(String url, String jsonData) {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(dotnetUrl + url))
                    .header("Content-Type", "application/json")
                    .header("API-KEY", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
