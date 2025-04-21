package com.ethicalhacking.filecmr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ethicalhacking.filecmr.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenre(String genre);
    List<Movie> findByYear(String year);
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByDirectorContainingIgnoreCase(String director);
    
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews r LEFT JOIN FETCH r.user WHERE m.id = :movieId")
    Optional<Movie> findByIdWithReviewsAndUsers(@Param("movieId") Long movieId);

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.reviews r LEFT JOIN FETCH r.user")
    List<Movie> findAllWithReviewsAndUsers();
    
}
