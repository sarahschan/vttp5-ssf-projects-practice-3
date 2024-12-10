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
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
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
    

    // Find an article by ID (from Crypto API)
    public News findArticleByIdFromAPI(String articleId){

        List<News> allArticles = getArticles();

        for (News article : allArticles) {
            if (article.getId().equals(articleId)){
                return article;
            }
        }

        return null;
    }


    // Save an article to Redis
    public void saveArticle(String articleId){
        
        // get the article
        News articleToSave = findArticleByIdFromAPI(articleId);

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


    // Find an article by ID (from Redis Database)
    public News findSavedArticleById(String articleId){

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


    // Get all saved articles from Redis (List POJOs)
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

    // This returns a Json Object with a single nested Json Array. Each element in the array is an article as a Json Object
    public String getAllSavedArticlesJsonString(){

        // Get the list of News POJOs
        List<News> allSavedArticlesPOJO = allSavedArticles();

        // Build the Json array
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (News article : allSavedArticlesPOJO) {
            // turn each article into a json object
            JsonObject articleJsonObject = Json.createObjectBuilder()
                                            .add("id", article.getId())
                                            .add("publishedOn", article.getPublishedOn())
                                            .add("publishedOnFormatted", article.getPublishedOnFormatted().toString())
                                            .add("title", article.getTitle())
                                            .add("url", article.getUrl())
                                            .add("imageUrl", article.getImageUrl())
                                            .add("body", article.getBody())
                                            .add("tags", article.getTags())
                                            .add("categories", article.getCategories())
                                            .build();
            // add the built object to the array
            jsonArrayBuilder.add(articleJsonObject);
        }

        // Build the final array
        JsonObject finalJsonObject = Json.createObjectBuilder()
                                    .add("articles", jsonArrayBuilder)
                                    .build();
        
        return finalJsonObject.toString();

    }





    // FOR REFERENCE: This returns an array of JsonObjects, each Object is a article
    // public String getAllSavedArticlesJsonString() {
    //     List<News> allSavedArticles = allSavedArticles(); // Fetch the list of articles

    //     JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

    //     for (News article : allSavedArticles) {
    //         JsonObjectBuilder articleBuilder = Json.createObjectBuilder();
    //         articleBuilder.add("id", article.getId())
    //                       .add("publishedOn", article.getPublishedOn())
    //                       .add("publishedOnFormatted", article.getPublishedOnFormatted().toString())
    //                       .add("title", article.getTitle())
    //                       .add("url", article.getUrl())
    //                       .add("imageUrl", article.getImageUrl())
    //                       .add("body", article.getBody())
    //                       .add("tags", article.getTags())
    //                       .add("categories", article.getCategories());

    //         jsonArrayBuilder.add(articleBuilder);
    //     }

    //     JsonArray articlesJsonArray = jsonArrayBuilder.build();

    //     // Return the JSON array as a string
    //     return articlesJsonArray.toString();
    // }



    // FOR REFERENCE: This returns one giant JsonObjects, with nested objects inside. Each nested object is an article (value), with the key being the id
    // public String getAllSavedArticlesJsonString() {
    //     List<News> allSavedArticlesPOJO = allSavedArticles();

    //     // Build the JSON object
    //     JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

    //     for (News article : allSavedArticlesPOJO) {
    //         JsonObjectBuilder articleBuilder = Json.createObjectBuilder();
    //         articleBuilder
    //             .add("id", article.getId())
    //             .add("publishedOn", article.getPublishedOn())
    //             .add("publishedOnFormatted", article.getPublishedOnFormatted().toString())
    //             .add("title", article.getTitle())
    //             .add("url", article.getUrl())
    //             .add("imageUrl", article.getImageUrl())
    //             .add("body", article.getBody())
    //             .add("tags", article.getTags())
    //             .add("categories", article.getCategories());
            
    //         // Use the article ID as the key
    //         jsonObjectBuilder.add(article.getId(), articleBuilder);
    //     }

    //     // Create the final JSON object
    //     JsonObject finalJsonObject = jsonObjectBuilder.build();

    //     // Convert the JSON object to a string
    //     return finalJsonObject.toString();
    // }

}
