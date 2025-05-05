package service;

import dao.BookingDAO;
import dao.MovieDAO;
import entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class BookingService {
	private UserService userService;
	private ShowtimeService showtimeService;
	
    public BookingService() {
    	userService = new UserService();
    	showtimeService = new ShowtimeService();
    }
	public Booking createBooking(Booking booking) {
		
    	User user = userService.getUserById(booking.getUser_id());
    	if(user == null) throw new RuntimeException("User not found");
    	Showtime showtime = showtimeService.getShowtimeById(booking.getShowtime_id());
    	if(showtime == null) throw new RuntimeException("Showtime not found");
    	booking.setBookingPrice(showtime.getMovie().getPrice());
    	List<Booking> bookeds = showtime.getBookings();
    	List<Seat> seats = new ArrayList<>();
    	for(Booking booked : bookeds) {
    		seats.addAll(booked.getSeats());
    	}
    	for(Seat seat : seats) {
    		for(Seat s : booking.getSeats()) {
    			if(s.getId() == seat.getId()) {
    				throw new RuntimeException("Seat has been booked for this showtime");
    			}
    		}
    	}
    	
    	return BookingDAO.insert(booking) > 0 ? booking : null;
    }

    public Booking getBookingById(UUID id) {
        return BookingDAO.findById(id);
    }

    public List<Booking> getAllBookings() {
        return BookingDAO.findAll();
    }

    public boolean updateBooking(Booking booking) {
        return BookingDAO.update(booking) > 0;
    }

    public boolean deleteBooking(UUID id) {
        return BookingDAO.delete(id) > 0;
    }

	public BigDecimal getRevenueByDate(LocalDate date) {
    	if(date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Date is invalid");
        return BookingDAO.getRevenueByDate(date);
    }

	public Map<LocalDate, BigDecimal> getDailyRevenueByMonth(int year, int month) {
		// Kiểm tra tháng không phải trong tương lai
		LocalDate firstOfMonth = LocalDate.of(year, month, 1);
		if (LocalDate.now().isBefore(firstOfMonth)) {
			throw new IllegalArgumentException("Month is invalid");
		}
		// Lấy map chưa sắp xếp từ DAO
		Map<LocalDate, BigDecimal> unsorted = BookingDAO.getDailyRevenueByMonth(year, month);
		// Đưa vào TreeMap để tự động sắp xếp theo key (LocalDate) tăng dần
		return new TreeMap<>(unsorted);
	}
	
	public BigDecimal getTotalRevenueByMonth(int year, int month) {
		Map<LocalDate, BigDecimal> dailyRevenue = getDailyRevenueByMonth(year, month);
		BigDecimal totalRevenue = BigDecimal.ZERO;
		for (BigDecimal revenue : dailyRevenue.values()) {
			totalRevenue = totalRevenue.add(revenue);
		}
		return totalRevenue;
	}

	/**
	 * Find bookings by user.
	 */
	public List<Booking> getBookingsByUser(UUID userId) {
		if(userId == null) throw new IllegalArgumentException("User ID is invalid");
		if(userService.getUserById(userId) == null) throw new RuntimeException("User not found");

		return BookingDAO.findByUserId(userId);
	}

	/**
	 * Find bookings by date.
	 */
	public List<Booking> getBookingsByDate(LocalDate date) {
		if(date == null || date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Date is invalid");
		return BookingDAO.findByDate(date);
	}

	/**
	 * Find bookings by movie.
	 */
	public List<Booking> getBookingsByMovie(UUID movieId) {
		if(movieId == null) throw new IllegalArgumentException("Movie ID is invalid");
		if(MovieDAO.findById(movieId) == null) throw new RuntimeException("Movie not found");
		return BookingDAO.findByMovieId(movieId);
	}

}