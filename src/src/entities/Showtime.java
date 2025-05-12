package entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Showtime {
    private UUID id;
    private LocalDate dateShow;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID movie_id;
    private Long room_id;
    private Movie movie;
    private Room room;
    private List<Booking> bookings;

    public Showtime() {
        this.bookings = new ArrayList<>();
    }
    
    public Showtime(LocalDate dateShow, LocalTime startTime, LocalTime endTime,
            Movie movie, Room theater) {
		this();
		this.id = UUID.randomUUID();
		this.dateShow = dateShow;
		this.startTime = startTime;
		this.endTime = endTime;
		this.movie = movie;
		this.room = theater;
	}
    public Showtime(UUID id, LocalDate dateShow, LocalTime startTime, LocalTime endTime,
                     Movie movie, Room theater) {
        this();
        this.id = id;
        this.dateShow = dateShow;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movie = movie;
        this.room = theater;
    }

    // Getters & Setters
    public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }


    public LocalDate getDateShow() { return dateShow; }
    public void setDateShow(LocalDate dateShow) { this.dateShow = dateShow; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public UUID getMovie_id() {
		return movie_id;
	}

	public void setMovie_id(UUID movie_id) {
		this.movie_id = movie_id;
	}

	public Long getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}

	public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public void addBooking(Booking b) { this.bookings.add(b); }
    
    @Override
    public String toString() { return "Showtime:{"+id+",movie:"+movie.getTitle()+"\ndateshow:"+dateShow.toString()+"\ntime:"+startTime.toString()+"}"; }
}
