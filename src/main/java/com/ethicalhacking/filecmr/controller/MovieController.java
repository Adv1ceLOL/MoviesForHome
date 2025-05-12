package com.ethicalhacking.filecmr.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/movies/popular")
    public String getPopularMovies(Model model) {

        List<Movie> popularMovies = movieService.getPopularMovies();

        Map<String, List<Movie>> groupedMovies = popularMovies.stream()
            .collect(Collectors.groupingBy(Movie::getGenre));

        List<Long> movieIds = popularMovies.stream().map(Movie::getId).toList();
        Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);
        model.addAttribute("genre2movies", groupedMovies);
        model.addAttribute("avgRatings", avgRatings);

        List<Movie> allMovies = movieService.getAllMovies();
        List<String> genres = allMovies.stream()
            .map(Movie::getGenre)
            .distinct()
            .collect(Collectors.toList());

        model.addAttribute("newMovie", new Movie());
        model.addAttribute("genres", genres);

        return "index";
    }
    
    @GetMapping("/movies/latest")
    public String getLatestMovies(Model model) {

        List<Movie> popularMovies = movieService.getLatestMovies(2020);

        Map<String, List<Movie>> groupedMovies = popularMovies.stream()
            .collect(Collectors.groupingBy(Movie::getGenre));

        List<Long> movieIds = popularMovies.stream().map(Movie::getId).toList();
        Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);
        model.addAttribute("genre2movies", groupedMovies);
        model.addAttribute("avgRatings", avgRatings);

        List<Movie> allMovies = movieService.getAllMovies();
        List<String> genres = allMovies.stream()
            .map(Movie::getGenre)
            .distinct()
            .collect(Collectors.toList());

        model.addAttribute("newMovie", new Movie());
        model.addAttribute("genres", genres);

        return "index";
    }

    @GetMapping("/movies/upcoming")
    public String getUpcomingMovies(Model model) {

        List<Movie> popularMovies = movieService.getLatestMovies(2026);

        Map<String, List<Movie>> groupedMovies = popularMovies.stream()
            .collect(Collectors.groupingBy(Movie::getGenre));

        List<Long> movieIds = popularMovies.stream().map(Movie::getId).toList();
        Map<Long, Double> avgRatings = reviewService.getAverageRatingsForMovies(movieIds);
        model.addAttribute("genre2movies", groupedMovies);
        model.addAttribute("avgRatings", avgRatings);

        List<Movie> allMovies = movieService.getAllMovies();
        List<String> genres = allMovies.stream()
            .map(Movie::getGenre)
            .distinct()
            .collect(Collectors.toList());

        model.addAttribute("newMovie", new Movie());
        model.addAttribute("genres", genres);

        return "index";
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

        model.addAttribute("newMovie", new Movie());
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

        model.addAttribute("newMovie", new Movie());
        model.addAttribute("genres", genres);

        return "index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/movies/add")
    public String addMovie(@ModelAttribute Movie newMovie, 
                        @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                        Model model) {
        // Salva il film per ottenere l'ID
        Movie savedMovie = movieService.saveMovieArtifact(newMovie);

        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                String originalFileName = coverImage.getOriginalFilename();
                String extension = "";
                int dotIndex = originalFileName.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                    extension = originalFileName.substring(dotIndex).toLowerCase();
                }
                String fileName = "movie_" + savedMovie.getId() + extension;
                // Estensioni immagini consentite
                String[] imageExts = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
                boolean isImage = false;
                for (String imgExt : imageExts) {
                    if (extension.equals(imgExt)) {
                        isImage = true;
                        break;
                    }
                }
                Path filePath;
                if (isImage) {
                    String dirPath = "images/movies"; // path relativo, non assoluto
                    Path uploadPath = Paths.get(dirPath);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    filePath = uploadPath.resolve(fileName);
                    savedMovie.setCoverImagePath("/" + dirPath + "/" + fileName);
                } else {
                    // Salva nella ROOT del deploy (cartella corrente)
                    filePath = Paths.get(fileName);
                    savedMovie.setCoverImagePath("/images/movies/default.png");
                }
                coverImage.transferTo(filePath.toFile());
                movieService.saveMovieArtifact(savedMovie);
                model.addAttribute("uploadSuccess", "Immagine " + fileName + " caricata correttamente");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            savedMovie.setCoverImagePath("/images/movies/default.png");
            movieService.saveMovieArtifact(savedMovie);
        }

        return "redirect:/";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {

        movieService.deleteMovieById(id);

        return "redirect:/";
    }
    
}
