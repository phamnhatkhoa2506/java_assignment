package service;

import dao.MovieDAO;
import entities.Movie;

import java.util.List;
import java.util.UUID;

public class MovieService {
    public Movie createMovie(Movie movie) {
    	Movie c_movie = MovieDAO.insert(movie) > 0 ? movie : null;
        if(c_movie == null) throw new RuntimeException("Error in creating movie");
    	return c_movie;
    }

    public Movie getMovieById(UUID id) {
        Movie movie = MovieDAO.findById(id);
        if(movie == null) throw new RuntimeException("Movie ID not found");
        return movie;
    }

    public List<Movie> searchByName(String key) {
        List<Movie> movies = MovieDAO.findByName(key);
        return movies;
    }

    public List<Movie> getAllMovies() {
        return MovieDAO.findAll();
    }

    public boolean updateMovie(Movie movie) {
        return MovieDAO.update(movie) > 0;
    }

    public boolean deleteMovie(UUID id) {
        return MovieDAO.delete(id) > 0;
    }
}
