import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Reservation {

    // status constants
    public static final String STATUS_ACTIVE      = "Active";
    public static final String STATUS_CHECKED_OUT = "Checked Out";
    public static final String STATUS_CANCELLED   = "Cancelled";

    // date format used throughout the system: yyyy-MM-dd
    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // fields
    private String reservationId;
    private String customerId;
    private String roomNumber;
    private String checkInDate;    // yyyy-MM-dd
    private String checkOutDate;   
    private double totalCost;
    private String status;

    // constructor 
    public Reservation(String reservationId, String customerId,
                       String roomNumber, String checkInDate,
                       String checkOutDate, double totalCost, String status) {
        this.reservationId = reservationId;
        this.customerId    = customerId;
        this.roomNumber    = roomNumber;
        this.checkInDate   = checkInDate;
        this.checkOutDate  = checkOutDate;
        this.totalCost     = totalCost;
        this.status        = status;
    }

    // getters
    public String getReservationId() { return reservationId; }
    public String getCustomerId()    { return customerId;    }
    public String getRoomNumber()    { return roomNumber;    }
    public String getCheckInDate()   { return checkInDate;  }
    public String getCheckOutDate()  { return checkOutDate; }
    public double getTotalCost()     { return totalCost;    }
    public String getStatus()        { return status;       }

    // setters 
    public void setStatus(String status)        { this.status      = status;    }
    public void setTotalCost(double cost)       { this.totalCost   = cost;      }
    public void setCheckInDate(String date)     { this.checkInDate = date;      }
    public void setCheckOutDate(String date)    { this.checkOutDate = date;     }

    // calculates how many nights are in this booking (checkout - checkin)
    public long getNights() {
        try {
            LocalDate in  = LocalDate.parse(checkInDate,  DATE_FMT);
            LocalDate out = LocalDate.parse(checkOutDate, DATE_FMT);
            return Math.max(0, ChronoUnit.DAYS.between(in, out));
        } catch (Exception e) {
            return 0;
        }
    }

    // static version: returns -1 if dates are invalid or reversed
    public static long calculateNights(String checkIn, String checkOut) {
        try {
            LocalDate in  = LocalDate.parse(checkIn,  DATE_FMT);
            LocalDate out = LocalDate.parse(checkOut, DATE_FMT);
            long nights   = ChronoUnit.DAYS.between(in, out);
            return nights > 0 ? nights : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    // saves to file as: id|custId|room|checkIn|checkOut|cost|status
    public String toFileString() {
        return reservationId + "|" + customerId  + "|" + roomNumber + "|"
               + checkInDate + "|" + checkOutDate + "|" + totalCost + "|" + status;
    }

    // loads from a pipe-separated file line
    public static Reservation fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        return new Reservation(p[0], p[1], p[2], p[3], p[4],
                               Double.parseDouble(p[5]), p[6]);
    }

    // shown in the Billing combo box
    @Override
    public String toString() {
        return reservationId + "  |  Room " + roomNumber
               + "  [" + checkInDate + " → " + checkOutDate + "]";
    }
}
