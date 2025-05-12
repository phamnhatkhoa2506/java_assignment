package service;

import dao.MovieDAO;
import dao.RoomDAO;
import dao.ShowtimeDAO;
import entities.Booking;
import entities.Movie;
import entities.Room;
import entities.Seat;
import entities.Showtime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;

public class ShowtimeService {
	public List<LocalDate> getAllDates() {
		return ShowtimeDAO.getAllDates();
	}

	public List<LocalTime> getAllTimes() {
		return ShowtimeDAO.getAllTimes();
	}

	public List<String> getAllMovieNames() {
		return ShowtimeDAO.getAllMovieNames();
	}
 
    public Showtime createShowtime(Showtime showtime) {
    	LocalDate date = showtime.getDateShow();
    	LocalTime time = showtime.getStartTime();
    	
    	LocalDateTime dateTimeStart = LocalDateTime.of(date, time);
    	
    	LocalDateTime dateTimeEnd = dateTimeStart.plusMinutes(showtime.getMovie().getDuration());
    	
    	List<Showtime> showtimeOfRoom = ShowtimeDAO.findAllByRoomId(showtime.getRoom().getId());
    	for(Showtime shtime : showtimeOfRoom) {
    		LocalDateTime dateTimeStart_other = LocalDateTime.of(shtime.getDateShow(), shtime.getStartTime());
    		LocalDateTime dateTimeEnd_other = dateTimeStart_other.plusMinutes(shtime.getMovie().getDuration());
    		
    		if (dateTimeStart.isBefore(dateTimeEnd_other) && dateTimeEnd.isAfter(dateTimeStart_other)) {
    		    // Có trùng lặp thời gian
    		    throw new RuntimeException("Overlaping in showtime deteced!");
    		}

    	}
    	
        return ShowtimeDAO.insert(showtime) > 0 ? showtime : null;
    }

    public Showtime getShowtimeById(UUID id) {
        return ShowtimeDAO.findById(id);
    }

    public List<Showtime> getAllShowtimes() {
        return ShowtimeDAO.findAll();
    }

    public boolean updateShowtime(Showtime showtime) {
        return ShowtimeDAO.update(showtime) > 0; // or update
    }

    public boolean deleteShowtime(UUID id) {
        return ShowtimeDAO.delete(id) > 0;
    }
    
    private List<Seat> getAllSeat(String showtime_id){
    	List<Seat> seats = new ArrayList<>();
    	Showtime showtime = ShowtimeDAO.findById(UUID.fromString(showtime_id));
    	if(showtime == null) throw new RuntimeException("Showtime not found");
    	Room room = showtime.getRoom();
    	if(room == null) throw new RuntimeException("Showtime's room not found");
    	seats = room.getSeats();
    	if(seats == null) throw new RuntimeException("Room's seats not found");

    	return seats;
    }
    
    private List<Seat> getAllBookedSeat(String showtime_id){
    	List<Seat> bookedSeats = new ArrayList<>();
    	Showtime showtime = ShowtimeDAO.findById(UUID.fromString(showtime_id));
    	if(showtime == null) throw new RuntimeException("Showtime not found");
    	List<Booking> bookings = showtime.getBookings();
    	for(Booking booking : bookings) {
    		List<Seat> seats = 	booking.getSeats();
    		bookedSeats.addAll(seats);
    	}
    	return bookedSeats;
    }
    public Map<String, List<Seat> > getSeatOfShowtime(String showtime_id) {
    	Map<String, List<Seat>> result = new HashMap<>();
    	result.put("All Seat", getAllSeat(showtime_id));
    	result.put("Booked Seats", getAllBookedSeat(showtime_id));
    	return result;
    }

	public List<Showtime> getShowtimeByMovieId(UUID movie_id) {
		Movie movie = MovieDAO.findById(movie_id);
		if(movie == null) throw new RuntimeException("Movie not found");

		List<Showtime> showtimes = ShowtimeDAO.findAllByMovieId(movie_id);
		if(showtimes == null) throw new RuntimeException("Showtime not found");
		return showtimes;

	}

	public List<Showtime> getShowtimeByRoomId(Long room_id) {
		Room room = RoomDAO.findById(room_id);
		if(room == null) throw new RuntimeException("Room not found");

		List<Showtime> showtimes = ShowtimeDAO.findAllByRoomId(room_id);
		if(showtimes == null) throw new RuntimeException("Showtime not found");
		return showtimes;
	}
	
	public List<Showtime> getShowtimeByDate(LocalDate date) {
		if(date == null) throw new RuntimeException("Date not found");
		List<Showtime> showtimes = ShowtimeDAO.findAllByDate(date);
		if(showtimes == null) throw new RuntimeException("Showtime not found");
		return showtimes;
		
	}

	public List<Showtime> getShowtimeByTime(LocalTime time) {
		if(time == null) throw new RuntimeException("Time not found");
		List<Showtime> showtimes = ShowtimeDAO.findAllByTime(time);
		if(showtimes == null) throw new RuntimeException("Showtime not found");
		return showtimes;
		
	}
}