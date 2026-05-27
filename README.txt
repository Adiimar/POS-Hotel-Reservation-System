╔══════════════════════════════════════════════════════════════════╗
║          ♛  MARTINEZ ROYAL SUITE                                 ║
║             Hotel Management System  —  v1.0                     ║
╚══════════════════════════════════════════════════════════════════╝

─────────────────────────────────────────────────────────────────
  REQUIREMENTS
─────────────────────────────────────────────────────────────────
  • Java Development Kit (JDK) 8 or higher
  • Any OS: Windows, macOS, Linux

  Check your Java version:
    java -version

─────────────────────────────────────────────────────────────────
  FOLDER STRUCTURE
─────────────────────────────────────────────────────────────────
  MartinezRoyalSuite/
  ├── src/                  ← All Java source files
  │   ├── Main.java         ← Entry point (run this)
  │   ├── Theme.java        ← Colors, fonts, symbols
  │   ├── RoyalComponents.java  ← Reusable styled widgets
  │   ├── FileHandler.java  ← File I/O + in-memory data
  │   ├── Room.java         ← Room model
  │   ├── Customer.java     ← Customer model
  │   ├── Reservation.java  ← Reservation model
  │   ├── LoginScreen.java  ← Login / Welcome screen
  │   ├── MainFrame.java    ← Main window + sidebar nav
  │   ├── DashboardPanel.java   ← Dashboard module
  │   ├── RoomPanel.java        ← Room Management module
  │   ├── CustomerPanel.java    ← Customer Management module
  │   ├── ReservationPanel.java ← Reservation/Booking module
  │   └── BillingPanel.java     ← Billing & Checkout module
  └── data/                 ← Text file storage (auto-created)
      ├── rooms.txt
      ├── customers.txt
      └── reservations.txt

─────────────────────────────────────────────────────────────────
  HOW TO COMPILE & RUN
─────────────────────────────────────────────────────────────────

  STEP 1 — Open a terminal / command prompt

  STEP 2 — Navigate into the src/ folder
    cd path/to/MartinezRoyalSuite/src

  STEP 3 — Compile all Java files
    javac *.java

  STEP 4 — Run the program
    java Main

  ✔  The data/ folder is created automatically on first run.
  ✔  The program must be run from inside the src/ folder so
     it can find/create the data/ directory correctly.

─────────────────────────────────────────────────────────────────
  RUNNING IN AN IDE (IntelliJ / Eclipse / NetBeans)
─────────────────────────────────────────────────────────────────

  IntelliJ IDEA:
    1. File → Open → select the MartinezRoyalSuite/ folder
    2. Right-click src/ → Mark Directory as → Sources Root
    3. Right-click Main.java → Run 'Main.main()'
    4. Set the Working Directory to .../MartinezRoyalSuite/src
       (Run → Edit Configurations → Working Directory)

  Eclipse:
    1. File → New → Java Project
    2. Uncheck "Use default location", browse to MartinezRoyalSuite/
    3. Add src/ as the source folder
    4. Run Main.java as a Java Application

─────────────────────────────────────────────────────────────────
  LOGIN CREDENTIALS
─────────────────────────────────────────────────────────────────
  Username : admin
  Password : admin123

─────────────────────────────────────────────────────────────────
  DATA FILE FORMAT
─────────────────────────────────────────────────────────────────

  rooms.txt       (pipe-separated)
    roomNumber|type|pricePerNight|available
    Example:  101|Deluxe|4500.0|true

  customers.txt
    customerId|name|contact|email|idType|idNumber
    Example:  C001|Juan dela Cruz|09171234567|juan@email.com|Passport|P123456

  reservations.txt
    reservationId|customerId|roomNumber|checkIn|checkOut|totalCost|status
    Example:  R0001|C001|101|2025-06-01|2025-06-04|13500.0|Active

  Status values: Active | Checked Out | Cancelled

─────────────────────────────────────────────────────────────────
  SYSTEM MODULES
─────────────────────────────────────────────────────────────────
  ♛ Dashboard       — Live stats: rooms, guests, revenue
  🛏 Room Mgmt.     — Add/Edit/Delete rooms, view availability
  👤 Customers      — Register guests, edit & delete records
  📅 Reservations   — Book rooms, auto-compute cost, cancel
  💰 Billing        — Load bill, checkout, print receipt

─────────────────────────────────────────────────────────────────
  TYPICAL WORKFLOW
─────────────────────────────────────────────────────────────────
  1. Add a Room        → Room Management → fill form → Add Room
  2. Add a Customer    → Customers → fill form → Add Guest
  3. Make a Booking    → Reservations → pick guest & room,
                         enter dates → Book Now
  4. Checkout & Bill   → Billing → select reservation →
                         Load Bill → Checkout → Print Bill

─────────────────────────────────────────────────────────────────
  TROUBLESHOOTING
─────────────────────────────────────────────────────────────────
  • "cannot find symbol" error  → Make sure you compiled ALL
    *.java files together:  javac *.java

  • Data not saving  → Run the program from inside src/ so the
    data/ folder is created in the right place.

  • Blank tables on startup  → Normal if data files are empty.
    Add some rooms and customers first.

  • Crown symbol (♛) shows as a box  → Your terminal font may
    not support Unicode; the GUI itself will display it fine.
