package test;

import service.MovieService;
import entities.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MovieServiceTest {
    public static void main(String[] args) {
    	try {
            MovieService movieService = new MovieService();

            // 1. Create a new movie
            System.out.println("== Create Movie ==");
            Movie m = new Movie(
                "USA",
                "English",
                "Test Movie",
                120,
                "Director Name",
                "Action",
                18,
                new BigDecimal("9.99"),
                LocalDate.of(2025, 5, 1),
                "Description",
                "http://example.com/poster.jpg"
            );
            Movie created = movieService.createMovie(m);
            System.out.println("Created: " + created);

            // 2. Retrieve by ID
            System.out.println("\n== Get Movie by ID ==");
            Movie fetched = movieService.getMovieById(created.getId());
            System.out.println("Fetched: " + fetched);

            // 3. List all movies
            System.out.println("\n== List All Movies ==");
            List<Movie> all = movieService.getAllMovies();
            all.forEach(System.out::println);

//            // 4. Update movie
//            System.out.println("\n== Update Movie ==");
//            fetched.setTitle("Updated Title");
//            boolean updateResult = movieService.updateMovie(fetched);
//            System.out.println("Update result: " + updateResult);
//            System.out.println("After update: " + movieService.getMovieById(fetched.getId()));
//
//            // 5. Delete movie
//            System.out.println("\n== Delete Movie ==");
//            boolean deleteResult = movieService.deleteMovie(fetched.getId());
//            System.out.println("Delete result: " + deleteResult);
//            System.out.println("After delete: " + movieService.getMovieById(fetched.getId()));
    	}
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
}