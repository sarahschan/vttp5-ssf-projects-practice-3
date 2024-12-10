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
    

    // Find an article by ID (from Crypto API)
    public News findArticleById(String articleId){

        List<News> allArticles = getArticles();

        for (News article : allArticles){
            if (article.getId().equals(articleId)){
                return article;
            }
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
            JsonObject jsonNews = jReader.readObject();

            // Extract data and create News POJO
                String id = jsonNews.getString("id");
                Long publishedOn = jsonNews.getJsonNumber("publishedOn").longValue();
                String title = jsonNews.getString("title");
                String url = jsonNews.getString("url");
                String imageUrl = jsonNews.getString("imageUrl");
                String body = jsonNews.getString("body");
                String tags = jsonNews.getString("tags");
                String categories = jsonNews.getString("categories");
            
            News article = new News(id, publishedOn, title, url, imageUrl, body, tags, categories);
            savedArticles.add(article);

        }

        return savedArticles;
    }
}
