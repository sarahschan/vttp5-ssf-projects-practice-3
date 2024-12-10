package sg.edu.nus.iss.august_2022_assessment_practice.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class News {
    
    private String id;
    private Long publishedOn;   // epoch seconds
    private LocalDate publishedOnFormatted;
    private String title;
    private String url;
    private String imageUrl;
    private String body;
    private String tags;
    private String categories;


    public News() {
    }


    public News(String id, Long publishedOn, String title, String url, String imageUrl, String body, String tags, String categories) {
        this.id = id;
        this.publishedOn = publishedOn;
        this.publishedOnFormatted = dateConverter(publishedOn);
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.body = body;
        this.tags = tags;
        this.categories = categories;
    }

    
    // Helper method to convert date from epoch seconds -> LocalDate
    private LocalDate dateConverter(Long publishedOn){
        return LocalDate.ofInstant(Instant.ofEpochSecond(publishedOn), ZoneId.systemDefault());
    }


    @Override
    public String toString() {
        return id + "," + publishedOn + "," + publishedOnFormatted + "," + title + "," + url + "," + imageUrl + "," + body + "," + tags + "," + categories;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(Long publishedOn) {
        this.publishedOn = publishedOn;
    }

    public LocalDate getPublishedOnFormatted() {
        return publishedOnFormatted;
    }

    public void setPublishedOnFormatted(LocalDate publishedOnFormatted) {
        this.publishedOnFormatted = publishedOnFormatted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
    
}
