package sg.edu.nus.iss.august_2022_assessment_practice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.august_2022_assessment_practice.model.News;
import sg.edu.nus.iss.august_2022_assessment_practice.service.NewsService;

@Controller
public class NewsController {
    
    @Autowired
    NewsService newsService;

    // show all news articles
    @GetMapping(path={"", "/"})
    public String showAllNews(Model model){

        List<News> newsList = newsService.getArticles();
        model.addAttribute("newsList", newsList);

        return "news";
    }


    // handle saving of articles
    @PostMapping("/articles")
    public String saveArticles(@RequestParam(required=false, name="selectedArticleId") List<String> selectedArticleIds){

        // check if selectedArticleIds has entries
        if (selectedArticleIds != null) {

            // For every articleId, use the saveArticle method to save the article to redis
            for (String articleId : selectedArticleIds) {
                newsService.saveArticle(articleId);
            }

            return "redirect:/";
        }

        return "redirect:/";
    }
}
