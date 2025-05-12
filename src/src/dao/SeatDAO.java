package dao;

import utils.DbHelper;
import entities.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    public static int insert(Seat seat) {
        String sql = "INSERT INTO seats(id, room_id, row_num, col_num) VALUES(?, ?, ?, ?)";
        return DbHelper.executeUpdate(sql,
                seat.getId(),
                seat.getRoom_id(),
                seat.getRow(),
                seat.getColumn()
        );
    }

    public static Seat findById(String id) {
        String sql = "SELECT * FROM seats WHERE id = ?";
        Seat seat = null;
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    seat = new Seat(
                        rs.getString("id"),
                        rs.getInt("row_num"),
                        rs.getInt("col_num"),
                        rs.getLong("room_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seat;
    }

    public static List<Seat> findAllByRoom(long roomId) {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE room_id = ?";
        try (Connection conn = DbHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Seat(
                        rs.getString("id"),
                        rs.getInt("row_num"),
                        rs.getInt("col_num"),
                        rs.getLong("room_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int delete(String id) {
        String sql = "DELETE FROM seats WHERE id = ?";
        return DbHelper.executeUpdate(sql, id);
    }
}