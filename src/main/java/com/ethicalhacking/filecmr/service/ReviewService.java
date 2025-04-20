package com.ethicalhacking.filecmr.service;

import java.util.List;

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
}
