package sg.edu.nus.iss.august_2022_assessment_practice.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonArray;
import sg.edu.nus.iss.august_2022_assessment_practice.constant.Constant;
import sg.edu.nus.iss.august_2022_assessment_practice.model.News;
import sg.edu.nus.iss.august_2022_assessment_practice.repository.NewsRepo;

@Service
public class NewsService {
    
    @Autowired
    NewsRepo newsRepo;

    RestTemplate template = new RestTemplate();


    // Call Crypto API and get all the articles
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
    

    // Find an article by ID (from Redis Database)
    public News findArticleById(String articleId){

        // Note that you CANNOT use getArticles for this because it checks from the Crypto API
        //  the instruction is to pull from the redis database of saved articles
        //  the Crypto API refreshes super fast so an article that might still be in redis may no longer be on the API

        // check if key exists
        if (newsRepo.hasHashKey(Constant.REDIS_KEY, articleId)){
            
            // If the key exists, retrieve it
            String articleString = String.valueOf(newsRepo.get(Constant.REDIS_KEY, articleId));

            // Make conversion from JsonObject String -> JsonObject -> POJO
            JsonReader jReader = Json.createReader(new StringReader(articleString));
            JsonObject articleJsonObject = jReader.readObject();
                // Extract
                String id = articleJsonObject.getString("id");
                Long publishedOn = articleJsonObject.getJsonNumber("publishedOn").longValue();
                String title = articleJsonObject.getString("title");
                String url = articleJsonObject.getString("url");
                String imageUrl = articleJsonObject.getString("imageUrl");
                String body = articleJsonObject.getString("body");
                String tags = articleJsonObject.getString("tags");
                String categories = articleJsonObject.getString("categories");
            
            // Create POJO
            News article = new News(id, publishedOn, title, url, imageUrl, body, tags, categories);

            return article;

        }

        return null;
    }


    // Save an article to Redis
    public void saveArticle(String articleId){
        
        // get the article
        News articleToSave = findArticleById(articleId);

        // build the newsJsonObject
        JsonObject newsJsonObject = Json.createObjectBuilder()
                                    .add("id", articleToSave.getId())
                                    .add("publishedOn", articleToSave.getPublishedOn())
                                    .add("publishedOnFormatted", articleToSave.getPublishedOnFormatted().toString())
                                    .add("title", articleToSave.getTitle())
                                    .add("url", articleToSave.getUrl())
                                    .add("imageUrl", articleToSave.getImageUrl())
                                    .add("body", articleToSave.getBody())
                                    .add("tags", articleToSave.getTags())
                                    .add("categories", articleToSave.getCategories())
                                    .build();

        // save to redis
        newsRepo.create(Constant.REDIS_KEY, articleId, newsJsonObject.toString());

    }


    // Get all saved articles from Redis
    public List<News> allSavedArticles(){

        // Retrieve all saved articles from Redis
        Map<Object, Object> articlesMap = newsRepo.getEntries(Constant.REDIS_KEY);

        // Prepare the list to hold the POJOs
        List<News> savedArticles = new ArrayList<>();

        // Deserialize JsonObject String -> JsonObject -> News
        for (Map.Entry<Object, Object> entry : articlesMap.entrySet()){
            JsonReader jReader = Json.createReader(new StringReader(entry.getValue().toString()));
            JsonObject articleJsonObject = jReader.readObject();

            // Extract data and create News POJO
                String id = articleJsonObject.getString("id");
                Long publishedOn = articleJsonObject.getJsonNumber("publishedOn").longValue();
                String title = articleJsonObject.getString("title");
                String url = articleJsonObject.getString("url");
                String imageUrl = articleJsonObject.getString("imageUrl");
                String body = articleJsonObject.getString("body");
                String tags = articleJsonObject.getString("tags");
                String categories = articleJsonObject.getString("categories");
            
            News article = new News(id, publishedOn, title, url, imageUrl, body, tags, categories);
            savedArticles.add(article);

        }

        return savedArticles;
    }
}
