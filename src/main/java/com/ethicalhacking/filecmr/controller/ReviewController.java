package com.ethicalhacking.filecmr.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.model.Review;
import com.ethicalhacking.filecmr.model.User;
import com.ethicalhacking.filecmr.service.MovieService;
import com.ethicalhacking.filecmr.service.ReviewService;
import com.ethicalhacking.filecmr.service.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class ReviewController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    // DTO per i commenti degli utenti
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserCommentDTO {
        private String username;
        private String userProfileImage;
        private Integer rating;
        private String comment;
        private Long reviewId;
    }

    @GetMapping("/comments")
    public String showComments(@RequestParam(name = "filter", required = false) String filter, Model model) {
        List<Movie> allMovies = movieService.getAllMovies();
        List<Movie> movies = movieService.getAllMoviesWithReviewsAndUsers();

        Map<String, List<UserCommentDTO>> groupedComments = new LinkedHashMap<>();
        for (Movie movie : movies) {
            List<UserCommentDTO> comments = new ArrayList<>();
            if (filter != null && !filter.isEmpty()) {
                String baseQuery = "SELECT u.username, u.profile_image_path, r.rating, r.content, r.id FROM users u JOIN reviews r ON u.id = r.user_id WHERE r.movie_id = " + movie.getId() + " AND (" + filter + ")";
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(baseQuery);
                for (Map<String, Object> row : rows) {
                    comments.add(new UserCommentDTO(
                        (String) row.get("username"),
                        (String) row.get("profile_image_path"),
                        (Integer) row.get("rating"),
                        (String) row.get("content"),
                        ((Number) row.get("id")).longValue()
                    ));
                }
            } else {
                for (Review review : movie.getReviews()) {
                    User user = review.getUser();
                    if (user != null) {
                        comments.add(new UserCommentDTO(
                            user.getUsername(),
                            user.getProfileImagePath(),
                            review.getRating(),
                            review.getContent(),
                            review.getId()
                        ));
                    }
                }
            }
            if (!comments.isEmpty()) {
                groupedComments.put(movie.getTitle(), comments);
            }
        }
        model.addAttribute("movies", allMovies);
        model.addAttribute("groupedComments", groupedComments);
        model.addAttribute("moviesWithComments", movies);
        return "comments";
    }

    @GetMapping("/comments/{movieId}")
    public String showCommentsForMovie(@PathVariable Long movieId, Model model) {
    
    List<Movie> allMovies = movieService.getAllMovies();
    Optional<Movie> movieOpt = movieService.getMovieByIdWithReviewsAndUsers(movieId);

    if (movieOpt.isEmpty()) {
        return "redirect:/comments"; // oppure una pagina di errore
    }

    Movie movie = movieOpt.get();
    List<UserCommentDTO> comments = new ArrayList<>();

    for (Review review : movie.getReviews()) {
        User user = review.getUser();
        if (user != null) {
            comments.add(new UserCommentDTO(
                user.getUsername(),
                user.getProfileImagePath(),
                review.getRating(),
                review.getContent(),
                review.getId()
            ));
        }
    }

    Map<String, List<UserCommentDTO>> groupedComments = new LinkedHashMap<>();
    if (!comments.isEmpty()) {
        groupedComments.put(movie.getTitle(), comments);
    }

    model.addAttribute("movies", allMovies);
    model.addAttribute("groupedComments", groupedComments);
    
    return "comments";

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/auth/comments/add")
    public String addReview(@RequestParam Long movieId, @RequestParam String content, @RequestParam Integer rating, Model model, RedirectAttributes redirectAttributes) {
        if (rating == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a star rating.");
            return "redirect:/comments/" + movieId;
        }
        Review review = new Review();
        review.setMovie(movieService.getMovieById(movieId));
        review.setRating(rating);
        review.setContent(content);
        
        // Get the currently logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        review.setUser(user);

        reviewService.saveReview(review);
        
        return "redirect:/comments/" + movieId;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/auth/reviews/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        
        reviewService.deleteReviewById(reviewId);
        
        return "redirect:/comments";
    }
    

}
