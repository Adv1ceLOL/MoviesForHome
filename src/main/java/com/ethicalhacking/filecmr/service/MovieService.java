package com.ethicalhacking.filecmr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.repository.MovieRepository;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public void deleteMovieById(Long id) {
        movieRepository.deleteById(id);
    }

    public void deleteAllMovies() {
        movieRepository.deleteAll();
    }

    public void saveMovie(Movie movie) {
        movieRepository.save(movie);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

}
