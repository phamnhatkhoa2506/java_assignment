package dao;

import utils.DbHelper;
import entities.User;
import entities.Booking;
import entities.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {
    public static int insert(User user) {
        String sql = "INSERT INTO users(id, username, password, email, firstname, lastname, birthday) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return DbHelper.executeUpdate(sql,
                user.getId().toString(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                Date.valueOf(user.getBirthday())
        );
    }

    public static User findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getDate("birthday").toLocalDate()
                    );
                    // Load bookings for this user
                    String sqlB = "SELECT * FROM bookings WHERE user_id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlB)) {
                        ps2.setString(1, id.toString());
                        try (ResultSet rb = ps2.executeQuery()) {
                            while (rb.next()) {
                                Booking bk = new Booking();
                                UUID bid = UUID.fromString(rb.getString("id"));
                                bk.setId(bid);
                                bk.setBookingDate(rb.getDate("booking_date").toLocalDate());
                                bk.setBookingTime(rb.getTime("booking_time").toLocalTime());
                                bk.setBookingPrice(rb.getBigDecimal("booking_price"));
                                // Load seats for this booking
                                String sqlS =
                                    "SELECT s.id, s.row_num, s.col_num, s.room_id " +
                                    "FROM booking_seats bs JOIN seats s ON bs.seat_id = s.id " +
                                    "WHERE bs.booking_id = ?";
                                try (PreparedStatement ps3 = conn.prepareStatement(sqlS)) {
                                    ps3.setString(1, bid.toString());
                                    try (ResultSet rs3 = ps3.executeQuery()) {
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
                                user.addBooking(bk);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static List<User> findByName(String key) {
        String sql = "SELECT * FROM users WHERE lastname LIKE ?";
        List<User> users = new ArrayList<>();
        try (Connection conn = DbHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getDate("birthday").toLocalDate()
                    );
                    // Load bookings for this user
                    String sqlB = "SELECT * FROM bookings WHERE user_id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlB)) {
                        ps2.setString(1, user.getId().toString());
                        try (ResultSet rb = ps2.executeQuery()) {
                            while (rb.next()) {
                                Booking bk = new Booking();
                                UUID bid = UUID.fromString(rb.getString("id"));
                                bk.setId(bid);
                                bk.setBookingDate(rb.getDate("booking_date").toLocalDate());
                                bk.setBookingTime(rb.getTime("booking_time").toLocalTime());
                                bk.setBookingPrice(rb.getBigDecimal("booking_price"));
                                // Load seats for this booking
                                String sqlS =
                                    "SELECT s.id, s.row_num, s.col_num, s.room_id " +
                                    "FROM booking_seats bs JOIN seats s ON bs.seat_id = s.id " +
                                    "WHERE bs.booking_id = ?";
                                try (PreparedStatement ps3 = conn.prepareStatement(sqlS)) {
                                    ps3.setString(1, bid.toString());
                                    try (ResultSet rs3 = ps3.executeQuery()) {
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
                                user.addBooking(bk);
                            }
                        }
                    }

                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id FROM users";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(findById(UUID.fromString(rs.getString("id"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static int update(User user) {
        String sql = "UPDATE users SET username=?, password=?, email=?, firstname=?, lastname=?, birthday=? WHERE id=?";
        return DbHelper.executeUpdate(sql,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                Date.valueOf(user.getBirthday()),
                user.getId().toString()
        );
    }

    public static int delete(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return DbHelper.executeUpdate(sql, id.toString());
    }
}