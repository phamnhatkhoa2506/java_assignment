package entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Movie {
    private UUID id;
    private String country;
    private String language;
    private String title;
    private int duration;
    private String directorName;
    private String genre;
    private int forAge;
    private BigDecimal price;
    private LocalDate releaseDate;
    private String description;
    private String posterURL;
    private List<Showtime> showtimes;

    public Movie() {
        this.showtimes = new ArrayList<>();
    }

    public Movie(UUID id, String country, String language, String title, int duration,
                 String directorName, String genre, int forAge,
                 BigDecimal price, LocalDate releaseDate,
                 String description, String posterURL) {
        this();
        this.id = id;
        this.country = country;
        this.language = language;
        this.title = title;
        this.duration = duration;
        this.directorName = directorName;
        this.genre = genre;
        this.forAge = forAge;
        this.price = price;
        this.releaseDate = releaseDate;
        this.description = description;
        this.posterURL = posterURL;
    }
    public Movie(String country, String language, String title, int duration,
            String directorName, String genre, int forAge,
            BigDecimal price, LocalDate releaseDate,
            String description, String posterURL) {
	   this();
	   this.id = UUID.randomUUID();
	   this.country = country;
	   this.language = language;
	   this.title = title;
	   this.duration = duration;
	   this.directorName = directorName;
	   this.genre = genre;
	   this.forAge = forAge;
	   this.price = price;
	   this.releaseDate = releaseDate;
	   this.description = description;
	   this.posterURL = posterURL;
}
    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getDirectorName() { return directorName; }
    public void setDirectorName(String directorName) { this.directorName = directorName; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getForAge() { return forAge; }
    public void setForAge(int forAge) { this.forAge = forAge; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterURL() { return posterURL; }
    public void setPosterURL(String posterURL) { this.posterURL = posterURL; }

    public List<Showtime> getShowtimes() { return showtimes; }
    public void setShowtimes(List<Showtime> screenings) { this.showtimes = screenings; }
    public void addShowtime(Showtime s) { this.showtimes.add(s); }
    
	@Override
    public String toString() { return title; }

}
