public class Room {

    // fields 
    private String  roomNumber;
    private String  type;          // Standard | Deluxe | Suite | Presidential
    private double  pricePerNight;
    private boolean available;

    // room types offered by Martinez Royal Suite
    public static final String[] TYPES = {
        "Standard", "Deluxe", "Suite", "Presidential"
    };

    // constructor 
    public Room(String roomNumber, String type, double pricePerNight, boolean available) {
        this.roomNumber    = roomNumber;
        this.type          = type;
        this.pricePerNight = pricePerNight;
        this.available     = available;
    }

    // getters 
    public String  getRoomNumber()    { return roomNumber;    }
    public String  getType()          { return type;          }
    public double  getPricePerNight() { return pricePerNight; }
    public boolean isAvailable()      { return available;     }

    // setters 
    public void setType(String type)             { this.type          = type;     }
    public void setPricePerNight(double price)   { this.pricePerNight = price;    }
    public void setAvailable(boolean available)  { this.available     = available; }

    // Converts this Room to a pipe-separated line for the text file
    // Format: roomNumber|type|pricePerNight|available
    // Example: 101|Deluxe|4500.0|true

    public String toFileString() {
        return roomNumber + "|" + type + "|" + pricePerNight + "|" + available;
    }

    // creates a Room object by reading a pipe-separated file line
    public static Room fromFileString(String line) {
        String[] p = line.split("\\|");
        return new Room(p[0], p[1], Double.parseDouble(p[2]), Boolean.parseBoolean(p[3]));
    }

    // Used in JComboBox dropdowns
    @Override
    public String toString() {
        return "Room " + roomNumber + " — " + type
               + "  (₱" + String.format("%,.2f", pricePerNight) + "/night)";
    }
}
