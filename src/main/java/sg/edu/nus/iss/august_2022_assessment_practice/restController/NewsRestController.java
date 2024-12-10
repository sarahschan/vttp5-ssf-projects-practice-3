package sg.edu.nus.iss.august_2022_assessment_practice.restController;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.august_2022_assessment_practice.model.News;
import sg.edu.nus.iss.august_2022_assessment_practice.service.NewsService;

@RestController
public class NewsRestController {
    
    @Autowired
    NewsService newsService;

    @GetMapping("/news/{id}")
    public ResponseEntity<String> getSavedArticle(@PathVariable("id") String id){

        News foundArticle = newsService.findArticleById(id);

        // if null was returned, means article doesn't exist
        if (foundArticle == null) {

            // return 404 Not Found and error response
            // Set the header
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            // build the body - error message
            String errorMessage = "Cannot find news article " + id;
            JsonObject errorBody = Json.createObjectBuilder()
                                        .add("error", errorMessage)
                                        .build();

            // Build the response entity
            ResponseEntity<String> response = ResponseEntity.status(404)
                                                            .headers(headers)
                                                            .body(errorBody.toString());

            return response;

        } else {

            // return 200 OK and article
            // Set the header
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            // build the body - found article
            //  quite sure we don't have to do this, but it's fine
            //  FYI - if you change it to ResponseEntity<Object>, you can just return the foundArticle object
            JsonObject newsJsonObject = Json.createObjectBuilder()
                                            .add("id", foundArticle.getId())
                                            .add("publishedOn", foundArticle.getPublishedOn())
                                            .add("publishedOnFormatted", foundArticle.getPublishedOnFormatted().toString())
                                            .add("title", foundArticle.getTitle())
                                            .add("url", foundArticle.getUrl())
                                            .add("imageUrl", foundArticle.getImageUrl())
                                            .add("body", foundArticle.getBody())
                                            .add("tags", foundArticle.getTags())
                                            .add("categories", foundArticle.getCategories())
                                            .build();

            // Build the response entity
            ResponseEntity<String> response = ResponseEntity.status(200)
                                                            .headers(headers)
                                                            .body(newsJsonObject.toString());
            return response;
        }
    }


    // FAFO - Additional endpoint to get all articles saved in redis
    @GetMapping("/news/all")
    public ResponseEntity<List<News>> getAllSavedArticles(){
        
        List<News> savedArticles = newsService.allSavedArticles();

        // return 200 OK and article
        // Set the header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Build the response entity
        ResponseEntity<List<News>> response = ResponseEntity.status(200)
                                                              .headers(headers)
                                                              .body(savedArticles);

        return response;


    }

    @GetMapping("news/all/json")
    public ResponseEntity<List<String>> getAllSavedArticlesJson(){
        
        List<News> savedArticles = newsService.allSavedArticles();

        List<String> savedArticlesJson = new ArrayList<>();

        for (News article : savedArticles) {
            JsonObject articleJson = Json.createObjectBuilder()
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
            savedArticlesJson.add(articleJson.toString());
        }

         // return 200 OK and article
        // Set the header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Build the response entity
        ResponseEntity<List<String>> response = ResponseEntity.status(200)
                                                              .headers(headers)
                                                              .body(savedArticlesJson);

        return response;
    }

}
