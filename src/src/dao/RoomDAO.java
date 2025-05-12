package dao;

import utils.DbHelper;
import entities.Room;
import entities.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    /**
     * Insert a new room and auto-generate its seats.
     * @param room the Room object (id will be set after insertion)
     * @return generated room ID, or -1 on error
     */
    public static long insert(Room room) {
        String sqlRoom = "INSERT INTO rooms(name, total_seats, seats_per_row, seats_available) VALUES(?, ?, ?, ?)";
        String sqlSeat = "INSERT INTO seats(id, room_id, row_num, col_num) VALUES(?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement psRoom = null;
        PreparedStatement psSeat = null;
        ResultSet rsKeys = null;
        try {
            conn = DbHelper.getConnection();
            conn.setAutoCommit(false);

            psRoom = conn.prepareStatement(sqlRoom, Statement.RETURN_GENERATED_KEYS);
            psRoom.setString(1, room.getName());
            psRoom.setInt(2, room.getTotalSeats());
            psRoom.setInt(3, room.getSeatsPerRow());
            psRoom.setInt(4, room.getSeatsAvailable());
            int affected = psRoom.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return -1;
            }

            rsKeys = psRoom.getGeneratedKeys();
            if (!rsKeys.next()) {
                conn.rollback();
                return -1;
            }

            long roomId = rsKeys.getLong(1);
            room.setId(roomId);

            psSeat = conn.prepareStatement(sqlSeat);
            int totalSeats = room.getTotalSeats();
            int seatsPerRow = room.getSeatsPerRow();
            int rows = (totalSeats + seatsPerRow - 1) / seatsPerRow;
            int count = 0;

            for (int r = 1; r <= rows && count < totalSeats; r++) {
                for (int c = 1; c <= seatsPerRow && count < totalSeats; c++) {
                    String seatId = roomId + "@" + r + "_" + c;
                    psSeat.setString(1, seatId);
                    psSeat.setLong(2, roomId);
                    psSeat.setInt(3, r);
                    psSeat.setInt(4, c);
                    psSeat.executeUpdate();
                    count++;
                }
            }

            conn.commit();
            return roomId;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return -1;
        } finally {
            try { if (rsKeys != null) rsKeys.close(); } catch (SQLException ignore) {}
            try { if (psSeat != null) psSeat.close(); } catch (SQLException ignore) {}
            try { if (psRoom != null) psRoom.close(); } catch (SQLException ignore) {}
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
            }
        }
    }

    public static Room findById(long id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        Room room = null;
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    room = new Room(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("total_seats"),
                        rs.getInt("seats_per_row")
                    );
                    room.setSeatsAvailable(rs.getInt("seats_available"));
                    // Load seats
                    String sqlS = "SELECT * FROM seats WHERE room_id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlS)) {
                        ps2.setLong(1, id);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            List<Seat> seats = new ArrayList<>();
                            while (rs2.next()) {
                                seats.add(new Seat(
                                    rs2.getString("id"),
                                    rs2.getInt("row_num"),
                                    rs2.getInt("col_num"),
                                    rs2.getLong("room_id")
                                ));
                            }
                            room.setSeats(seats);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    public static List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id FROM rooms";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(findById(rs.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static int update(Room room) {
        String sql = "UPDATE rooms SET name=?, total_seats=?, seats_per_row=?, seats_available=? WHERE id=?";
        return DbHelper.executeUpdate(sql,
                room.getName(),
                room.getTotalSeats(),
                room.getSeatsPerRow(),
                room.getSeatsAvailable(),
                room.getId()
        );
    }

    public static int delete(long id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        return DbHelper.executeUpdate(sql, id);
    }
}