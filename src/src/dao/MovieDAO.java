package dao;

import utils.DbHelper;
import entities.Movie;
import entities.Showtime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieDAO {
    public static int insert(Movie movie) {
        String sql = "INSERT INTO movies(id, country, language, title, duration, director_name, genre, for_age, price, release_date, description, poster_url)"
    + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return DbHelper.executeUpdate(sql,
                movie.getId().toString(),
                movie.getCountry(),
                movie.getLanguage(),
                movie.getTitle(),
                movie.getDuration(),
                movie.getDirectorName(),
                movie.getGenre(),
                movie.getForAge(),
                movie.getPrice(),
                Date.valueOf(movie.getReleaseDate()),
                movie.getDescription(),
                movie.getPosterURL()
        );
    }

    public static Movie findById(UUID id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        Movie movie = null;
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    movie = new Movie(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("country"),
                        rs.getString("language"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("director_name"),
                        rs.getString("genre"),
                        rs.getInt("for_age"),
                        rs.getBigDecimal("price"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("poster_url")
                    );
                    // Load showtimes for this movie
                    String sqlS = "SELECT * FROM showtimes WHERE movie_id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlS)) {
                        ps2.setString(1, id.toString());
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                Showtime s = new Showtime();
                                s.setId(UUID.fromString(rs2.getString("id")));
                                s.setDateShow(rs2.getDate("date_show").toLocalDate());
                                s.setStartTime(rs2.getTime("start_time").toLocalTime());
                                s.setEndTime(rs2.getTime("end_time").toLocalTime());
                                s.setMovie_id(id);
                                s.setRoom_id(rs2.getLong("room_id"));
                                movie.addShowtime(s);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }

    public static List<Movie> findByName(String key) {
        String sql = "SELECT * FROM movies WHERE title LIKE ?";
        List<Movie> movies = new ArrayList<>();
    
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, "%" + key + "%");
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("country"),
                        rs.getString("language"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("director_name"),
                        rs.getString("genre"),
                        rs.getInt("for_age"),
                        rs.getBigDecimal("price"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("poster_url")
                    );
    
                    // Load showtimes for this movie
                    String sqlS = "SELECT * FROM showtimes WHERE movie_id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlS)) {
                        ps2.setString(1, movie.getId().toString());
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                Showtime s = new Showtime();
                                s.setId(UUID.fromString(rs2.getString("id")));
                                s.setDateShow(rs2.getDate("date_show").toLocalDate());
                                s.setStartTime(rs2.getTime("start_time").toLocalTime());
                                s.setEndTime(rs2.getTime("end_time").toLocalTime());
                                s.setMovie_id(movie.getId());
                                s.setRoom_id(rs2.getLong("room_id"));
                                movie.addShowtime(s);
                            }
                        }
                    }
    
                    movies.add(movie);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return movies;
    }    

    public static List<Movie> findAll() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT id FROM movies";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(findById(UUID.fromString(rs.getString("id"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int update(Movie movie) {
        String sql = "UPDATE movies SET country=?, language=?, title=?, duration=?, director_name=?, genre=?, for_age=?, price=?, release_date=?, description=?, poster_url=? WHERE id=?";
        return DbHelper.executeUpdate(sql,
                movie.getCountry(),
                movie.getLanguage(),
                movie.getTitle(),
                movie.getDuration(),
                movie.getDirectorName(),
                movie.getGenre(),
                movie.getForAge(),
                movie.getPrice(),
                Date.valueOf(movie.getReleaseDate()),
                movie.getDescription(),
                movie.getPosterURL(),
                movie.getId().toString()
        );
    }

    public static int delete(UUID id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        return DbHelper.executeUpdate(sql, id.toString());
    }
}