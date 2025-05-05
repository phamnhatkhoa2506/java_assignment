package test;

import service.*;
import entities.User;
import entities.Movie;
import entities.Room;
import entities.Showtime;
import entities.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class BookingServiceTest {
    public static void main(String[] args) {
        UserService userService = new UserService();
        MovieService movieService = new MovieService();
        RoomService roomService = new RoomService();
        ShowtimeService showtimeService = new ShowtimeService();
        BookingService bookingService = new BookingService();

        // Prerequisites: user, movie, room, showtime
        User u = new User(UUID.randomUUID(),"hiioji","bkpass","bk019@example.com","Bk","User",LocalDate.of(1992,2,2));
        u = userService.register(u);
        Movie m = new Movie("USAC","English","Booking Movie",100,"Dir","Comedy",12,new java.math.BigDecimal("7.00"),LocalDate.now(),"Desc","url");
        m = movieService.createMovie(m);
        Room r = new Room("Room F", 30, 6);
        r = roomService.createRoom(r);
        Showtime s = new Showtime(LocalDate.now(), LocalTime.of(10,0), LocalTime.of(12,0), m, r);
        s = showtimeService.createShowtime(s);
//        private UUID id;
//        private LocalDate bookingDate;
//        private LocalTime bookingTime;
//        private BigDecimal bookingPrice;
//        private UUID user_id;
//        private UUID showtime_id;
//        private User user;
//        private List<String> seats_ids;
//        private List<Seat> seats = new ArrayList<>();
        
        Booking booking = new Booking();
        booking.setBookingDate(LocalDate.now());
        booking.setBookingPrice(s.getMovie().getPrice());
        booking.setBookingTime(LocalTime.of(12, 0));
        booking.addSeat(r.getSeats().getFirst());
        booking.getSeats_ids().add(r.getSeats().getFirst().getId());
        booking.setShowtime(s);
        booking.setShowtime_id(s.getId());
        booking.setUser(u);
        booking.setUser_id(u.getId());
        
        bookingService.createBooking(booking);
    }
}
