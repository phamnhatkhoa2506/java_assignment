package service;

import dao.SeatDAO;
import entities.Seat;

import java.util.List;

public class SeatService {
    public Seat createSeat(Seat seat) {
        return SeatDAO.insert(seat) > 0 ? seat : null;
    }

    public Seat getSeatById(String id) {
        return SeatDAO.findById(id);
    }

    public List<Seat> getSeatsByRoom(long roomId) {
        return SeatDAO.findAllByRoom(roomId);
    }

    public boolean deleteSeat(String id) {
        return SeatDAO.delete(id) > 0;
    }
}