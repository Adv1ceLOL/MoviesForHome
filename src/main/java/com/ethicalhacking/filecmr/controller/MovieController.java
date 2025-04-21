package com.ethicalhacking.filecmr.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.service.MovieService;
import com.ethicalhacking.filecmr.service.ReviewService;


@Controller
public class MovieController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getAllMovies(Model model) {
        return "redirect:/";
    }

    @PostMapping("/movies")
    public String getMovieByTitle(@RequestParam String title, Model model) {
        // Recupera i film in base al titolo
        List<Movie> movies = movieService.getMoviesByTitleContaining(title);

        Map<String, List<Movie>> groupedMovies = movies.stream()
            .collect(Collectors.groupingBy(Movie::getGenre));

        List<Long> movieIds = movies.stream().map(Movie::getId).toList();
        Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);

        model.addAttribute("genre2movies", groupedMovies);
        model.addAttribute("avgRatings", avgRatings);

        List<Movie> allMovies = movieService.getAllMovies();

        List<String> genres = allMovies.stream()
            .map(Movie::getGenre)
            .distinct()
            .collect(Collectors.toList());

        model.addAttribute("genres", genres);

        return "index";
    }

    @GetMapping("/movies/{genre}")
    public String getMoviesByGenre(@PathVariable String genre, Model model) {
        // Recupera i film in base al genere
        List<Movie> moviesByGenre = movieService.getMoviesByGenre(genre);

        Map<String, List<Movie>> groupedMovies = moviesByGenre.stream()
            .collect(Collectors.groupingBy(Movie::getGenre));

        List<Long> movieIds = moviesByGenre.stream().map(Movie::getId).toList();
        Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);

        model.addAttribute("genre2movies", groupedMovies);
        model.addAttribute("avgRatings", avgRatings);

        List<Movie> movies = movieService.getAllMovies();

        List<String> genres = movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .collect(Collectors.toList());

        model.addAttribute("genres", genres);

        return "index";
    }

    @PostMapping("/addMovie")
    // Solo per admin role (aggiungere un pulsante quando si è loggati come admin)
    public String addMovie(@ModelAttribute Movie movie, Model model) {
        
        movieService.saveMovieWithCoverImage(movie);
        
        return "redirect:/";
    }

    @PostMapping("/deleteMovie/{id}")
    // Solo per admin role (aggiungere un pulsante quando si è loggati come admin)
    public String deleteMovie(@PathVariable Long id) {

        movieService.deleteMovieById(id);

        return "redirect:/";
    }
    
}
