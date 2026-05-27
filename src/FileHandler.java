import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // file paths
    private static final String DATA_DIR              = "data" + File.separator;
    private static final String ROOMS_FILE            = DATA_DIR + "rooms.txt";
    private static final String CUSTOMERS_FILE        = DATA_DIR + "customers.txt";
    private static final String RESERVATIONS_FILE     = DATA_DIR + "reservations.txt";
    private static final String ARCHIVED_GUESTS_FILE  = DATA_DIR + "archived_guests.txt";

    // shared in-memory lists 
    public static List<Room>         rooms          = new ArrayList<>();
    public static List<Customer>     customers      = new ArrayList<>();
    public static List<Reservation>  reservations   = new ArrayList<>();
    public static List<ArchivedGuest> archivedGuests = new ArrayList<>();

    // initialization 

    public static void initialize() {
        new File(DATA_DIR).mkdirs();
        loadAll();
    }

    // load 

    public static void loadAll() {
        rooms          = readRooms();
        customers      = readCustomers();
        reservations   = readReservations();
        archivedGuests = readArchivedGuests();
    }

    public static List<Room> readRooms() {
        List<Room> list = new ArrayList<>();
        for (String line : readLines(ROOMS_FILE)) {
            try { list.add(Room.fromFileString(line)); }
            catch (Exception e) { System.err.println("Bad room line: " + line); }
        }
        return list;
    }

    public static List<Customer> readCustomers() {
        List<Customer> list = new ArrayList<>();
        for (String line : readLines(CUSTOMERS_FILE)) {
            try { list.add(Customer.fromFileString(line)); }
            catch (Exception e) { System.err.println("Bad customer line: " + line); }
        }
        return list;
    }

    public static List<Reservation> readReservations() {
        List<Reservation> list = new ArrayList<>();
        for (String line : readLines(RESERVATIONS_FILE)) {
            try { list.add(Reservation.fromFileString(line)); }
            catch (Exception e) { System.err.println("Bad reservation line: " + line); }
        }
        return list;
    }

    public static List<ArchivedGuest> readArchivedGuests() {
        List<ArchivedGuest> list = new ArrayList<>();
        for (String line : readLines(ARCHIVED_GUESTS_FILE)) {
            try { list.add(ArchivedGuest.fromFileString(line)); }
            catch (Exception e) { System.err.println("Bad archive line: " + line); }
        }
        return list;
    }

    private static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading " + path + ": " + e.getMessage());
        }
        return lines;
    }

    // save

    public static void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) pw.println(r.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    public static void saveCustomers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) pw.println(c.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    public static void saveReservations() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            for (Reservation r : reservations) pw.println(r.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    public static void saveArchivedGuests() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVED_GUESTS_FILE))) {
            for (ArchivedGuest a : archivedGuests) pw.println(a.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving archived guests: " + e.getMessage());
        }
    }

    // ── archive guest on checkout ──────────────────────────────────────

    /**
     * @param cust  the Customer object (may be null if already missing)
     * @param res   the Reservation being checked out
     * @param room  the Room object (for type info; may be null)
     */
    public static void archiveGuest(Customer cust, Reservation res, Room room) {
        // 1 ── Build and store the archive record
        String archiveId = generateArchiveId();
        ArchivedGuest entry = ArchivedGuest.fromCheckout(archiveId, cust, res, room);
        archivedGuests.add(entry);
        saveArchivedGuests();

        // 2 ── Remove customer from active list if no other active reservations remain
        if (cust == null) return;  // already removed somehow — nothing to do

        boolean hasOtherActiveRes = reservations.stream()
            .anyMatch(r -> r.getCustomerId().equals(cust.getCustomerId())
                       && !r.getReservationId().equals(res.getReservationId())
                       && r.getStatus().equals(Reservation.STATUS_ACTIVE));

        if (!hasOtherActiveRes) {
            customers.removeIf(c -> c.getCustomerId().equals(cust.getCustomerId()));
            saveCustomers();
        }
    }

    // ID generators 

    public static String generateCustomerId() {
        int max = 0;
        for (Customer c : customers) {
            try {
                int n = Integer.parseInt(c.getCustomerId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }

    public static String generateReservationId() {
        int max = 0;
        for (Reservation r : reservations) {
            try {
                int n = Integer.parseInt(r.getReservationId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("R%04d", max + 1);
    }

    public static String generateArchiveId() {
        int max = 0;
        for (ArchivedGuest a : archivedGuests) {
            try {
                int n = Integer.parseInt(a.getArchiveId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("A%04d", max + 1);
    }

    // lookup helpers

    public static Room findRoom(String roomNumber) {
        for (Room r : rooms)
            if (r.getRoomNumber().equalsIgnoreCase(roomNumber)) return r;
        return null;
    }

    public static Customer findCustomer(String id) {
        for (Customer c : customers)
            if (c.getCustomerId().equals(id)) return c;
        return null;
    }

    public static String getCustomerName(String customerId) {
        Customer c = findCustomer(customerId);
        return (c != null) ? c.getName() : "Unknown";
    }

    // validation

    public static boolean roomExists(String roomNumber) {
        return findRoom(roomNumber) != null;
    }

    public static boolean isDoubleBooked(String roomNumber, String newIn,
                                          String newOut, String excludeId) {
        for (Reservation r : reservations) {
            if (!r.getRoomNumber().equals(roomNumber)) continue;
            if (!r.getStatus().equals(Reservation.STATUS_ACTIVE)) continue;
            if (excludeId != null && r.getReservationId().equals(excludeId)) continue;
            if (newIn.compareTo(r.getCheckOutDate()) < 0 &&
                newOut.compareTo(r.getCheckInDate()) > 0) return true;
        }
        return false;
    }

    // summary stats

    public static int countActiveReservations() {
        int count = 0;
        for (Reservation r : reservations)
            if (r.getStatus().equals(Reservation.STATUS_ACTIVE)) count++;
        return count;
    }

    public static double getTotalRevenue() {
        double total = 0;
        for (Reservation r : reservations)
            if (r.getStatus().equals(Reservation.STATUS_CHECKED_OUT)) total += r.getTotalCost();
        return total;
    }

    public static int countArchivedGuests() {
        return archivedGuests.size();
    }
}