package service;

import dao.RoomDAO;
import entities.Room;

import java.util.List;

public class RoomService {
    public Room createRoom(Room room) {
        long room_id =  RoomDAO.insert(room);
        return getRoomById(room_id);
    }

    public Room getRoomById(long id) {
        return RoomDAO.findById(id);
    }

    public List<Room> getAllRooms() {
        return RoomDAO.findAll();
    }

    public boolean updateRoom(Room room) {
        return RoomDAO.update(room) > 0;
    }

    public boolean deleteRoom(long id) {
        return RoomDAO.delete(id) > 0;
    }
}