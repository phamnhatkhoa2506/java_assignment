package test;

import service.RoomService;
import entities.Room;

import java.util.List;

public class RoomServiceTest {
    public static void main(String[] args) {
        RoomService roomService = new RoomService();

        // 1. Create room
        System.out.println("== Create Room ==");
        Room r = new Room("Room A", 30, 5);
        Room created = roomService.createRoom(r);
        System.out.println("Created: " + created);

        // 2. Retrieve by ID
        System.out.println("\n== Get Room by ID ==");
        Room fetched = roomService.getRoomById(created.getId());
        System.out.println("Fetched: " + fetched);
        System.out.println("Seats Generated: " + fetched.getSeats().size());

        // 3. List all rooms
        System.out.println("\n== List All Rooms ==");
        List<Room> all = roomService.getAllRooms();
        all.forEach(System.out::println);
//
//        // 4. Update room
//        System.out.println("\n== Update Room ==");
//        fetched.setName("Room B");
//        boolean updateResult = roomService.updateRoom(fetched);
//        System.out.println("Update result: " + updateResult);
//        System.out.println("After update: " + roomService.getRoomById(fetched.getId()));
//
//        // 5. Delete room
//        System.out.println("\n== Delete Room ==");
//        boolean deleteResult = roomService.deleteRoom(fetched.getId());
//        System.out.println("Delete result: " + deleteResult);
    }
}