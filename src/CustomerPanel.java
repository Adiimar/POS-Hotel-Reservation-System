import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class CustomerPanel extends JPanel {

    private final MainFrame frame;

    //for table
    private DefaultTableModel tableModel;
    private JTable            table;

    // for form fields
    private JLabel            lblGenId;
    private JTextField        fldName, fldContact, fldEmail, fldIdNumber;
    private JComboBox<String> cbIdType;

    // buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // id of current selected customer
    private String selectedCustomerId = null;

    public CustomerPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    // layout of module

    private void buildUI() {
        add(RoyalComponents.makeSectionHeader("\uD83D\uDC64", "Customer Management"),
                BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTablePanel(), buildFormPanel());
        split.setDividerLocation(630);
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

        JLabel sub = RoyalComponents.makeLabel("All Guests  —  Click a row to select");
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] cols = {"ID", "Name", "Contact", "Email", "ID Type", "ID Number"};
        tableModel = new DefaultTableModel(cols, 0);
        table = RoyalComponents.makeTable(tableModel, false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        p.add(sub, BorderLayout.NORTH);
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

        JLabel formTitle = new JLabel("Guest Details");
        formTitle.setFont(Theme.FONT_HEADER);
        formTitle.setForeground(Theme.TEXT_GOLD);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_GOLD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        lblGenId    = new JLabel("(auto-generated)");
        lblGenId.setFont(Theme.FONT_TEXT);
        lblGenId.setForeground(Theme.GOLD_DIM);

        fldName     = RoyalComponents.makeField(18);
        fldContact  = RoyalComponents.makeField(18);
        fldEmail    = RoyalComponents.makeField(18);
        cbIdType    = RoyalComponents.makeCombo(Customer.ID_TYPES);
        fldIdNumber = RoyalComponents.makeField(18);

        JLabel required = RoyalComponents.makeSmall("  * Required fields");
        required.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(formTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(18));
        card.add(formRow("Customer ID :", lblGenId));
        card.add(Box.createVerticalStrut(12));
        card.add(formRow("Full Name * :", fldName));
        card.add(Box.createVerticalStrut(12));
        card.add(formRow("Contact *   :", fldContact));
        card.add(Box.createVerticalStrut(12));
        card.add(formRow("Email  *     :", fldEmail));
        card.add(Box.createVerticalStrut(12));
        card.add(formRow("ID Type  *  :", cbIdType));
        card.add(Box.createVerticalStrut(12));
        card.add(formRow("ID Number * :", fldIdNumber));
        card.add(Box.createVerticalStrut(6));
        card.add(required);
        card.add(Box.createVerticalStrut(22));
        card.add(buildButtons());

        outer.add(card, BorderLayout.NORTH);
        return outer;
    }

    private JPanel formRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = RoyalComponents.makeLabel(labelText);
        lbl.setPreferredSize(new Dimension(115, 26));
        row.add(lbl,   BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildButtons() {
        btnAdd    = RoyalComponents.makeGoldBtn("Add Guest");
        btnUpdate = RoyalComponents.makeGoldBtn("Update");
        btnDelete = RoyalComponents.makeGoldBtn("Delete");
        btnClear  = RoyalComponents.makeGoldBtn("Clear");

        Dimension btnSize = new Dimension(130, 34);
        btnAdd.setPreferredSize(btnSize);
        btnUpdate.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnClear.setPreferredSize(btnSize);

        btnAdd.addActionListener(e    -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
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

    private void addCustomer() {
        if (!validateForm(false)) return;

        String id = FileHandler.generateCustomerId();
        Customer c = new Customer(
            id,
            fldName.getText().trim(),
            fldContact.getText().trim(),
            fldEmail.getText().trim(),
            (String) cbIdType.getSelectedItem(),
            fldIdNumber.getText().trim()
        );
        FileHandler.customers.add(c);
        FileHandler.saveCustomers();
        refresh();
        clearForm();
        RoyalComponents.showInfo(this, "Guest registered! ID assigned: " + id);
    }

    private void updateCustomer() {
        if (selectedCustomerId == null) {
            RoyalComponents.showError(this, "Select a guest from the table first.");
            return;
        }
        if (!validateForm(true)) return;

        Customer c = FileHandler.findCustomer(selectedCustomerId);
        if (c == null) { refresh(); return; }

        c.setName(fldName.getText().trim());
        c.setContact(fldContact.getText().trim());
        c.setEmail(fldEmail.getText().trim());
        c.setIdType((String) cbIdType.getSelectedItem());
        c.setIdNumber(fldIdNumber.getText().trim());

        FileHandler.saveCustomers();
        refresh();
        clearForm();
        RoyalComponents.showInfo(this, "Guest record updated!");
    }

    private void deleteCustomer() {
        if (selectedCustomerId == null) {
            RoyalComponents.showError(this, "Select a guest to delete.");
            return;
        }
        for (Reservation r : FileHandler.reservations) {
            if (r.getCustomerId().equals(selectedCustomerId)
                    && r.getStatus().equals(Reservation.STATUS_ACTIVE)) {
                RoyalComponents.showError(this,
                        "Cannot delete — this guest has an active reservation ("
                        + r.getReservationId() + ").");
                return;
            }
        }

        String name = FileHandler.getCustomerName(selectedCustomerId);
        if (!RoyalComponents.confirm(this, "Delete guest '" + name + "'?")) return;

        final String id = selectedCustomerId;
        FileHandler.customers.removeIf(c -> c.getCustomerId().equals(id));
        FileHandler.saveCustomers();
        refresh();
        clearForm();
    }

    // row selection

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String id   = (String) tableModel.getValueAt(row, 0);
        Customer c  = FileHandler.findCustomer(id);
        if (c == null) return;

        selectedCustomerId = id;
        lblGenId.setText(id);
        fldName.setText(c.getName());
        fldContact.setText(c.getContact());
        fldEmail.setText(c.getEmail());
        cbIdType.setSelectedItem(c.getIdType());
        fldIdNumber.setText(c.getIdNumber());
    }

    // user input validation 

    private boolean validateForm(boolean isUpdate) {
        if (fldName.getText().trim().isEmpty()) {
            RoyalComponents.showError(this, "Full name is required.");
            fldName.requestFocus(); return false;
        }
        String contact = fldContact.getText().trim();
        if (contact.isEmpty()) {
            RoyalComponents.showError(this, "Contact number is required.");
            fldContact.requestFocus(); return false;
        }
        if (!contact.matches("[0-9+\\-\\s]{7,15}")) {
            RoyalComponents.showError(this,
                    "Contact must be 7-15 digits (e.g. 09171234567).");
            fldContact.requestFocus(); return false;
        }
        if (fldIdNumber.getText().trim().isEmpty()) {
            RoyalComponents.showError(this, "Government ID number is required.");
            fldIdNumber.requestFocus(); return false;
        }
        return true;
    }

    // clear 

    private void clearForm() {
        selectedCustomerId = null;
        lblGenId.setText("(auto-generated)");
        fldName.setText("");
        fldContact.setText("");
        fldEmail.setText("");
        cbIdType.setSelectedIndex(0);
        fldIdNumber.setText("");
        table.clearSelection();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Customer c : FileHandler.customers) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getContact(),
                c.getEmail(),
                c.getIdType(),
                c.getIdNumber()
            });
        }
    }
}