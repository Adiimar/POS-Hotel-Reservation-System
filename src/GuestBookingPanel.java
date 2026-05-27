import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class GuestBookingPanel extends JPanel {

    private final MainFrame frame;

    private static final int    STEP_BROWSE = 0, STEP_DETAILS = 1, STEP_BOOKING = 2, STEP_CONFIRM = 3;
    private static final String[] STEP_KEYS  = {"browse", "details", "booking", "confirm"};
    private static final String[] STEP_NAMES = {"1  Browse Rooms", "2  Room Details", "3  Guest Info", "4  Confirm Booking"};

    private int  currentStep = STEP_BROWSE;
    private Room selectedRoom;

    private JLabel[]   stepLabels = new JLabel[4];
    private CardLayout stepLayout;
    private JPanel     stepArea;

    // Step 1
    private DefaultTableModel roomTableModel;
    private JTable            roomTable;
    private JComboBox<String> cbTypeFilter;
    private JTextField        tfMaxPrice;

    // Step 2
    private JLabel lblDetNumber, lblDetType, lblDetPrice, lblDetStatus;

    // Step 3
    private JComboBox<String>   cbGuestMode;
    private JPanel              cardGuestMode;
    private CardLayout          guestModeLayout;
    private JComboBox<Customer> cbCustomer;
    private JTextField          fldNewName, fldNewContact, fldNewEmail, fldNewIdNumber;
    private JComboBox<String>   cbNewIdType;

    // Step 4  ← lblRoom added
    private JTextField tfCheckIn, tfCheckOut;
    private JLabel     lblRoom, lblNights, lblRatePerNight, lblSubtotal, lblTotal;

    private JButton btnBack, btnNext, btnConfirm;

    public GuestBookingPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    // layout 

    private void buildUI() {
        add(RoyalComponents.makeSectionHeader("\uD83D\uDECE", "Guest Booking"), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        body.add(buildStepIndicator(), BorderLayout.NORTH);
        body.add(buildStepArea(),      BorderLayout.CENTER);
        body.add(buildNavBar(),        BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildStepIndicator() {
        JPanel p = new JPanel(new GridLayout(1, 4));
        p.setBackground(Theme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, Theme.BORDER),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        for (int i = 0; i < 4; i++) {
            stepLabels[i] = new JLabel(STEP_NAMES[i], SwingConstants.CENTER);
            stepLabels[i].setFont(new Font("Arial", Font.BOLD, 12));
            stepLabels[i].setForeground(i == 0 ? Theme.TEXT_GOLD : Theme.TEXT_MUTED);
            p.add(stepLabels[i]);
        }
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        w.add(p);
        return w;
    }

    private JPanel buildStepArea() {
        stepLayout = new CardLayout();
        stepArea   = new JPanel(stepLayout);
        stepArea.setOpaque(false);
        stepArea.add(buildBrowseStep(),  STEP_KEYS[0]);
        stepArea.add(buildDetailsStep(), STEP_KEYS[1]);
        stepArea.add(buildBookingStep(), STEP_KEYS[2]);
        stepArea.add(buildConfirmStep(), STEP_KEYS[3]);
        return stepArea;
    }

    private JPanel buildNavBar() {
        btnBack    = RoyalComponents.makeOutlineBtn("← Back");
        btnNext    = RoyalComponents.makeGoldBtn("Next →");
        btnConfirm = RoyalComponents.makeSuccessBtn("Confirm Booking");
        btnBack.setPreferredSize(new Dimension(120, 36));
        btnNext.setPreferredSize(new Dimension(120, 36));
        btnConfirm.setPreferredSize(new Dimension(160, 36));
        btnBack.addActionListener(e    -> goBack());
        btnNext.addActionListener(e    -> goNext());
        btnConfirm.addActionListener(e -> confirmBooking());
        btnBack.setVisible(false);
        btnConfirm.setVisible(false);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(btnConfirm); right.add(btnNext);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        bar.add(btnBack, BorderLayout.WEST);
        bar.add(right,   BorderLayout.EAST);
        return bar;
    }

    // step panels 

    private JPanel buildBrowseStep() {
        cbTypeFilter = RoyalComponents.makeCombo(new String[]{"All Types","Standard","Deluxe","Suite","Presidential"});
        cbTypeFilter.setPreferredSize(new Dimension(140, 32));
        tfMaxPrice = RoyalComponents.makeField(8);
        tfMaxPrice.setPreferredSize(new Dimension(100, 32));
        tfMaxPrice.setToolTipText("Leave blank for no limit");

        JButton btnFilter = RoyalComponents.makeGoldBtn("Filter");
        btnFilter.setPreferredSize(new Dimension(80, 32));
        btnFilter.addActionListener(e -> applyRoomFilter());
        JButton btnReset = RoyalComponents.makeOutlineBtn("Reset");
        btnReset.setPreferredSize(new Dimension(80, 32));
        btnReset.addActionListener(e -> { cbTypeFilter.setSelectedIndex(0); tfMaxPrice.setText(""); applyRoomFilter(); });

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filterBar.setOpaque(false);
        filterBar.add(RoyalComponents.makeLabel("Room Type:")); filterBar.add(cbTypeFilter);
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(RoyalComponents.makeLabel("Max Price (₱):")); filterBar.add(tfMaxPrice);
        filterBar.add(btnFilter); filterBar.add(btnReset);

        roomTableModel = new DefaultTableModel(new String[]{"Room No.","Type","Price / Night","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = RoyalComponents.makeTable(roomTableModel, true);
        int[] widths = {80, 120, 140, 100};
        for (int i = 0; i < widths.length; i++) roomTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roomTable.getSelectedRow() >= 0) btnNext.setEnabled(true);
        });
        roomTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) goNext(); }
        });

        JLabel hint = RoyalComponents.makeSmall("  Double-click a room or select and click Next →");
        hint.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel p = new JPanel(new BorderLayout(0, 10)); p.setOpaque(false);
        p.add(filterBar,                                 BorderLayout.NORTH);
        p.add(RoyalComponents.makeScrollPane(roomTable), BorderLayout.CENTER);
        p.add(hint,                                      BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildDetailsStep() {
        lblDetNumber = detailVal("—"); lblDetType   = detailVal("—");
        lblDetPrice  = detailVal("—"); lblDetStatus = detailVal("—");

        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 14));
        grid.setOpaque(false); grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.add(detailKey("Room Number:")); grid.add(lblDetNumber);
        grid.add(detailKey("Room Type:"));   grid.add(lblDetType);
        grid.add(detailKey("Price / Night:")); grid.add(lblDetPrice);
        grid.add(detailKey("Availability:")); grid.add(lblDetStatus);

        JLabel note = RoyalComponents.makeSmall("Click Next to proceed to guest information and date selection.");
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        return wrapInCard("Room Details", new Dimension(520, 320), grid, vgap(16), note);
    }

    private JPanel buildBookingStep() {
        JLabel modeLabel = RoyalComponents.makeLabel("Guest Type:");
        modeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbGuestMode = RoyalComponents.makeCombo(new String[]{"Existing Customer","New Guest (Register Now)"});
        cbGuestMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbGuestMode.setAlignmentX(Component.LEFT_ALIGNMENT);

        guestModeLayout = new CardLayout();
        cardGuestMode   = new JPanel(guestModeLayout);
        cardGuestMode.setOpaque(false);
        cardGuestMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardGuestMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        cardGuestMode.add(buildExistingCustomerPanel(), "existing");
        cardGuestMode.add(buildNewGuestPanel(),         "new");
        cbGuestMode.addActionListener(e ->
            guestModeLayout.show(cardGuestMode, cbGuestMode.getSelectedIndex() == 0 ? "existing" : "new"));

        return wrapInCard("Guest Information", new Dimension(580, 420),
            modeLabel, vgap(6), cbGuestMode, vgap(18), cardGuestMode);
    }

    private JPanel buildExistingCustomerPanel() {
        JLabel lbl = RoyalComponents.makeLabel("Select Customer:");
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbCustomer = new JComboBox<>();
        cbCustomer.setFont(Theme.FONT_TEXT); cbCustomer.setBackground(Theme.BG_INPUT);
        cbCustomer.setForeground(Theme.TEXT_CREAM);
        cbCustomer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbCustomer.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbCustomer.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? Theme.TBL_SELECT : Theme.BG_INPUT);
                setForeground(Theme.TEXT_CREAM); setFont(Theme.FONT_TEXT);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });

        JLabel hint = RoyalComponents.makeSmall("Only customers currently in the system are shown.");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(lbl); p.add(vgap(8)); p.add(cbCustomer); p.add(vgap(8)); p.add(hint);
        return p;
    }

    private JPanel buildNewGuestPanel() {
        fldNewName     = RoyalComponents.makeField(20);
        fldNewContact  = RoyalComponents.makeField(20);
        fldNewEmail    = RoyalComponents.makeField(20);
        cbNewIdType    = RoyalComponents.makeCombo(Customer.ID_TYPES);
        fldNewIdNumber = RoyalComponents.makeField(20);
        for (JComponent c : new JComponent[]{fldNewName,fldNewContact,fldNewEmail,cbNewIdType,fldNewIdNumber}) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        JLabel req = RoyalComponents.makeSmall("  * Required fields");
        req.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(inlineRow("Full Name *:", fldNewName));     p.add(vgap(8));
        p.add(inlineRow("Contact *:",   fldNewContact));  p.add(vgap(8));
        p.add(inlineRow("Email:",       fldNewEmail));    p.add(vgap(8));
        p.add(inlineRow("ID Type *:",   cbNewIdType));    p.add(vgap(8));
        p.add(inlineRow("ID Number *:", fldNewIdNumber)); p.add(vgap(6));
        p.add(req);
        return p;
    }

    private JPanel buildConfirmStep() {
        tfCheckIn  = makePlaceholderField("yyyy-MM-dd", 12);
        tfCheckOut = makePlaceholderField("yyyy-MM-dd", 12);
        for (JTextField f : new JTextField[]{tfCheckIn, tfCheckOut}) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        JButton btnCalc = RoyalComponents.makeGoldBtn("Calculate");
        btnCalc.setPreferredSize(new Dimension(110, 36));
        btnCalc.addActionListener(e -> calculateCost());

        JPanel datePanel = new JPanel(new GridBagLayout());
        datePanel.setOpaque(false); datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        GridBagConstraints dc = new GridBagConstraints();
        dc.anchor = GridBagConstraints.NORTHWEST; dc.fill = GridBagConstraints.HORIZONTAL; dc.weighty = 0;
        dc.gridx = 0; dc.weightx = 1.0; dc.insets = new Insets(0, 0, 0, 16);
        datePanel.add(makeDateCol("Check-In",  tfCheckIn),  dc);
        dc.gridx = 1;
        datePanel.add(makeDateCol("Check-Out", tfCheckOut), dc);
        dc.gridx = 2; dc.weightx = 0; dc.fill = GridBagConstraints.NONE; dc.insets = new Insets(18, 0, 0, 0);
        datePanel.add(btnCalc, dc);

        // ── CHANGE 1: lblRoom is now a class field so refreshConfirmRoomLabel() can update it
        lblRoom         = detailVal("—");
        lblNights       = detailVal("—");
        lblRatePerNight = detailVal("—");
        lblSubtotal     = detailVal("—");
        lblTotal        = new JLabel("—");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(Theme.GOLD_BRIGHT);

        JLabel totalKey = new JLabel("TOTAL DUE:");
        totalKey.setFont(new Font("Georgia", Font.BOLD, 14));
        totalKey.setForeground(Theme.TEXT_GOLD);

        JPanel costGrid = new JPanel(new GridLayout(0, 2, 16, 12));
        costGrid.setOpaque(false); costGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        // ── CHANGE 2: use lblRoom instead of an anonymous detailVal("—")
        costGrid.add(detailKey("Room:"));          costGrid.add(lblRoom);
        costGrid.add(detailKey("Nights:"));        costGrid.add(lblNights);
        costGrid.add(detailKey("Rate / Night:"));  costGrid.add(lblRatePerNight);
        costGrid.add(goldSep()); costGrid.add(goldSep());
        costGrid.add(detailKey("Subtotal:"));      costGrid.add(lblSubtotal);
        costGrid.add(detailKey("Tax (0%):"));      costGrid.add(detailVal("₱0.00"));
        costGrid.add(totalKey);                    costGrid.add(lblTotal);

        // Wrap the hint in a FlowLayout panel so it centres properly inside the BoxLayout card
        JLabel note = RoyalComponents.makeSmall("Click 'Calculate' after setting dates, then 'Confirm Booking' to save.");
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        notePanel.setOpaque(false);
        notePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        notePanel.add(note);

        return wrapInCard("Booking Summary", new Dimension(560, 460),
            datePanel, vgap(20), costGrid, vgap(12), notePanel);
    }

    // navigation 

    private void goNext() {
        switch (currentStep) {
            case STEP_BROWSE:
                int row = roomTable.getSelectedRow();
                if (row < 0) { RoyalComponents.showError(this, "Please select a room first."); return; }
                String roomNo = (String) roomTableModel.getValueAt(row, 0);
                selectedRoom  = FileHandler.findRoom(roomNo);
                if (selectedRoom == null) return;
                if (!selectedRoom.isAvailable()) {
                    RoyalComponents.showError(this, "Room " + roomNo + " is currently occupied. Please select an available room.");
                    return;
                }
                populateRoomDetails();
                goToStep(STEP_DETAILS);
                break;
            case STEP_DETAILS:
                goToStep(STEP_BOOKING);
                break;
            case STEP_BOOKING:
                if (!validateGuestStep()) return;
                refreshConfirmRoomLabel();
                goToStep(STEP_CONFIRM);
                break;
        }
    }

    private boolean validateGuestStep() {
        if (cbGuestMode.getSelectedIndex() == 0) {
            if (cbCustomer.getSelectedItem() == null || cbCustomer.getItemCount() == 0) {
                RoyalComponents.showError(this, "No customers found. Please register a new guest instead.");
                return false;
            }
            return true;
        }
        String name = fldNewName.getText().trim(), contact = fldNewContact.getText().trim(),
               idNum = fldNewIdNumber.getText().trim();
        if (name.isEmpty())                     { return fieldErr(fldNewName,     "Full name is required."); }
        if (!name.matches("[a-zA-Z\\s.'\\-]+")) { return fieldErr(fldNewName,     "Name must contain letters only."); }
        if (contact.isEmpty())                  { return fieldErr(fldNewContact,  "Contact number is required."); }
        if (!contact.matches("[0-9]{7,15}"))    { return fieldErr(fldNewContact,  "Contact must be 7–15 digits (e.g. 09171234567)."); }
        if (idNum.isEmpty())                    { return fieldErr(fldNewIdNumber, "Government ID number is required."); }
        if (!idNum.matches("[0-9]+"))           { return fieldErr(fldNewIdNumber, "ID number must contain digits only."); }
        return true;
    }

    private boolean fieldErr(JTextField f, String msg) {
        RoyalComponents.showError(this, msg); f.requestFocus(); return false;
    }

    private void goBack() { if (currentStep > STEP_BROWSE) goToStep(currentStep - 1); }

    private void goToStep(int step) {
        currentStep = step;
        for (int i = 0; i < stepLabels.length; i++) {
            stepLabels[i].setForeground(i == step ? Theme.TEXT_GOLD : Theme.TEXT_MUTED);
            stepLabels[i].setFont(new Font("Arial", i == step ? Font.BOLD : Font.PLAIN, 12));
        }
        stepLayout.show(stepArea, STEP_KEYS[step]);
        btnBack.setVisible(step > STEP_BROWSE);
        btnNext.setVisible(step < STEP_CONFIRM);
        btnConfirm.setVisible(step == STEP_CONFIRM);
    }

    // business logic 

    private void applyRoomFilter() {
        String type = (String) cbTypeFilter.getSelectedItem();
        double max  = Double.MAX_VALUE;
        if (!tfMaxPrice.getText().trim().isEmpty()) {
            try { max = Double.parseDouble(tfMaxPrice.getText().trim().replace(",", "")); }
            catch (NumberFormatException ex) { RoyalComponents.showError(this, "Max price must be a number."); return; }
        }
        roomTableModel.setRowCount(0);
        for (Room r : FileHandler.rooms) {
            if ((type.equals("All Types") || r.getType().equals(type)) && r.getPricePerNight() <= max)
                roomTableModel.addRow(new Object[]{r.getRoomNumber(), r.getType(),
                    String.format("₱%,.2f", r.getPricePerNight()), r.isAvailable() ? "Available" : "Occupied"});
        }
    }

    private void populateRoomDetails() {
        if (selectedRoom == null) return;
        lblDetNumber.setText("Room " + selectedRoom.getRoomNumber());
        lblDetType.setText(selectedRoom.getType());
        lblDetPrice.setText(String.format("₱%,.2f / night", selectedRoom.getPricePerNight()));
        boolean avail = selectedRoom.isAvailable();
        lblDetStatus.setText(avail ? "✔  Available" : "○  Occupied");
        lblDetStatus.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
        lblDetStatus.setForeground(avail ? Theme.SUCCESS : Theme.DANGER);
    }

    // ── CHANGE 4: refreshConfirmRoomLabel now also populates lblRoom
    private void refreshConfirmRoomLabel() {
        if (selectedRoom != null) {
            lblRoom.setText("Room " + selectedRoom.getRoomNumber() + " — " + selectedRoom.getType());
            lblRatePerNight.setText(String.format("₱%,.2f", selectedRoom.getPricePerNight()));
        } else {
            lblRoom.setText("—");
            lblRatePerNight.setText("—");
        }
        lblNights.setText("—"); lblSubtotal.setText("—"); lblTotal.setText("—");
    }

    private void calculateCost() {
        if (selectedRoom == null) { RoyalComponents.showError(this, "No room selected."); return; }
        String inStr = tfCheckIn.getText().trim(), outStr = tfCheckOut.getText().trim();
        long nights = Reservation.calculateNights(inStr, outStr);
        if (nights <= 0) {
            RoyalComponents.showError(this, "Invalid dates. Check-out must be after check-in.\nFormat: yyyy-MM-dd");
            return;
        }
        if (FileHandler.isDoubleBooked(selectedRoom.getRoomNumber(), inStr, outStr, null)) {
            RoyalComponents.showError(this, "Room " + selectedRoom.getRoomNumber() + " is already booked for those dates.");
            return;
        }
        double rate = selectedRoom.getPricePerNight(), total = rate * nights;
        lblNights.setText(nights + " night(s)");
        lblRatePerNight.setText(String.format("₱%,.2f", rate));
        lblSubtotal.setText(String.format("₱%,.2f", total));
        lblTotal.setText(String.format("₱%,.2f", total));
    }

    private void confirmBooking() {
        String inStr = tfCheckIn.getText().trim(), outStr = tfCheckOut.getText().trim();
        long nights  = Reservation.calculateNights(inStr, outStr);
        if (nights <= 0) {
            RoyalComponents.showError(this, "Please set valid check-in and check-out dates, then click Calculate."); return;
        }
        if (lblTotal.getText().equals("—")) {
            RoyalComponents.showError(this, "Please click 'Calculate' to confirm the cost before booking."); return;
        }
        if (FileHandler.isDoubleBooked(selectedRoom.getRoomNumber(), inStr, outStr, null)) {
            RoyalComponents.showError(this, "Room " + selectedRoom.getRoomNumber() + " is already booked for those dates."); return;
        }

        Customer customer;
        if (cbGuestMode.getSelectedIndex() == 0) {
            customer = (Customer) cbCustomer.getSelectedItem();
            if (customer == null) { RoyalComponents.showError(this, "Please select a customer."); return; }
        } else {
            String name = fldNewName.getText().trim(), contact = fldNewContact.getText().trim(),
                   email = fldNewEmail.getText().trim(), idNum = fldNewIdNumber.getText().trim();
            String idType = (String) cbNewIdType.getSelectedItem();
            if (name.isEmpty())   { RoyalComponents.showError(this, "Guest name is required."); return; }
            if (contact.isEmpty() || !contact.matches("[0-9+\\-\\s]{7,15}")) {
                RoyalComponents.showError(this, "Valid contact number required (7-15 digits)."); return;
            }
            if (idNum.isEmpty())  { RoyalComponents.showError(this, "Government ID number is required."); return; }
            String newId = FileHandler.generateCustomerId();
            customer = new Customer(newId, name, contact, email, idType, idNum);
            FileHandler.customers.add(customer); FileHandler.saveCustomers();
        }

        double total = selectedRoom.getPricePerNight() * nights;
        String resId = FileHandler.generateReservationId();
        FileHandler.reservations.add(new Reservation(resId, customer.getCustomerId(),
            selectedRoom.getRoomNumber(), inStr, outStr, total, Reservation.STATUS_ACTIVE));
        selectedRoom.setAvailable(false);
        FileHandler.saveReservations(); FileHandler.saveRooms();
        frame.refreshAllPanels();

        RoyalComponents.showInfo(this,
            "Booking Confirmed!\n\n"
            + "Reservation ID : " + resId               + "\n"
            + "Guest          : " + customer.getName()  + "\n"
            + "Room           : " + selectedRoom.getRoomNumber() + " (" + selectedRoom.getType() + ")\n"
            + "Check-In       : " + inStr  + "\n"
            + "Check-Out      : " + outStr + "\n"
            + "Nights         : " + nights + "\n"
            + "Total          : " + String.format("₱%,.2f", total));
        resetPanel();
    }

    //  reset 

    public void refresh() { applyRoomFilter(); refreshCustomerCombo(); }

    private void refreshCustomerCombo() {
        cbCustomer.removeAllItems();
        for (Customer c : FileHandler.customers) cbCustomer.addItem(c);
    }

    private void resetPanel() {
        selectedRoom = null;
        roomTable.clearSelection();
        cbGuestMode.setSelectedIndex(0);
        guestModeLayout.show(cardGuestMode, "existing");
        tfCheckIn.setText(""); tfCheckOut.setText("");
        fldNewName.setText(""); fldNewContact.setText(""); fldNewEmail.setText(""); fldNewIdNumber.setText("");
        cbNewIdType.setSelectedIndex(0);
        lblRoom.setText("—");
        lblNights.setText("—"); lblRatePerNight.setText("—"); lblSubtotal.setText("—"); lblTotal.setText("—");
        refresh(); goToStep(STEP_BROWSE);
    }

    // helpers 

    /** Wraps components in a centred card with a title + gold separator pre-added. */
    private JPanel wrapInCard(String title, Dimension size, Component... components) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Theme.BORDER); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 40, 28, 40));
        card.setPreferredSize(size);

        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_HEADER); lbl.setForeground(Theme.TEXT_GOLD);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_GOLD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        card.add(lbl); card.add(vgap(8)); card.add(sep); card.add(vgap(20));
        for (Component c : components) card.add(c);

        JPanel outer = new JPanel(new GridBagLayout()); outer.setOpaque(false);
        outer.add(card);
        return outer;
    }

    /** Labelled column wrapping a date input field. */
    private JPanel makeDateCol(String label, JTextField field) {
        JPanel col = new JPanel(); col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        JLabel lbl = RoyalComponents.makeLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        col.add(lbl); col.add(vgap(4)); col.add(field);
        return col;
    }

    /** Text field with italic ghost-text format hint that disappears when the user types. */
    private JTextField makePlaceholderField(String placeholder, int cols) {
        JTextField f = new JTextField(cols) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(140, 160, 190, 150));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets(); FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, ins.left + 2, (getHeight()-fm.getHeight())/2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        f.setBackground(Theme.BG_INPUT); f.setForeground(Theme.TEXT_CREAM);
        f.setCaretColor(Theme.TEXT_CREAM); f.setFont(Theme.FONT_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { f.repaint(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { f.repaint(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { f.repaint(); }
        });
        return f;
    }

    private JPanel inlineRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false); row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JLabel lbl = RoyalComponents.makeLabel(label);
        lbl.setPreferredSize(new Dimension(110, 24));
        row.add(lbl, BorderLayout.WEST); row.add(field, BorderLayout.CENTER);
        return row;
    }

    private Component  vgap(int h) { return Box.createVerticalStrut(h); }
    private JSeparator goldSep()   { JSeparator s = new JSeparator(); s.setForeground(Theme.BORDER_GOLD); return s; }

    private JLabel detailKey(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial", Font.BOLD, 12)); l.setForeground(Theme.TEXT_MUTED); return l;
    }
    private JLabel detailVal(String t) {
        JLabel l = new JLabel(t); l.setFont(Theme.FONT_TEXT); l.setForeground(Theme.TEXT_CREAM); return l;
    }
}