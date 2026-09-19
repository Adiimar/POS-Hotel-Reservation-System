# Hotel Management System

> A desktop-based hotel management system built with Java Swing, featuring a dark navy & gold royal theme.

---

## Overview

This project is a full-featured hotel management application designed for front-desk operations. It manages rooms, guests, reservations, billing, and checkout archives — all through a polished, themed Java Swing interface.

---

## Features

| Module | Description |
|---|---|
| **Dashboard** | Live overview — room counts, active bookings, revenue stats, and recent reservations |
| **Room Management** | Add, update, and delete rooms with type and pricing |
| **Customer Management** | Register and manage guest profiles with government ID tracking |
| **Reservations** | Book rooms, prevent double-booking, calculate costs, and cancel reservations |
| **Billing & Checkout** | Generate bills with tax breakdown, process checkouts, and print summaries |
| **Archived Guests** | View complete checkout history with searchable records and revenue stats |
| **Guest Booking** | Step-by-step guided booking wizard (Browse → Details → Guest Info → Confirm) |

---

## Tech Stack

- **Language:** Java (JDK 8+)
- **UI Framework:** Java Swing (Nimbus Look and Feel base)
- **Storage:** Plain-text flat files (`data/*.txt`, pipe-delimited)
- **Architecture:** MVC-inspired — model classes, panel-based views, centralized `FileHandler`

---

## Project Structure

```
MartinezRoyalSuite/
├── src/
│   ├── Main.java                 # Entry point
│   ├── MainFrame.java            # Main window with sidebar navigation
│   ├── LoginScreen.java          # Login screen with credential validation
│   │
│   ├── model/
│   │   ├── Room.java             # Room entity
│   │   ├── Customer.java         # Customer/guest entity
│   │   ├── Reservation.java      # Reservation entity
│   │   └── ArchivedGuest.java    # Checkout archive record
│   │
│   ├── panels/
│   │   ├── DashboardPanel.java
│   │   ├── RoomPanel.java
│   │   ├── CustomerPanel.java
│   │   ├── ReservationPanel.java
│   │   ├── BillingPanel.java
│   │   ├── ArchivedGuestsPanel.java
│   │   └── GuestBookingPanel.java
│   │
│   ├── FileHandler.java          # All file I/O, ID generation, shared data lists
│   ├── Theme.java                # Global color palette and fonts
│   └── RoyalComponents.java      # Reusable UI component factory
│
└── data/                         # Auto-created on first run
    ├── rooms.txt
    ├── customers.txt
    ├── reservations.txt
    └── archived_guests.txt
```

---

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans) or the terminal

### Run via Terminal

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/martinez-royal-suite.git
cd martinez-royal-suite

# Compile all Java files (from the src/ directory)
javac *.java

# Run the application
java Main
```

### Run via IDE

1. Open the project folder in your IDE
2. Set `Main.java` as the run configuration entry point
3. Hit **Run** — the `data/` folder will be created automatically on first launch

---

## Default Login Credentials

| Username | Password |
|---|---|
| `admin` | `admin123` |
| `Karl Martinez` | `adimar` |
| `Bernabe Cabuhayan` | `serberns` |

> These are hardcoded in `LoginScreen.java`. For production use, replace with a secure authentication mechanism.

---

## Data Storage

All data is stored locally in the `data/` directory as pipe-delimited `.txt` files:

```
# rooms.txt
101|Standard|3500.0|true

# customers.txt
C001|Juan Dela Cruz|09171234567|juan@email.com|Passport|P1234567

# reservations.txt
R0001|C001|101|2025-06-15|2025-06-18|10500.0|Active

# archived_guests.txt
A0001|C001|Juan Dela Cruz|09171234567|R0001|101|Standard|2025-06-15|2025-06-18|3|10500.0|2025-06-18 14:30
```

---

## Design System

The UI uses a consistent **dark navy & gold** royal theme defined in `Theme.java`:

- **Backgrounds:** Deep navy (`#080C34` → `#0C1C4B`)
- **Accents:** Gold (`#D4AF37`) with bright and dim variants
- **Text:** White, cream, and muted lavender
- **Status colors:** Green (Active/Available), Orange (Occupied), Red (Cancelled), Blue (Checked Out)

All reusable components (buttons, fields, tables, cards) are centralized in `RoyalComponents.java`.

---

## Room Types & Pricing

The system supports four room categories (pricing set per room):

- **Standard**
- **Deluxe**
- **Suite**
- **Presidential**

---

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you'd like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

This project is open source and available under the [MIT License](LICENSE).

---

## Authors

Developed as a Java Swing desktop application project.

> *"Curated Comfort. Crowned in Elegance."*
