package sg.edu.nus.iss.august_2022_assessment_practice.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.august_2022_assessment_practice.model.News;
import sg.edu.nus.iss.august_2022_assessment_practice.service.NewsService;

@RestController
public class NewsRestController {
    
    @Autowired
    NewsService newsService;

    @GetMapping()
    public List<News> showAllNews(){

        List<News> newsList = newsService.getArticles();

        return newsList;
    }
}
