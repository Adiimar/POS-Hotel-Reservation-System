import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class BillingPanel extends JPanel {

    private final MainFrame frame;

    // reservation selector 
    private JComboBox<Reservation> cbReservation;

    // bill summary labels 
    private JLabel lblResId, lblGuestName, lblRoomNo, lblRoomType;
    private JLabel lblCheckIn, lblCheckOut, lblNights;
    private JLabel lblRate, lblSubtotal, lblTax, lblTotal, lblStatus;

    // buttons 
    private JButton btnLoad, btnCheckout, btnPrint;

    // history table
    private DefaultTableModel tableModel;

    public BillingPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    // layout

    private void buildUI() {
        add(RoyalComponents.makeSectionHeader("\uD83D\uDCB0", "Billing & Checkout"),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(Theme.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        body.add(buildTopRow(),       BorderLayout.NORTH);
        body.add(buildHistoryTable(), BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    private JPanel buildTopRow() {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        row.add(buildSelectorCard(), BorderLayout.WEST);
        row.add(buildBillCard(),     BorderLayout.CENTER);
        return row;
    }

    // selector card (left)

    private JPanel buildSelectorCard() {
        JPanel card = RoyalComponents.makeCard();
        card.setPreferredSize(new Dimension(290, 0));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));

        JLabel title = cardTitle("Select Reservation");

        JSeparator sep = goldSep();

        JLabel hint = RoyalComponents.makeSmall("Shows Active reservations only");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbReservation = new JComboBox<>();
        cbReservation.setFont(Theme.FONT_TEXT);
        cbReservation.setBackground(Theme.BG_INPUT);
        cbReservation.setForeground(Theme.TEXT_CREAM);
        cbReservation.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cbReservation.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLoad = RoyalComponents.makeGoldBtn("Load Bill");
        btnLoad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLoad.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLoad.addActionListener(e -> loadBill());

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(hint);
        card.add(Box.createVerticalStrut(8));
        card.add(cbReservation);
        card.add(Box.createVerticalStrut(14));
        card.add(btnLoad);
        card.add(Box.createVerticalGlue());
        return card;
    }

    // bill card (right) 

    private JPanel buildBillCard() {
        JPanel card = RoyalComponents.makeCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        card.add(buildBillHeader(),  BorderLayout.NORTH);
        card.add(buildBillDetails(), BorderLayout.CENTER);
        card.add(buildActionRow(),   BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildBillHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JPanel hotelRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        hotelRow.setOpaque(false);
        hotelRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel crownLbl = new JLabel("\uD83D\uDC51");
        crownLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        crownLbl.setForeground(Theme.TEXT_GOLD);

        JLabel nameLbl = new JLabel("Martinez Royal Suite");
        nameLbl.setFont(new Font("Georgia", Font.BOLD, 16));
        nameLbl.setForeground(Theme.TEXT_GOLD);

        hotelRow.add(crownLbl);
        hotelRow.add(nameLbl);

        JLabel subLbl = new JLabel("Official Invoice / Bill of Charges");
        subLbl.setFont(Theme.FONT_SUBHDR);
        subLbl.setForeground(Theme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(hotelRow);
        header.add(Box.createVerticalStrut(2));
        header.add(subLbl);
        header.add(Box.createVerticalStrut(10));
        return header;
    }

    private JPanel buildBillDetails() {
        lblResId     = billVal("—");
        lblGuestName = billVal("—");
        lblRoomNo    = billVal("—");
        lblRoomType  = billVal("—");
        lblCheckIn   = billVal("—");
        lblCheckOut  = billVal("—");
        lblNights    = billVal("—");
        lblRate      = billVal("—");
        lblSubtotal  = billVal("—");
        lblTax       = billVal("—");
        lblTotal     = billValBig("—");
        lblStatus    = billVal("—");

        JPanel details = new JPanel(new GridLayout(0, 2, 8, 8));
        details.setOpaque(false);

        details.add(billKey("Reservation ID:")); details.add(lblResId);
        details.add(billKey("Guest Name:"));     details.add(lblGuestName);
        details.add(billKey("Room Number:"));    details.add(lblRoomNo);
        details.add(billKey("Room Type:"));      details.add(lblRoomType);
        details.add(billKey("Check-In:"));       details.add(lblCheckIn);
        details.add(billKey("Check-Out:"));      details.add(lblCheckOut);
        details.add(billKey("Nights:"));         details.add(lblNights);
        details.add(billKey("Rate/Night:"));     details.add(lblRate);

        JSeparator s1 = new JSeparator(); s1.setForeground(Theme.BORDER_GOLD);
        JSeparator s2 = new JSeparator(); s2.setForeground(Theme.BORDER_GOLD);
        details.add(s1); details.add(s2);

        details.add(billKey("Subtotal:"));       details.add(lblSubtotal);
        details.add(billKey("Tax (0%):"));       details.add(lblTax);
        details.add(billKeyBig("TOTAL DUE:"));   details.add(lblTotal);
        details.add(billKey("Status:"));         details.add(lblStatus);

        return details;
    }

    private JPanel buildActionRow() {
        btnCheckout = RoyalComponents.makeSuccessBtn("Checkout");
        btnPrint    = RoyalComponents.makeGoldBtn("Print Bill");
        btnCheckout.addActionListener(e -> doCheckout());
        btnPrint.addActionListener(e    -> printBill());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.add(btnPrint);
        row.add(btnCheckout);
        return row;
    }

    // history table 

    private JPanel buildHistoryTable() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("Complete Reservation History");
        lbl.setFont(Theme.FONT_HEADER);
        lbl.setForeground(Theme.TEXT_GOLD);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] cols = {"Res. ID", "Customer", "Room", "Check-In", "Check-Out",
                         "Nights", "Total", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable histTable = RoyalComponents.makeTable(tableModel, true);

        int[] widths = {68, 140, 65, 100, 100, 58, 100, 95};
        for (int i = 0; i < widths.length; i++)
            histTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        p.add(lbl, BorderLayout.NORTH);
        p.add(RoyalComponents.makeScrollPane(histTable), BorderLayout.CENTER);
        return p;
    }

    // actions 

    private void loadBill() {
        Reservation res = (Reservation) cbReservation.getSelectedItem();
        if (res == null) {
            RoyalComponents.showError(this, "No reservation selected.");
            return;
        }

        Customer cust = FileHandler.findCustomer(res.getCustomerId());
        Room     room = FileHandler.findRoom(res.getRoomNumber());

        String custName = (cust != null) ? cust.getName()           : "Unknown";
        String roomType = (room != null) ? room.getType()           : "Unknown";
        double rate     = (room != null) ? room.getPricePerNight()  : 0;

        lblResId.setText(res.getReservationId());
        lblGuestName.setText(custName);
        lblRoomNo.setText("Room " + res.getRoomNumber());
        lblRoomType.setText(roomType);
        lblCheckIn.setText(res.getCheckInDate());
        lblCheckOut.setText(res.getCheckOutDate());
        lblNights.setText(res.getNights() + " night(s)");
        lblRate.setText(String.format("₱%,.2f", rate));
        lblSubtotal.setText(String.format("₱%,.2f", res.getTotalCost()));
        lblTax.setText("₱0.00");
        lblTotal.setText(String.format("₱%,.2f", res.getTotalCost()));
        lblStatus.setText(res.getStatus());

        switch (res.getStatus()) {
            case Reservation.STATUS_ACTIVE:
                lblStatus.setForeground(Theme.SUCCESS); break;
            case Reservation.STATUS_CHECKED_OUT:
                lblStatus.setForeground(Theme.INFO);    break;
            default:
                lblStatus.setForeground(Theme.DANGER);
        }
    }

    private void doCheckout() {
        Reservation res = (Reservation) cbReservation.getSelectedItem();
        if (res == null) {
            RoyalComponents.showError(this, "Please load a bill first.");
            return;
        }
        if (!res.getStatus().equals(Reservation.STATUS_ACTIVE)) {
            RoyalComponents.showError(this,
                    "This reservation is already " + res.getStatus() + ".");
            return;
        }

        Customer cust     = FileHandler.findCustomer(res.getCustomerId());
        Room     room     = FileHandler.findRoom(res.getRoomNumber());
        String   custName = (cust != null) ? cust.getName()
                                           : FileHandler.getCustomerName(res.getCustomerId());

        if (!RoyalComponents.confirm(this,
                "Confirm checkout for " + custName + "?\n"
                + "Room " + res.getRoomNumber() + " will be marked available again.\n\n"
                + "The guest's profile will be archived automatically.")) return;

        res.setStatus(Reservation.STATUS_CHECKED_OUT);
        if (room != null) room.setAvailable(true);

        FileHandler.saveReservations();
        FileHandler.saveRooms();
        FileHandler.archiveGuest(cust, res, room);

        refresh();
        frame.refreshAllPanels();
        loadBill();

        RoyalComponents.showInfo(this,
                "Checkout complete!\n"
                + "Thank you for staying at Martinez Royal Suite.\n"
                + "Total Charged: " + String.format("₱%,.2f", res.getTotalCost()) + "\n\n"
                + custName + "'s profile has been moved to Archived Guests.");
    }

    private void printBill() {
        Reservation res = (Reservation) cbReservation.getSelectedItem();
        if (res == null) {
            RoyalComponents.showError(this, "Please load a bill first.");
            return;
        }

        Customer cust    = FileHandler.findCustomer(res.getCustomerId());
        Room     room    = FileHandler.findRoom(res.getRoomNumber());
        String custName  = (cust != null) ? cust.getName()          : "—";
        String roomType  = (room != null) ? room.getType()          : "—";
        double rate      = (room != null) ? room.getPricePerNight() : 0;

        String receipt =
            "═══════════════════════════════════════════\n"
            + "          " + Theme.CROWN + "  Martinez Royal Suite\n"
            + "          Hotel Management System\n"
            + "═══════════════════════════════════════════\n"
            + "  OFFICIAL RECEIPT\n"
            + "───────────────────────────────────────────\n"
            + "  Reservation ID : " + res.getReservationId()         + "\n"
            + "  Guest          : " + custName                        + "\n"
            + "  Room           : " + res.getRoomNumber() + " (" + roomType + ")\n"
            + "  Check-In       : " + res.getCheckInDate()            + "\n"
            + "  Check-Out      : " + res.getCheckOutDate()           + "\n"
            + "  Nights         : " + res.getNights()                 + "\n"
            + "  Rate/Night     : " + String.format("₱%,.2f", rate)  + "\n"
            + "───────────────────────────────────────────\n"
            + "  Subtotal       : " + String.format("₱%,.2f", res.getTotalCost()) + "\n"
            + "  Tax (0%)       : ₱0.00\n"
            + "───────────────────────────────────────────\n"
            + "  TOTAL CHARGED  : " + String.format("₱%,.2f", res.getTotalCost()) + "\n"
            + "═══════════════════════════════════════════\n"
            + "  Status         : " + res.getStatus()                 + "\n"
            + "───────────────────────────────────────────\n"
            + "  Thank you for choosing Martinez Royal Suite!\n"
            + "  We hope to welcome you again soon.\n"
            + "═══════════════════════════════════════════\n";

        JTextArea ta = new JTextArea(receipt);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setBackground(new Color(12, 4, 32));
        ta.setForeground(Theme.GOLD);
        ta.setCaretColor(Theme.GOLD);
        ta.setEditable(false);
        ta.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(460, 440));
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER_GOLD, 1));

        JDialog dlg = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Receipt — " + res.getReservationId(), true);
        dlg.getContentPane().setBackground(Theme.BG_DARKEST);
        dlg.getContentPane().add(sp, BorderLayout.CENTER);

        JButton copyBtn = RoyalComponents.makeGoldBtn("Copy Text");
        copyBtn.addActionListener(e -> {
            ta.selectAll();
            ta.copy();
            ta.select(0, 0);
            RoyalComponents.showInfo(dlg, "Receipt text copied to clipboard!");
        });

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bar.setBackground(Theme.BG_DARKEST);
        bar.add(copyBtn);
        dlg.getContentPane().add(bar, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // refresh 

    public void refresh() {
        cbReservation.removeAllItems();
        for (Reservation r : FileHandler.reservations)
            if (r.getStatus().equals(Reservation.STATUS_ACTIVE))
                cbReservation.addItem(r);

        tableModel.setRowCount(0);
        for (Reservation r : FileHandler.reservations) {
            tableModel.addRow(new Object[]{
                r.getReservationId(),
                FileHandler.getCustomerName(r.getCustomerId()),
                "Room " + r.getRoomNumber(),
                r.getCheckInDate(),
                r.getCheckOutDate(),
                r.getNights() + "N",
                String.format("₱%,.2f", r.getTotalCost()),
                r.getStatus()
            });
        }

        for (JLabel l : new JLabel[]{lblResId, lblGuestName, lblRoomNo, lblRoomType,
                lblCheckIn, lblCheckOut, lblNights, lblRate, lblSubtotal,
                lblTax, lblTotal, lblStatus}) {
            if (l != null) { l.setText("—"); l.setForeground(Theme.TEXT_CREAM); }
        }
    }

    // helpers 

    private JLabel cardTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADER);
        l.setForeground(Theme.TEXT_GOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JSeparator goldSep() {
        JSeparator s = new JSeparator();
        s.setForeground(Theme.BORDER_GOLD);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    private JLabel billKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    private JLabel billKeyBig(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Georgia", Font.BOLD, 14));
        l.setForeground(Theme.TEXT_GOLD);
        return l;
    }

    private JLabel billVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TEXT);
        l.setForeground(Theme.TEXT_CREAM);
        return l;
    }

    private JLabel billValBig(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 18));
        l.setForeground(Theme.GOLD_BRIGHT);
        return l;
    }
}