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
    List<Movie> findByYear(Integer year);
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByDirectorContainingIgnoreCase(String director);

    // Custom query to find movies based on the higher average of ratings in reviews (show only those with rating > 4)
    @Query("SELECT m FROM Movie m JOIN m.reviews r GROUP BY m.id HAVING AVG(r.rating) >= 4 ORDER BY AVG(r.rating) DESC")
    List<Movie> findMostPopularMovies();

    List<Movie> findByYearGreaterThanEqual(Integer year);
    
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.reviews r LEFT JOIN FETCH r.user WHERE m.id = :movieId")
    Optional<Movie> findByIdWithReviewsAndUsers(@Param("movieId") Long movieId);

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.reviews r LEFT JOIN FETCH r.user")
    List<Movie> findAllWithReviewsAndUsers();
    
}
