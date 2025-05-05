package entities;

public class Seat {
    private String id; // <roomId>-<row>-<column>
    private int row;
    private int column;
    private Long room_id;
    private Room room;
//    private List<Booking> bookings;
    
    public Seat() {
    	this.room = new Room();
    }
    public Seat(int row, int column, Long room_id) {
    	this();
        this.row = row;
        this.column = column;
        this.room_id = room_id;
        this.id = room_id+"."+row+"-"+column;  
    }
    
    public Seat(String id, int row, int column, Long room_id) {
    	this();
        this.id = id;  
        this.row = row;
        this.column = column;
        this.room_id = room_id;
    }

//    public boolean isBookedForScreening(Showtime showtime) {
//        return bookings.stream().anyMatch(booking -> booking.getShowtime().equals(showtime));
//    }
//    
    public Long getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Long room_id) {
		this.room_id = room_id;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }
    
    @Override
    public String toString() { return "Seat{"+id+"}";}    

}
