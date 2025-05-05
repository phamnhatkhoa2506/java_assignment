package dao;

import utils.DbHelper;
import entities.Showtime;
import entities.Booking;
import entities.Movie;
import entities.Room;
import entities.Seat;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ShowtimeDAO {
    public static List<LocalDate> getAllDates() {
        String query = "SELECT DISTINCT date_show FROM showtimes";
        List<LocalDate> dates = new ArrayList<>();
        try (Connection conn = DbHelper.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                LocalDate date = rs.getDate(1, null).toLocalDate();
                dates.add(date);
            }

            dates.sort(Comparator.reverseOrder());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return dates;
    }

    public static List<LocalTime> getAllTimes() {
        String query = "SELECT DISTINCT start_time FROM showtimes";
        List<LocalTime> times = new ArrayList<>();
        try (Connection conn = DbHelper.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                LocalTime time = rs.getTime(1, null).toLocalTime();
                times.add(time);
            }

            times.sort(Comparator.reverseOrder());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return times;
    }

    public static List<String> getAllMovieNames() {
        List<Showtime> showtimes = findAll();
        List<String> movieNames = new ArrayList<>();

        for (Showtime showtime : showtimes) {
            String movieName = showtime.getMovie().getTitle();
            if (!movieNames.contains(movieName))
                movieNames.add(movieName);
        }

        return movieNames;
    }

    public static int insert(Showtime s) {
        String sql = "INSERT INTO showtimes(id, date_show, start_time, end_time, movie_id, room_id) VALUES(?, ?, ?, ?, ?, ?)";
        return DbHelper.executeUpdate(sql,
                s.getId().toString(),
                Date.valueOf(s.getDateShow()),
                Time.valueOf(s.getStartTime()),
                Time.valueOf(s.getStartTime().plusMinutes(s.getMovie().getDuration())),
                s.getMovie().getId().toString(),
                s.getRoom().getId()
        );
    }

    public static Showtime findById(UUID id) {
        String sql = "SELECT * FROM showtimes WHERE id = ?";
        try (Connection conn = DbHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                Showtime s = new Showtime();
                s.setId(id);
                s.setDateShow(rs.getDate("date_show").toLocalDate());
                s.setStartTime(rs.getTime("start_time").toLocalTime());
                s.setEndTime(rs.getTime("end_time").toLocalTime());
                s.setMovie_id(UUID.fromString(rs.getString("movie_id")));
                s.setRoom_id(rs.getLong("room_id"));

                // --- load bookings & seats ---
                loadBookingsAndSeats(conn, s);

                // --- load movie object ---
                String sqlM = "SELECT * FROM movies WHERE id = ?";
                try (PreparedStatement psM = conn.prepareStatement(sqlM)) {
                    psM.setString(1, s.getMovie_id().toString());
                    try (ResultSet rm = psM.executeQuery()) {
                        if (rm.next()) {
                            Movie movie = new Movie(
                                UUID.fromString(rm.getString("id")),
                                rm.getString("country"),
                                rm.getString("language"),
                                rm.getString("title"),
                                rm.getInt("duration"),
                                rm.getString("director_name"),
                                rm.getString("genre"),
                                rm.getInt("for_age"),
                                rm.getBigDecimal("price"),
                                rm.getDate("release_date").toLocalDate(),
                                rm.getString("description"),
                                rm.getString("poster_url")
                            );
                            s.setMovie(movie);
                        }
                    }
                }

                // --- load room object ---
                String sqlR = "SELECT * FROM rooms WHERE id = ?";
                try (PreparedStatement psR = conn.prepareStatement(sqlR)) {
                    psR.setLong(1, s.getRoom_id());
                    try (ResultSet rr = psR.executeQuery()) {
                        if (rr.next()) {
                            Room room = new Room(
                                rr.getLong("id"),
                                rr.getString("name"),
                                rr.getInt("total_seats"),
                                rr.getInt("seats_per_row")
                            );
                            room.setSeatsAvailable(rr.getInt("seats_available"));
                            // load seats of room
                            List<Seat> seats = new ArrayList<>();
                            String sqlS = "SELECT * FROM seats WHERE room_id = ?";
                            try (PreparedStatement psS = conn.prepareStatement(sqlS)) {
                                psS.setLong(1, room.getId());
                                try (ResultSet rs2 = psS.executeQuery()) {
                                    while (rs2.next()) {
                                        seats.add(new Seat(
                                            rs2.getString("id"),
                                            rs2.getInt("row_num"),
                                            rs2.getInt("col_num"),
                                            rs2.getLong("room_id")
                                        ));
                                    }
                                }
                            }
                            room.setSeats(seats);
                            s.setRoom(room);
                        }
                    }
                }

                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static void loadBookingsAndSeats(Connection conn, Showtime s) throws SQLException {
        String sqlB = "SELECT * FROM bookings WHERE showtime_id = ?";
        try (PreparedStatement psB = conn.prepareStatement(sqlB)) {
            psB.setString(1, s.getId().toString());
            try (ResultSet rb = psB.executeQuery()) {
                while (rb.next()) {
                    Booking bk = new Booking();
                    UUID bid = UUID.fromString(rb.getString("id"));
                    bk.setId(bid);
                    bk.setBookingDate(rb.getDate("booking_date").toLocalDate());
                    bk.setBookingTime(rb.getTime("booking_time").toLocalTime());
                    bk.setBookingPrice(rb.getBigDecimal("booking_price"));
                    bk.setShowtime(s);

                    String sqlS = "SELECT s.id, s.row_num, s.col_num, s.room_id "
                                + "FROM booking_seats bs JOIN seats s ON bs.seat_id=s.id "
                                + "WHERE bs.booking_id = ?";
                    try (PreparedStatement psS = conn.prepareStatement(sqlS)) {
                        psS.setString(1, bid.toString());
                        try (ResultSet rs3 = psS.executeQuery()) {
                            while (rs3.next()) {
                                Seat st = new Seat(
                                    rs3.getString("id"),
                                    rs3.getInt("row_num"),
                                    rs3.getInt("col_num"),
                                    rs3.getLong("room_id")
                                );
                                bk.addSeat(st);
                            }
                        }
                    }
                    s.addBooking(bk);
                }
            }
        }
    }

    public static List<Showtime> findAll() {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtimes";
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

     /**
     * Find showtimes on a specific date.
     */
    public static List<Showtime> findAllByDate(LocalDate date) {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtimes WHERE date_show = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(findById(UUID.fromString(rs.getString("id"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Showtime> findAllByTime(LocalTime time) {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtimes WHERE start_time = ?";
        try (Connection conn = DbHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, Time.valueOf(time));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(findById(UUID.fromString(rs.getString("id"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

   /**
     * Find showtimes on a specific date and room.
     */
    public static List<Showtime> findAllByRoomId(long roomId) {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtimes WHERE room_id = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(findById(UUID.fromString(rs.getString("id"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int update(Showtime s) {
        String sql = "UPDATE showtimes SET date_show=?, start_time=?, end_time=?, movie_id=?, room_id=? WHERE id=?";
        return DbHelper.executeUpdate(sql,
                Date.valueOf(s.getDateShow()),
                Time.valueOf(s.getStartTime()),
                Time.valueOf(s.getEndTime()),
                s.getMovie().getId().toString(),
                s.getRoom().getId(),
                s.getId().toString()
        );
    }

    /**
     * Find showtimes by movie ID.
    */
    public static List<Showtime> findAllByMovieId(UUID movieId) {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtimes WHERE movie_id = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movieId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(findById(UUID.fromString(rs.getString("id"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static List<Showtime> findByDateAndRoomId(LocalDate date, long roomId) {
        List<Showtime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtimes WHERE date_show = ? AND room_id = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setLong(2, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(findById(UUID.fromString(rs.getString("id"))));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int delete(UUID id) {
        String sql = "DELETE FROM showtimes WHERE id = ?";
        return DbHelper.executeUpdate(sql, id.toString());
    }
}
