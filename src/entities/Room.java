package entities;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private Long id;
    private String name;
    private int totalSeats;
    private int seatsPerRow;
    private int seatsAvailable;
    private List<Showtime> showtimes;
    private List<Seat> seats;

    public Room() {
        this.showtimes = new ArrayList<>();
        this.seats = new ArrayList<>();
    }

    public Room(String name, int totalSeats, int seatsPerRow) {
        this();
        this.name = name;
        this.totalSeats = totalSeats;
        this.seatsPerRow = seatsPerRow;
        this.seatsAvailable = totalSeats;
    }
    public Room(Long id, String name, int totalSeats, int seatsPerRow) {
        this();
        this.id = id;
        this.name = name;
        this.totalSeats = totalSeats;
        this.seatsPerRow = seatsPerRow;
        this.seatsAvailable = totalSeats;
    }
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }

    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }

    public List<Showtime> getShowtimes() { return showtimes; }
    public void setShowtimes(List<Showtime> showtimes) { this.showtimes = showtimes; }
    public void addShowtime(Showtime s) { this.showtimes.add(s); }
    
    
	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}

	@Override
    public String toString() { return name; }
    
    

}
