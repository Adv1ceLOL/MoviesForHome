package com.ethicalhacking.filecmr.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Movie saveMovieArtifact(Movie movie) {
        return movieRepository.save(movie);
    }

    public void saveMovieWithCoverImage(Movie movie) {
        Movie savedMovie = saveMovieArtifact(movie);
        String imagePath = "images/movies/movie_" + savedMovie.getId() + ".png";
        movie.setCoverImagePath(imagePath);
        movieRepository.save(movie);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    public List<Movie> getMoviesByTitleContaining(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> getPopularMovies() {
        return movieRepository.findMostPopularMovies();
    }

    public List<Movie> getLatestMovies(Integer year) {
        return movieRepository.findByYearGreaterThanEqual(year);
    }

    public List<Movie> getAllMoviesWithReviewsAndUsers() {
        return movieRepository.findAllWithReviewsAndUsers();
    }

    public Optional<Movie> getMovieByIdWithReviewsAndUsers(Long movieId) {
        return movieRepository.findByIdWithReviewsAndUsers(movieId);
    }

}
