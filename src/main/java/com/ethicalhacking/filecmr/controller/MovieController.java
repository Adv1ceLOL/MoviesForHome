package com.ethicalhacking.filecmr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ethicalhacking.filecmr.model.Movie;
import com.ethicalhacking.filecmr.service.MovieService;


@Controller
public class MovieController {
    @Autowired
    private MovieService movieService;

    @PostMapping("/addMovie")
    // Solo per admin role (aggiungere un pulsante quando si è loggati come admin)
    public String addMovie(@ModelAttribute Movie movie, Model model) {
        
        movieService.saveMovieWithCoverImage(movie);
        
        return "redirect:/";
    }

    @PostMapping("/deleteMovie/{id}")
    // Solo per admin role (aggiungere un pulsante quando si è loggati come admin)
    public String deleteMovie(@PathVariable Long id) {

        movieService.deleteMovieById(id);

        return "redirect:/";
    }
    
}
