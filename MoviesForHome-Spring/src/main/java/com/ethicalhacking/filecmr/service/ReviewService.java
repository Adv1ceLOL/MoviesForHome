package com.ethicalhacking.filecmr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ethicalhacking.filecmr.model.Review;
import com.ethicalhacking.filecmr.repository.ReviewRepository;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }

    public void deleteAllReviews() {
        reviewRepository.deleteAll();
    }

    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewsByRatingBetween(int minRating, int maxRating) {
        return reviewRepository.findByRatingBetween(minRating, maxRating);
    }

    public List<Review> getReviewsByRating(int rating) {
        return reviewRepository.findByRating(rating);
    }
    
    
    public Map<Long, Double> getAverageRatingsForMovies(List<Long> movieIds) {
        List<Object[]> rawResults = reviewRepository.findAverageRatingsForMovieIds(movieIds);

        // Converto in mappa Movie ID → Media (null se non presente)
        Map<Long, Double> avgMap = rawResults.stream().collect(Collectors.toMap(
            row -> (Long) row[0],
            row -> (Double) row[1]
        ));

        // Includo anche i film che non hanno recensioni
        Map<Long, Double> completeMap = new HashMap<>();
        for (Long id : movieIds) {
            completeMap.put(id, avgMap.getOrDefault(id, null));
        }

        return completeMap;
    }
}
