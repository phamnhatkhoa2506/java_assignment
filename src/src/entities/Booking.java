package entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * Booking entity, representing a booking with associated seats.
 */
public class Booking {
    private UUID id;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private BigDecimal bookingPrice;
    private UUID user_id;
    private UUID showtime_id;
    private User user;
    private Showtime showtime;
    private List<String> seats_ids = new ArrayList<>();
    private List<Seat> seats = new ArrayList<>();

    public Booking() {
    	this.id = UUID.randomUUID(); 
    }

    public Booking(UUID id, LocalDate bookingDate, LocalTime bookingTime,
                   BigDecimal bookingPrice, User user, Showtime screening, List<Seat> seats) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingPrice = bookingPrice;
        this.user = user;
        this.showtime = screening;
        this.seats = seats != null ? seats : new ArrayList<>();
    }

    public Booking(LocalDate bookingDate, LocalTime bookingTime,
                   BigDecimal bookingPrice, User user, Showtime screening) {
        this(UUID.randomUUID(), bookingDate, bookingTime, bookingPrice, user, screening, null);
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalTime bookingTime) { this.bookingTime = bookingTime; }

    public BigDecimal getBookingPrice() { return bookingPrice; }
    public void setBookingPrice(BigDecimal bookingPrice) { this.bookingPrice = bookingPrice; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
    public void addSeat(Seat seat) { this.seats.add(seat); }
    public void removeSeat(Seat seat) { this.seats.remove(seat); }

    public UUID getUser_id() {
		return user_id;
	}

	public void setUser_id(UUID user_id) {
		this.user_id = user_id;
	}

	
	public UUID getShowtime_id() {
		return showtime_id;
	}

	public void setShowtime_id(UUID showtime_id) {
		this.showtime_id = showtime_id;
	}

	public Showtime getShowtime() {
		return showtime;
	}

	public void setShowtime(Showtime showtime) {
		this.showtime = showtime;
	}

	public List<String> getSeats_ids() {
		return seats_ids;
	}

	public void setSeats_ids(List<String> seats_ids) {
		this.seats_ids = seats_ids;
	}

	@Override
    public String toString() {
        return "Booking{" +
               "id=" + id +
               ", bookingDate=" + bookingDate +
               ", bookingTime=" + bookingTime +
               ", bookingPrice=" + bookingPrice +
               ", user=" + user +
               ", screening=" + showtime +
               ", seats=" + seats +
               '}';
    }
}
