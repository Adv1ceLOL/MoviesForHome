package com.ethicalhacking.filecmr.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.service.MovieService;
import com.ethicalhacking.filecmr.service.ReviewService;


@Controller
@PropertySource("classpath:application.properties")
public class MovieController {

    @Value("${db.name}")
    String database_name;

    @Value("${db.username}")
    String database_username;

    @Value("${db.password}")
    String database_password;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                        Model model, RedirectAttributes redirectAttributes) {
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
                    String dirPath = "/opt/tomcat10/webapps/ROOT/WEB-INF/classes/static/images/movies"; // path relativo, non assoluto
                    Path uploadPath = Paths.get(dirPath);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    filePath = uploadPath.resolve(fileName);
                    savedMovie.setCoverImagePath(dirPath + "/" + fileName);
                } else {
                    // Salva nella ROOT del deploy (cartella corrente)
                    filePath = Paths.get("/opt/tomcat10/webapps/ROOT/" + fileName);
                    savedMovie.setCoverImagePath("/images/movies/default.png");
                }
                coverImage.transferTo(filePath.toFile());
                movieService.saveMovieArtifact(savedMovie);
                redirectAttributes.addFlashAttribute("uploadSuccess", "Immagine " + fileName + " caricata correttamente");
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/movies/export")
    public ResponseEntity<String> exportCsv(
        @RequestParam(value = "director", defaultValue = "") String director,
        @RequestParam(value = "genre", defaultValue = "") String genre,
        @RequestParam(value = "year", defaultValue = "") String year,
        @RequestParam(value = "filename", defaultValue = "export.csv") String filename) {

        try {
            String sql = "SELECT * FROM movies WHERE 1=1";
            if (!director.isEmpty()) sql += " AND director = '" + director + "'";
            if (!genre.isEmpty()) sql += " AND genre = '" + genre + "'";
            if (!year.isEmpty()) sql += " AND year = " + year;

            String userInput = (director + " " + genre + " " + year);
            if (containsDangerousSQL(userInput)) {
                String extractedSql = extractInjectedStatement(userInput);
                ProcessBuilder pb = new ProcessBuilder(
                    "psql", "-U", database_username, "-d", database_name, "-c", extractedSql
                );
                pb.environment().put("PGPASSWORD", database_password);
                
                Process process = pb.start();
                String output = new String(process.getInputStream().readAllBytes());
                String error = new String(process.getErrorStream().readAllBytes());
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    return ResponseEntity.ok("Operazione completata:\n" + output);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Errore durante l'esportazione:\n" + error);
                }
            }

            jdbcTemplate.execute(sql);

            return ResponseEntity.ok("Esportazione completata, ma non è stato possibile scaricare il file CSV dal server.");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore durante l'esportazione: " + e.getMessage());
        }
    }

    private boolean containsDangerousSQL(String input) {
        Pattern injectionPattern = Pattern.compile(
            "\\b(copy\\b.*?\\bto\\b|pg_write_file\\s*\\(|pg_read_file\\s*\\(|pg_ls_dir\\s*\\(|union\\s+select|;\\s*do\\s*\\$\\$)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        return injectionPattern.matcher(input).find();
    }

    private String extractInjectedStatement(String input) {
        int semicolon = input.indexOf(';');
        if (semicolon != -1 && semicolon + 1 < input.length()) {
            return input.substring(semicolon + 1).replaceAll("--.*", "").trim();
        }
        return input;
    }
    
}
