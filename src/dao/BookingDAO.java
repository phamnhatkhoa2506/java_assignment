package dao;

import utils.DbHelper;
import entities.Booking;
import entities.Seat;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingDAO {
    public static int insert(Booking b) {
        String sql = "INSERT INTO bookings(id, booking_date, booking_time, booking_price, user_id, showtime_id) VALUES(?, ?, ?, ?, ?, ?)";
        int result = DbHelper.executeUpdate(sql,
                b.getId().toString(),
                Date.valueOf(b.getBookingDate()),
                Time.valueOf(b.getBookingTime()),
                b.getBookingPrice(),
                b.getUser().getId().toString(),
                b.getShowtime().getId().toString()
        );
        if (result > 0 && b.getSeats() != null) {
            for (Seat seat : b.getSeats()) {
                DbHelper.executeUpdate(
                    "INSERT INTO booking_seats(booking_id, seat_id) VALUES(?, ?)",
                    b.getId().toString(), seat.getId()
                );
            }
        }
        return result;
    }

    public static Booking findById(UUID id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        Booking booking = null;
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    booking = new Booking();
                    booking.setId(id);
                    booking.setBookingDate(rs.getDate("booking_date").toLocalDate());
                    booking.setBookingTime(rs.getTime("booking_time").toLocalTime());
                    booking.setBookingPrice(rs.getBigDecimal("booking_price"));
                    booking.setUser_id(UUID.fromString(rs.getString("user_id")));
                    booking.setShowtime_id(UUID.fromString(rs.getString("showtime_id")));
                    // Load seats for this booking
                    String sqlS =
                        "SELECT s.id, s.row_num, s.col_num, s.room_id " +
                        "FROM booking_seats bs JOIN seats s ON bs.seat_id=s.id " +
                        "WHERE bs.booking_id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlS)) {
                        ps2.setString(1, id.toString());
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                Seat st = new Seat(
                                    rs2.getString("id"),
                                    rs2.getInt("row_num"),
                                    rs2.getInt("col_num"),
                                    rs2.getLong("room_id")
                                );
                                booking.addSeat(st);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booking;
    }

    public static List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT id FROM bookings";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID bid = UUID.fromString(rs.getString("id"));
                list.add(findById(bid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int update(Booking b) {
        String sql = "UPDATE bookings SET booking_date=?, booking_time=?, booking_price=?, user_id=?, showtime_id=? WHERE id=?";
        int result = DbHelper.executeUpdate(sql,
                Date.valueOf(b.getBookingDate()),
                Time.valueOf(b.getBookingTime()),
                b.getBookingPrice(),
                b.getUser().getId().toString(),
                b.getShowtime().getId().toString(),
                b.getId().toString()
        );
        if (result > 0) {
            // Re-insert seats: delete then insert
            DbHelper.executeUpdate("DELETE FROM booking_seats WHERE booking_id = ?", b.getId().toString());
            if (b.getSeats() != null) {
                for (Seat seat : b.getSeats()) {
                    DbHelper.executeUpdate(
                        "INSERT INTO booking_seats(booking_id, seat_id) VALUES(?, ?)"
                        , b.getId().toString(), seat.getId());
                }
            }
        }
        return result;
    }

    public static int delete(UUID id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        return DbHelper.executeUpdate(sql, id.toString());
    }

    public static List<Booking> findByUserId(UUID userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT id FROM bookings WHERE user_id = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
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
     * Find bookings by date.
    */
    public static List<Booking> findByDate(LocalDate date) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT id FROM bookings WHERE booking_date = ?";
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

    /**
     * Find bookings by movie ID via join on showtimes.
    */
    public static List<Booking> findByMovieId(UUID movieId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.id FROM bookings b JOIN showtimes s ON b.showtime_id = s.id WHERE s.movie_id = ?";
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
    
    /**
     * Báo cáo doanh thu theo ngày.
     * @param date ngày cần tính doanh thu (không null)
     * @return tổng doanh thu, >=0
     * @throws IllegalArgumentException nếu date null
     */
    public static BigDecimal getRevenueByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        String sql = "SELECT SUM(booking_price) AS rev FROM bookings WHERE booking_date = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal rev = rs.getBigDecimal("rev");
                    return rev != null ? rev : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }


    public static Map<LocalDate, BigDecimal> getDailyRevenueByMonth(int year, int month) {
        if (year < 1970 || month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid year or month");
        }
        String sql = "SELECT booking_date, SUM(booking_price) AS rev " +
                     "FROM bookings " +
                     "WHERE YEAR(booking_date)=? AND MONTH(booking_date)=? " +
                     "GROUP BY booking_date";
        Map<LocalDate, BigDecimal> revenueMap = new LinkedHashMap<>();
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("booking_date").toLocalDate();
                    BigDecimal rev = rs.getBigDecimal("rev");
                    revenueMap.put(date, rev != null ? rev : BigDecimal.ZERO);
                }
            }
            // Fill days without bookings with 0
            YearMonth ym = YearMonth.of(year, month);
            for (int d = 1; d <= ym.lengthOfMonth(); d++) {
                LocalDate day = ym.atDay(d);
                revenueMap.putIfAbsent(day, BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenueMap;
    }
}