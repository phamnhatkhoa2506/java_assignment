package test;

import service.ShowtimeService;
import entities.Seat;

import java.util.List;


public class ShowtimeServiceTest {
    public static void main(String[] args) {
        ShowtimeService showtimeService = new ShowtimeService();

//        // Create prerequisite movie & room
//        Movie m = new Movie("USA","English","MOCK",90,"Dir","Drama",15,new java.math.BigDecimal("5.00"),LocalDate.now(),"Desc","url");
//        m = movieService.createMovie(m);
//        Room r = new Room("Room D", 25, 5);
//        r = roomService.createRoom(r);
//
//        // 1. Create showtime
//        System.out.println("== Create Showtime ==");
//        Showtime s = new Showtime(LocalDate.now(), LocalTime.of(16,30), LocalTime.of(16,0), movieService.getMovieById(UUID.fromString("37a7b15c-f473-4a5e-987b-eb102f06bc97")), roomService.getRoomById(9));
//        Showtime created = showtimeService.createShowtime(s);
//        System.out.println("Created: " + created);
//
//        // 2. Get by ID
//        System.out.println("\n== Get Showtime by ID ==");
//        Showtime fetched = showtimeService.getShowtimeById(UUID.fromString("a11e711d-3338-4987-93b2-a99f3fb57b5e"));
//        System.out.println("Fetched: " + fetched);
//
//        // 3. List all
//        System.out.println("\n== List All Showtimes ==");
//        List<Showtime> all = showtimeService.getAllShowtimes();
//        all.forEach(System.out::println);
//
//        // 4. Update
//        System.out.println("\n== Update Showtime ==");
//        fetched.setStartTime(LocalTime.of(15,0));
//        boolean updateResult = showtimeService.updateShowtime(fetched);
//        System.out.println("Update result: " + updateResult);
//        System.out.println("After update: " + showtimeService.getShowtimeById(fetched.getId()));
//
//        // 5. Delete
//        System.out.println("\n== Delete Showtime ==");
//        boolean deleteResult = showtimeService.deleteShowtime(fetched.getId());
//        System.out.println("Delete result: " + deleteResult);
        // Get All Seat and Booked Seat
        var AllSeat = showtimeService.getSeatOfShowtime("78c5b4bb-097d-4d56-bf67-626dfbe059ad");
        for(String s : AllSeat.keySet()) {
        	List<Seat> seats = AllSeat.get(s);
        	System.out.println("Key:"+s);
        	for(Seat seat : seats) {
        		System.out.println(seat);
        	}
        }
    }
}