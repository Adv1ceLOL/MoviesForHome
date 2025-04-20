package com.ethicalhacking.filecmr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ethicalhacking.filecmr.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    List<Review> findByMovieId(Long movieId);
    List<Review> findByRating(Integer rating);
    List<Review> findByRatingBetween(Integer minRating, Integer maxRating);
}
