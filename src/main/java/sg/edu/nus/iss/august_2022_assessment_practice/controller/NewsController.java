package sg.edu.nus.iss.august_2022_assessment_practice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import sg.edu.nus.iss.august_2022_assessment_practice.model.News;
import sg.edu.nus.iss.august_2022_assessment_practice.service.NewsService;

@Controller
public class NewsController {
    
    @Autowired
    NewsService newsService;

    @GetMapping()
    public String showAllNews(Model model){

        List<News> newsList = newsService.getArticles();
        model.addAttribute("newsList", newsList);

        return "news";
    }
}
