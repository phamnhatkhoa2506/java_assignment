package test;

import service.RoomService;
import service.SeatService;
import entities.Room;
import entities.Seat;

import java.util.List;

public class SeatServiceTest {
    public static void main(String[] args) {
        RoomService roomService = new RoomService();
        SeatService seatService = new SeatService();
//
        // Ensure a room exists
        Room r = new Room("Room F", 50, 10);
        Room createdRoom = roomService.createRoom(r);

        // 1. List seats by room
        System.out.println("== List Seats for Room ==");
        List<Seat> seats = seatService.getSeatsByRoom(createdRoom.getId());
        seats.forEach(System.out::println);


    }
}