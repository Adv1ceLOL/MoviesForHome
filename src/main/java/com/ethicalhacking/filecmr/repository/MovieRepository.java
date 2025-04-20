package com.ethicalhacking.filecmr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ethicalhacking.filecmr.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenre(String genre);
    List<Movie> findByYear(String year);
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByDirectorContainingIgnoreCase(String director);
    
}
