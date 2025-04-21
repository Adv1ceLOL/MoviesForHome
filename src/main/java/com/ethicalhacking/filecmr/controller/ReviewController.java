package com.ethicalhacking.filecmr.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.model.Review;
import com.ethicalhacking.filecmr.model.User;
import com.ethicalhacking.filecmr.service.MovieService;
import com.ethicalhacking.filecmr.service.ReviewService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

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
    }

    @GetMapping("/comments")
    public String showComments(Model model) {
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
                        review.getContent()
                    ));
                }
            }

            if (!comments.isEmpty()) {
                groupedComments.put(movie.getTitle(), comments);
            }
        }

        model.addAttribute("groupedComments", groupedComments);
        model.addAttribute("moviesWithComments", movies);

        return "comments";
    }

    @GetMapping("/comments/{movieId}")
    public String showCommentsForMovie(@PathVariable Long movieId, Model model) {
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
                review.getContent()
            ));
        }
    }

    Map<String, List<UserCommentDTO>> groupedComments = new LinkedHashMap<>();
    if (!comments.isEmpty()) {
        groupedComments.put(movie.getTitle(), comments);
    }

    model.addAttribute("groupedComments", groupedComments);
    
    return "comments";
}
    

}
