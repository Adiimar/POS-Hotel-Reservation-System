import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ReservationPanel extends JPanel {

    private final MainFrame frame;

    // form widgets 
    private JComboBox<Customer> cbCustomer;
    private JComboBox<Room>     cbRoom;
    private JTextField          fldCheckIn, fldCheckOut;
    private JLabel              lblNights, lblTotal;
    private JButton             btnBook, btnCancel, btnClear;

    // table 
    private DefaultTableModel tableModel;
    private JTable            table;
    private int               selectedRow = -1;

    // ₱ peso sign 
    private static final Font PESO_FONT = new Font("Arial Unicode MS", Font.BOLD, 15);

    public ReservationPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    // layout

    private void buildUI() {
        add(RoyalComponents.makeSectionHeader("\uD83D\uDCC5", "Reservations & Bookings"),
                BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        body.add(buildBookingForm(),  BorderLayout.NORTH);
        body.add(buildTableSection(), BorderLayout.CENTER);
        return body;
    }

    // booking form card 

    private JPanel buildBookingForm() {
        JPanel card = RoyalComponents.makeCard();
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("New Booking");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_GOLD);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_GOLD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        cbCustomer = new JComboBox<>();
        cbCustomer.setFont(Theme.FONT_TEXT);
        cbCustomer.setBackground(Theme.BG_INPUT);
        cbCustomer.setForeground(Theme.TEXT_CREAM);

        cbRoom = new JComboBox<>();
        cbRoom.setFont(Theme.FONT_TEXT);
        cbRoom.setBackground(Theme.BG_INPUT);
        cbRoom.setForeground(Theme.TEXT_CREAM);

        fldCheckIn  = RoyalComponents.makeField(14);
        fldCheckOut = RoyalComponents.makeField(14);
        fldCheckIn.setToolTipText("Format: yyyy-MM-dd  (e.g. 2025-06-15)");
        fldCheckOut.setToolTipText("Format: yyyy-MM-dd  (e.g. 2025-06-18)");

        lblNights = computedLabel("—");
        lblTotal  = computedLabel("—");
        lblTotal.setFont(PESO_FONT);

        DocumentListener recalc = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { recalcCost(); }
            @Override public void removeUpdate(DocumentEvent e)  { recalcCost(); }
            @Override public void changedUpdate(DocumentEvent e) { recalcCost(); }
        };
        fldCheckIn.getDocument().addDocumentListener(recalc);
        fldCheckOut.getDocument().addDocumentListener(recalc);
        cbRoom.addActionListener(e -> recalcCost());

        JPanel row1 = fieldGroup("Guest *", cbCustomer, "Room *", cbRoom);
        JPanel row2 = fieldGroup("Check-In * (yyyy-MM-dd)", fldCheckIn,
                                  "Check-Out * (yyyy-MM-dd)", fldCheckOut);
        JPanel row3 = infoGroup("Nights", lblNights, "Total Cost", lblTotal);

        btnBook   = RoyalComponents.makeGoldBtn("Book Now");
        btnCancel = RoyalComponents.makeGoldBtn("Cancel Reservation");
        btnClear  = RoyalComponents.makeGoldBtn("Clear Form");
        btnBook.addActionListener(e   -> bookReservation());
        btnCancel.addActionListener(e -> cancelReservation());
        btnClear.addActionListener(e  -> clearForm());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(btnBook); btnRow.add(btnCancel); btnRow.add(btnClear);

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(row1);
        card.add(Box.createVerticalStrut(10));
        card.add(row2);
        card.add(Box.createVerticalStrut(10));
        card.add(row3);
        card.add(Box.createVerticalStrut(16));
        card.add(btnRow);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        wrapper.add(card);
        return wrapper;
    }

    // table section 

    private JPanel buildTableSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("All Reservations  —  Click a row to select for cancel");
        lbl.setFont(Theme.FONT_LABEL);
        lbl.setForeground(Theme.TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] cols = {"Res. ID", "Customer", "Room", "Check-In", "Check-Out",
                         "Nights", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        table = RoyalComponents.makeTable(tableModel, true);

        int[] widths = {68, 140, 65, 100, 100, 60, 100, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectedRow = table.getSelectedRow();
        });

        p.add(lbl, BorderLayout.NORTH);
        p.add(RoyalComponents.makeScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // form helpers 

    private JPanel fieldGroup(String l1, JComponent f1, String l2, JComponent f2) {
        JPanel p = new JPanel(new GridLayout(2, 2, 14, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(RoyalComponents.makeLabel(l1));
        p.add(RoyalComponents.makeLabel(l2));
        p.add(f1); p.add(f2);
        return p;
    }

    private JPanel infoGroup(String l1, JLabel v1, String l2, JLabel v2) {
        JPanel p = new JPanel(new GridLayout(2, 2, 14, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(RoyalComponents.makeLabel(l1));
        p.add(RoyalComponents.makeLabel(l2));
        p.add(v1); p.add(v2);
        return p;
    }

    private JLabel computedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Georgia", Font.BOLD, 15));
        l.setForeground(Theme.GOLD);
        return l;
    }

    // cost calculation 

    private void recalcCost() {
        String in  = fldCheckIn.getText().trim();
        String out = fldCheckOut.getText().trim();
        Room room  = (Room) cbRoom.getSelectedItem();

        if (in.length() < 10 || out.length() < 10 || room == null) {
            lblNights.setText("—"); lblTotal.setText("—"); return;
        }
        long nights = Reservation.calculateNights(in, out);
        if (nights <= 0) {
            lblNights.setText("Invalid dates"); lblTotal.setText("—"); return;
        }
        lblNights.setText(nights + (nights == 1 ? " night" : " nights"));
        lblTotal.setText(String.format("\u20B1%,.2f", nights * room.getPricePerNight()));
    }

    // booking operations 

    private void bookReservation() {
        Customer cust = (Customer) cbCustomer.getSelectedItem();
        Room     room = (Room)     cbRoom.getSelectedItem();

        if (cust == null) { RoyalComponents.showError(this, "Please select a guest."); return; }
        if (room == null) { RoyalComponents.showError(this, "Please select a room.");  return; }

        String in  = fldCheckIn.getText().trim();
        String out = fldCheckOut.getText().trim();

        if (in.isEmpty() || out.isEmpty()) {
            RoyalComponents.showError(this, "Please enter both check-in and check-out dates.");
            return;
        }
        long nights = Reservation.calculateNights(in, out);
        if (nights <= 0) {
            RoyalComponents.showError(this,
                    "Check-out date must be after check-in date.\nFormat: yyyy-MM-dd");
            return;
        }
        if (FileHandler.isDoubleBooked(room.getRoomNumber(), in, out, null)) {
            RoyalComponents.showError(this,
                    "Room " + room.getRoomNumber()
                    + " is already booked for those dates.\n"
                    + "Please choose different dates or a different room.");
            return;
        }

        double total = nights * room.getPricePerNight();
        String id    = FileHandler.generateReservationId();

        FileHandler.reservations.add(new Reservation(
            id, cust.getCustomerId(), room.getRoomNumber(),
            in, out, total, Reservation.STATUS_ACTIVE));

        Room roomToUpdate = FileHandler.findRoom(room.getRoomNumber());
        if (roomToUpdate != null) roomToUpdate.setAvailable(false);

        FileHandler.saveReservations();
        FileHandler.saveRooms();
        frame.refreshRooms();
        refresh();
        clearForm();

        RoyalComponents.showInfo(this,
                "Booking confirmed!\n"
                + "Reservation ID : " + id             + "\n"
                + "Guest          : " + cust.getName() + "\n"
                + "Room           : " + room.getRoomNumber() + "\n"
                + "Nights         : " + nights          + "\n"
                + "Total Cost     : " + String.format("\u20B1%,.2f", total));
    }

    private void cancelReservation() {
        if (selectedRow < 0) {
            RoyalComponents.showError(this,
                    "Select a reservation from the table to cancel.");
            return;
        }
        String resId  = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if (!status.equals(Reservation.STATUS_ACTIVE)) {
            RoyalComponents.showError(this, "Only Active reservations can be cancelled.");
            return;
        }
        if (!RoyalComponents.confirm(this, "Cancel reservation " + resId + "?")) return;

        for (Reservation r : FileHandler.reservations) {
            if (r.getReservationId().equals(resId)) {
                r.setStatus(Reservation.STATUS_CANCELLED);
                Room room = FileHandler.findRoom(r.getRoomNumber());
                if (room != null) room.setAvailable(true);
                break;
            }
        }
        FileHandler.saveReservations();
        FileHandler.saveRooms();
        frame.refreshRooms();
        refresh();
        selectedRow = -1;
        RoyalComponents.showInfo(this, "Reservation " + resId + " has been cancelled.");
    }

    // refresh 

    private void clearForm() {
        fldCheckIn.setText("");
        fldCheckOut.setText("");
        lblNights.setText("—");
        lblTotal.setText("—");
        if (cbCustomer.getItemCount() > 0) cbCustomer.setSelectedIndex(0);
        if (cbRoom.getItemCount()     > 0) cbRoom.setSelectedIndex(0);
        table.clearSelection();
        selectedRow = -1;
    }

    public void refresh() {
        cbCustomer.removeAllItems();
        for (Customer c : FileHandler.customers) cbCustomer.addItem(c);

        cbRoom.removeAllItems();
        for (Room r : FileHandler.rooms) cbRoom.addItem(r);

        tableModel.setRowCount(0);
        for (Reservation r : FileHandler.reservations) {
            tableModel.addRow(new Object[]{
                r.getReservationId(),
                FileHandler.getCustomerName(r.getCustomerId()),
                "Room " + r.getRoomNumber(),
                r.getCheckInDate(),
                r.getCheckOutDate(),
                r.getNights() + "N",
                String.format("\u20B1%,.2f", r.getTotalCost()),
                r.getStatus()
            });
        }
    }
}