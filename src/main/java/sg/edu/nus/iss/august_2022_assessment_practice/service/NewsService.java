package sg.edu.nus.iss.august_2022_assessment_practice.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonArray;
import sg.edu.nus.iss.august_2022_assessment_practice.constant.Constant;
import sg.edu.nus.iss.august_2022_assessment_practice.model.News;

@Service
public class NewsService {
    
    RestTemplate template = new RestTemplate();

    public List<News> getArticles(){
        
        ResponseEntity<String> response = template.getForEntity(Constant.CRYPTO_URL, String.class);
        String payloadJsonString = response.getBody();

        // Turn raw string into Json
        JsonReader jReader = Json.createReader(new StringReader(payloadJsonString));
        JsonObject jObject = jReader.readObject();

        // Extract only the data field
        JsonArray dataJsonArray = jObject.getJsonArray("Data");

        // Prepare the list to store the News POJOs
        List<News> newsList = new ArrayList<>();

        // Extract each news article from dataJsonArray
        for (int i = 0; i < dataJsonArray.size(); i++) {

            // Convert article into a Json Object
            JsonObject newsJsonObject = dataJsonArray.getJsonObject(i);
                // Extract fields
                String id = newsJsonObject.getString("id");
                Long publishedOn = newsJsonObject.getJsonNumber("published_on").longValue();
                String title = newsJsonObject.getString("title");
                String url = newsJsonObject.getString("url");
                String imageUrl = newsJsonObject.getString("imageurl");
                String body = newsJsonObject.getString("body");
                String tags = newsJsonObject.getString("tags");
                String categories = newsJsonObject.getString("categories");

            // Create the News POJO and add it to the list
            News news = new News(id, publishedOn, title, url, imageUrl, body, tags, categories);
            newsList.add(news);

        }

        return newsList;
    }
    
}
