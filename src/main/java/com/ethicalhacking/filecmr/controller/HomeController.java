package com.ethicalhacking.filecmr.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.service.MovieService;
import com.ethicalhacking.filecmr.service.ReviewService;


@Controller
public class HomeController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/")
    public String homepageLoad(Model model) {

        List<Movie> movies = movieService.getAllMovies();

        Map<String, List<Movie>> groupedMovies = movies.stream()
            .collect(Collectors.groupingBy(Movie::getGenre));

        List<Long> movieIds = movies.stream().map(Movie::getId).toList();
        Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);

        model.addAttribute("genre2movies", groupedMovies);
        model.addAttribute("avgRatings", avgRatings);

        return "index";
    }
    
}