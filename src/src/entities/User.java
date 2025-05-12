package entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private LocalDate birthday;
    private List<Booking> bookings;

    public User() {
        this.bookings = new ArrayList<>();
    }
    
    public User(UUID id, String username, String password, String email,
                String firstname, String lastname, LocalDate birthday) {
        this();
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }

    public User(String username, String password, String email,
            String firstname, String lastname, LocalDate birthday) {
	    this();
	    this.id = UUID.randomUUID();
	    this.username = username;
	    this.password = password;
	    this.email = email;
	    this.firstname = firstname;
	    this.lastname = lastname;
	    this.birthday = birthday;
    }
    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

	public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public void addBooking(Booking b) { this.bookings.add(b); }
    
    @Override
    public String toString() {
        return "User{" + id + ":" + username + '}';
    }	
}
