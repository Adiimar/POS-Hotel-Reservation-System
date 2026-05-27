import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class RoomPanel extends JPanel {

    private final MainFrame frame;

    // table
    private DefaultTableModel tableModel;
    private JTable            table;

    // form fields 
    private JTextField        fldNumber, fldPrice;
    private JComboBox<String> cbType, cbAvail;

    // buttons 
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // Currently selected table row (-1 = none)
    private int selectedRow = -1;

    public RoomPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    // layout 

    private void buildUI() {
        add(RoyalComponents.makeSectionHeader("\uD83D\uDECF", "Room Management"),
                BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildFormPanel());
        split.setDividerLocation(620);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(Theme.BG_MAIN);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);
    }

    // table panel (left) 

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_MAIN);
        p.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 10));

        JLabel subTitle = RoyalComponents.makeLabel("All Rooms  —  Click a row to select");
        subTitle.setForeground(Theme.TEXT_MUTED);
        subTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] cols = {"Room No.", "Type", "Price / Night", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        table = RoyalComponents.makeTable(tableModel, true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        p.add(subTitle, BorderLayout.NORTH);
        p.add(RoyalComponents.makeScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // form panel (right) 

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 22));

        JPanel card = RoyalComponents.makeCard();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel formTitle = new JLabel("Room Details");
        formTitle.setFont(Theme.FONT_HEADER);
        formTitle.setForeground(Theme.TEXT_GOLD);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_GOLD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        fldNumber = RoyalComponents.makeField(15);
        cbType    = RoyalComponents.makeCombo(Room.TYPES);
        fldPrice  = RoyalComponents.makeField(15);
        cbAvail   = RoyalComponents.makeCombo(new String[]{"Available", "Occupied"});

        card.add(formTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));
        card.add(formRow("Room Number :", fldNumber));
        card.add(Box.createVerticalStrut(14));
        card.add(formRow("Room Type   :", cbType));
        card.add(Box.createVerticalStrut(14));
        card.add(formRow("Price/Night :", fldPrice));
        card.add(Box.createVerticalStrut(14));
        card.add(formRow("Status      :", cbAvail));
        card.add(Box.createVerticalStrut(28));
        card.add(buildButtonPanel());

        outer.add(card, BorderLayout.NORTH);
        return outer;
    }

    private JPanel formRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = RoyalComponents.makeLabel(labelText);
        lbl.setPreferredSize(new Dimension(120, 26));
        row.add(lbl,   BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildButtonPanel() {
        btnAdd    = RoyalComponents.makeGoldBtn("Add Room");
        btnUpdate = RoyalComponents.makeGoldBtn("Update");
        btnDelete = RoyalComponents.makeGoldBtn("Delete");
        btnClear  = RoyalComponents.makeGoldBtn("Clear");

        Dimension btnSize = new Dimension(130, 34);
        btnAdd.setPreferredSize(btnSize);
        btnUpdate.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnClear.setPreferredSize(btnSize);

        btnAdd.addActionListener(e    -> addRoom());
        btnUpdate.addActionListener(e -> updateRoom());
        btnDelete.addActionListener(e -> deleteRoom());
        btnClear.addActionListener(e  -> clearForm());

        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.add(btnAdd);    p.add(btnUpdate);
        p.add(btnDelete); p.add(btnClear);
        return p;
    }

    // CRUD operations 

    private void addRoom() {
        if (!validateForm()) return;

        String number = fldNumber.getText().trim();
        if (!number.matches("\\d+")) {
            RoyalComponents.showError(this,
                    "Room number must contain numbers only (e.g. 101, 202).");
            fldNumber.requestFocus();
            return;
        }
        if (FileHandler.roomExists(number)) {
            RoyalComponents.showError(this,
                    "Room number '" + number + "' already exists.");
            return;
        }

        Room room = new Room(
            number,
            (String) cbType.getSelectedItem(),
            Double.parseDouble(fldPrice.getText().trim().replace(",", "")),
            cbAvail.getSelectedIndex() == 0
        );
        FileHandler.rooms.add(room);
        FileHandler.saveRooms();
        refresh();
        clearForm();
        RoyalComponents.showInfo(this, "Room " + number + " added successfully!");
    }

    private void updateRoom() {
        if (selectedRow < 0) {
            RoyalComponents.showError(this, "Please select a room from the table first.");
            return;
        }
        if (!validateForm()) return;

        String number = (String) tableModel.getValueAt(selectedRow, 0);
        Room room = FileHandler.findRoom(number);
        if (room == null) { refresh(); return; }

        room.setType((String) cbType.getSelectedItem());
        room.setPricePerNight(Double.parseDouble(fldPrice.getText().trim()));
        room.setAvailable(cbAvail.getSelectedIndex() == 0);

        FileHandler.saveRooms();
        refresh();
        clearForm();
        RoyalComponents.showInfo(this, "Room " + number + " updated!");
    }

    private void deleteRoom() {
        if (selectedRow < 0) {
            RoyalComponents.showError(this, "Please select a room to delete.");
            return;
        }
        String number = (String) tableModel.getValueAt(selectedRow, 0);
        for (Reservation r : FileHandler.reservations) {
            if (r.getRoomNumber().equals(number)
                    && r.getStatus().equals(Reservation.STATUS_ACTIVE)) {
                RoyalComponents.showError(this,
                        "Cannot delete Room " + number
                        + " — it has an active reservation ("
                        + r.getReservationId() + ").");
                return;
            }
        }
        if (!RoyalComponents.confirm(this,
                "Delete Room " + number + "? This cannot be undone.")) return;

        FileHandler.rooms.removeIf(r -> r.getRoomNumber().equals(number));
        FileHandler.saveRooms();
        refresh();
        clearForm();
    }

    // row selection 

    private void onRowSelected() {
        selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        String number = (String) tableModel.getValueAt(selectedRow, 0);
        Room room = FileHandler.findRoom(number);
        if (room == null) return;

        fldNumber.setText(room.getRoomNumber());
        fldNumber.setEditable(false);
        cbType.setSelectedItem(room.getType());
        fldPrice.setText(String.valueOf(room.getPricePerNight()));
        cbAvail.setSelectedIndex(room.isAvailable() ? 0 : 1);
    }

    // validation 

    private boolean validateForm() {
        String num   = fldNumber.getText().trim();
        String price = fldPrice.getText().trim();
        if (num.isEmpty()) {
            RoyalComponents.showError(this, "Room number cannot be empty.");
            fldNumber.requestFocus(); return false;
        }
        if (price.isEmpty()) {
            RoyalComponents.showError(this, "Price per night cannot be empty.");
            fldPrice.requestFocus(); return false;
        }
        try {
            double p = Double.parseDouble(price.replace(",", ""));
            if (p <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            RoyalComponents.showError(this,
                    "Price must be a positive number (e.g. 3500.00).");
            fldPrice.requestFocus(); return false;
        }
        return true;
    }

    // refresh 

    private void clearForm() {
        fldNumber.setText("");
        fldNumber.setEditable(true);
        fldPrice.setText("");
        cbType.setSelectedIndex(0);
        cbAvail.setSelectedIndex(0);
        table.clearSelection();
        selectedRow = -1;
    }

    public void refresh() {
        // Define display order using Room.TYPES (same order as the combo box)
        List<String> typeOrder = Arrays.asList(Room.TYPES);

        // Sort a copy so FileHandler.rooms insertion order is preserved for saving
        List<Room> sorted = new ArrayList<>(FileHandler.rooms);
        sorted.sort((a, b) -> {
            int typeA = typeOrder.indexOf(a.getType());
            int typeB = typeOrder.indexOf(b.getType());
            if (typeA != typeB) return Integer.compare(typeA, typeB);
            // Within the same type, sort numerically by room number
            try {
                return Integer.compare(
                    Integer.parseInt(a.getRoomNumber()),
                    Integer.parseInt(b.getRoomNumber())
                );
            } catch (NumberFormatException ex) {
                return a.getRoomNumber().compareTo(b.getRoomNumber());
            }
        });

        tableModel.setRowCount(0);
        for (Room r : sorted) {
            tableModel.addRow(new Object[]{
                r.getRoomNumber(),
                r.getType(),
                String.format("₱%,.2f", r.getPricePerNight()),
                r.isAvailable() ? "Available" : "Occupied"
            });
        }
    }
}