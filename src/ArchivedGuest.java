import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArchivedGuest {

    // fields 
    private String archiveId;        // e.g. "A0001"
    private String customerId;       // original customer ID before removal
    private String customerName;
    private String customerContact;  // phone / email / whatever customer stores
    private String reservationId;
    private String roomNumber;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private long   nights;
    private double totalCost;
    private String archivedOn;       // timestamp of checkout event

    // constructor 

    public ArchivedGuest(String archiveId,
                         String customerId,
                         String customerName,
                         String customerContact,
                         String reservationId,
                         String roomNumber,
                         String roomType,
                         String checkInDate,
                         String checkOutDate,
                         long   nights,
                         double totalCost,
                         String archivedOn) {
        this.archiveId       = archiveId;
        this.customerId      = customerId;
        this.customerName    = customerName;
        this.customerContact = customerContact;
        this.reservationId   = reservationId;
        this.roomNumber      = roomNumber;
        this.roomType        = roomType;
        this.checkInDate     = checkInDate;
        this.checkOutDate    = checkOutDate;
        this.nights          = nights;
        this.totalCost       = totalCost;
        this.archivedOn      = archivedOn;
    }

    // convenience factory

    public static ArchivedGuest fromCheckout(String archiveId,
                                              Customer    cust,
                                              Reservation res,
                                              Room        room) {
        String name    = (cust != null) ? cust.getName()    : "Unknown";
        String contact = (cust != null) ? cust.getContact() : "—";
        String type    = (room != null) ? room.getType()    : "Unknown";
        String ts      = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return new ArchivedGuest(
            archiveId,
            res.getCustomerId(),
            name,
            contact,
            res.getReservationId(),
            res.getRoomNumber(),
            type,
            res.getCheckInDate(),
            res.getCheckOutDate(),
            res.getNights(),
            res.getTotalCost(),
            ts
        );
    }

    // serialization 

    public String toFileString() {
        return String.join("|",
            archiveId,
            customerId,
            customerName,
            customerContact,
            reservationId,
            roomNumber,
            roomType,
            checkInDate,
            checkOutDate,
            String.valueOf(nights),
            String.valueOf(totalCost),
            archivedOn
        );
    }

    // Parse one line from archived_guests.txt 
    public static ArchivedGuest fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 12) throw new IllegalArgumentException("Bad archive line: " + line);
        return new ArchivedGuest(
            p[0], p[1], p[2], p[3], p[4], p[5], p[6],
            p[7], p[8],
            Integer.parseInt(p[9]),
            Double.parseDouble(p[10]),
            p[11]
        );
    }

    @Override
    public String toString() {
        return archiveId + " — " + customerName + " | " + checkOutDate;
    }

    // getters 

    public String getArchiveId()       { return archiveId; }
    public String getCustomerId()      { return customerId; }
    public String getCustomerName()    { return customerName; }
    public String getCustomerContact() { return customerContact; }
    public String getReservationId()   { return reservationId; }
    public String getRoomNumber()      { return roomNumber; }
    public String getRoomType()        { return roomType; }
    public String getCheckInDate()     { return checkInDate; }
    public String getCheckOutDate()    { return checkOutDate; }
    public long   getNights()          { return nights; }
    public double getTotalCost()       { return totalCost; }
    public String getArchivedOn()      { return archivedOn; }
}