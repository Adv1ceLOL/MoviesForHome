package com.ethicalhacking.filecmr.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class HomeController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/")
    public String homepage_lfi(@RequestParam(name = "filePath", required = false) String filePath, Model model) {
        
        if (filePath == null || filePath.isEmpty()) {
            // Nessun parametro: mostra la home page classica
            List<Movie> movies = movieService.getAllMovies();

            Map<String, List<Movie>> groupedMovies = movies.stream()
                .collect(Collectors.groupingBy(Movie::getGenre));

            List<Long> movieIds = movies.stream().map(Movie::getId).toList();
            Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);

            model.addAttribute("genre2movies", groupedMovies);
            model.addAttribute("avgRatings", avgRatings);

            List<String> genres = movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .collect(Collectors.toList());

            model.addAttribute("genres", genres);
            model.addAttribute("newMovie", new Movie());

            return "index";
        }

        String baseDir = getClass().getClassLoader().getResource("static/").getPath(); // directory di partenza
        String content = "";

        try {
            Path path = Paths.get(baseDir + filePath);
            content = Files.readString(path);
        } catch (Exception e) {
            content = "Errore nella lettura del file: " + e.getMessage();
        }

        model.addAttribute("fileContent", content);

        return "lfi"; // Mostra il contenuto del file
    }
    
    
}