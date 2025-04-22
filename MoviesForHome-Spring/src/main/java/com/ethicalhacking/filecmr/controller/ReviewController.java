package com.ethicalhacking.filecmr.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class ReviewController {
    
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
    public String showComments(Model model) {
        List<Movie> allMovies = movieService.getAllMovies();
        List<Movie> movies = movieService.getAllMoviesWithReviewsAndUsers();

        Map<String, List<UserCommentDTO>> groupedComments = new LinkedHashMap<>();

        for (Movie movie : movies) {
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
    public String addReview(@RequestParam Long movieId, @RequestParam String content, @RequestParam Integer rating, Model model) {

        Review review = new Review();
        review.setMovie(movieService.getMovieById(movieId));
        review.setRating(rating);
        review.setContent(content);
        review.setUser(userService.getUserById(1L)); // da modificare con l'utente loggato

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
