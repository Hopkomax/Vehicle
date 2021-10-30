package ua.service.vehicles.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

@Service
public class VinDecoderService {

    private final String url;

    public VinDecoderService(@Value("${vin.decoder.url}") String url) {
        this.url = url;
    }

    public DecodedVin decodeVin(String vin) throws IOException {
        String readyUrl = url + vin;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(readyUrl);
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", "Basic YTUzN2M2YmQtZDQwNi00OGZhLWEwZTctMzVhYjExMGQ1YjE2");
            request.addHeader("partner-token", "8ad06ec2e7c045a19376c983208d1e31");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            
            JSONObject obj = new JSONObject(json);
            String make = obj.getJSONObject("data").get("make").toString();
            String model = obj.getJSONObject("data").get("model").toString();
            int year = Integer.parseInt(obj.getJSONObject("data").get("year").toString());

            System.out.println("make data: " + make);
            System.out.println("json " + json);

            return new DecodedVin(make, model, year);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}