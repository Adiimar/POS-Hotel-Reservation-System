import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivedGuestsPanel extends JPanel {

    // table 
    private DefaultTableModel tableModel;
    private JTable            archiveTable;

    // dearch/filter 
    private JTextField tfSearch;

    // detail labels 
    private JLabel lblArchId, lblCustId, lblName, lblContact;
    private JLabel lblResId, lblRoom, lblType;
    private JLabel lblIn, lblOut, lblNights, lblTotal, lblArchivedOn;

    // stat labels 
    private JLabel lblStatCount, lblStatRevenue;

    public ArchivedGuestsPanel(MainFrame frame) {
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        add(RoyalComponents.makeSectionHeader("\uD83D\uDDC4\uFE0F", "Archived Guests"),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(Theme.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        body.add(buildStatBar(),    BorderLayout.NORTH);
        body.add(buildMainArea(),   BorderLayout.CENTER);
        body.add(buildDetailCard(), BorderLayout.SOUTH);

        add(body, BorderLayout.CENTER);
    }

    // stat bar
    private JPanel buildStatBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 4));
        bar.setOpaque(false);

        lblStatCount   = statLabel("—");
        lblStatRevenue = statLabel("—");

        bar.add(statChip("#",  "Total Archived",        lblStatCount,   false));
        bar.add(statChip("P",  "Revenue from Archives", lblStatRevenue, true));

        return bar;
    }

    /**
     * @param symbol   short prefix symbol drawn in gold
     * @param key      descriptive label text
     * @param valLabel the dynamic value label
     * @param isPeso   true -> prepend a peso sign label before the value
     */
    private JPanel statChip(String symbol, String key, JLabel valLabel, boolean isPeso) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        chip.setOpaque(false);

        JLabel symLbl = new JLabel(symbol);
        symLbl.setFont(new Font("Arial", Font.BOLD, 11));
        symLbl.setForeground(Theme.TEXT_GOLD);

        JLabel keyLbl = new JLabel(key + ":");
        keyLbl.setFont(new Font("Arial", Font.BOLD, 12));
        keyLbl.setForeground(Theme.TEXT_MUTED);

        chip.add(symLbl);
        chip.add(keyLbl);

        if (isPeso) {
            JLabel pesoSign = new JLabel("\u20B1");   // ₱
            pesoSign.setFont(new Font("Arial Unicode MS", Font.BOLD, 13));
            pesoSign.setForeground(Theme.TEXT_GOLD);
            chip.add(pesoSign);
        }

        chip.add(valLabel);
        return chip;
    }

    private JLabel statLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(Theme.TEXT_GOLD);
        return l;
    }

    // Main part - search card + table 
    private JPanel buildMainArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Search row
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);

        JLabel lbl = new JLabel("Search:");
        lbl.setFont(Theme.FONT_TEXT);
        lbl.setForeground(Theme.TEXT_CREAM);

        tfSearch = new JTextField(24);
        tfSearch.setFont(Theme.FONT_TEXT);
        tfSearch.setBackground(Theme.BG_INPUT);
        tfSearch.setForeground(Theme.TEXT_CREAM);
        tfSearch.setCaretColor(Theme.TEXT_CREAM);
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_GOLD, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JButton btnSearch = RoyalComponents.makeGoldBtn("Filter");
        btnSearch.addActionListener(e -> applyFilter());

        JButton btnClear = RoyalComponents.makeGoldBtn("Clear");
        btnClear.addActionListener(e -> { tfSearch.setText(""); applyFilter(); });

        searchRow.add(lbl);
        searchRow.add(tfSearch);
        searchRow.add(btnSearch);
        searchRow.add(btnClear);

        // Table
        String[] cols = {
            "Archive ID", "Guest Name", "Contact",
            "Res. ID", "Room", "Type",
            "Check-In", "Check-Out", "Nights",
            "Total (₱)", "Archived On"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        archiveTable = RoyalComponents.makeTable(tableModel, true);

        int[] widths = {75, 130, 120, 70, 60, 90, 95, 95, 50, 95, 120};
        for (int i = 0; i < widths.length; i++)
            archiveTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Row selection → fill detail card
        archiveTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillDetail();
        });

        panel.add(searchRow,                               BorderLayout.NORTH);
        panel.add(RoyalComponents.makeScrollPane(archiveTable), BorderLayout.CENTER);
        return panel;
    }

    // detail card 
    private JPanel buildDetailCard() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setPreferredSize(new Dimension(0, 190));

        JLabel title = new JLabel("Guest Archive Details");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_GOLD);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_GOLD);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(6));
        top.add(sep);
        top.add(Box.createVerticalStrut(10));

        // Two-column detail grid
        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 6));
        grid.setOpaque(false);

        lblArchId    = detailVal("—");
        lblCustId    = detailVal("—");
        lblName      = detailVal("—");
        lblContact   = detailVal("—");
        lblResId     = detailVal("—");
        lblRoom      = detailVal("—");
        lblType      = detailVal("—");
        lblIn        = detailVal("—");
        lblOut       = detailVal("—");
        lblNights    = detailVal("—");
        lblTotal     = detailVal("—");
        lblArchivedOn = detailVal("—");

        grid.add(detailKey("Archive ID:"));   grid.add(lblArchId);
        grid.add(detailKey("Customer ID:"));  grid.add(lblCustId);
        grid.add(detailKey("Guest Name:"));   grid.add(lblName);
        grid.add(detailKey("Contact:"));      grid.add(lblContact);
        grid.add(detailKey("Reservation:"));  grid.add(lblResId);
        grid.add(detailKey("Room:"));         grid.add(lblRoom);
        grid.add(detailKey("Room Type:"));    grid.add(lblType);
        grid.add(detailKey("Check-In:"));     grid.add(lblIn);
        grid.add(detailKey("Check-Out:"));    grid.add(lblOut);
        grid.add(detailKey("Nights:"));       grid.add(lblNights);
        grid.add(detailKey("Total:"));        grid.add(lblTotal);
        grid.add(detailKey("Archived On:"));  grid.add(lblArchivedOn);

        card.add(top,  BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    //  DATA - Called when a row is selected in the table
    private void fillDetail() {
        int row = archiveTable.getSelectedRow();
        if (row < 0) return;

        // Find matching ArchivedGuest by Archive ID in column 0
        String archId = (String) tableModel.getValueAt(row, 0);
        ArchivedGuest ag = null;
        for (ArchivedGuest a : FileHandler.archivedGuests)
            if (a.getArchiveId().equals(archId)) { ag = a; break; }

        if (ag == null) return;

        lblArchId.setText(ag.getArchiveId());
        lblCustId.setText(ag.getCustomerId());
        lblName.setText(ag.getCustomerName());
        lblContact.setText(ag.getCustomerContact());
        lblResId.setText(ag.getReservationId());
        lblRoom.setText("Room " + ag.getRoomNumber());
        lblType.setText(ag.getRoomType());
        lblIn.setText(ag.getCheckInDate());
        lblOut.setText(ag.getCheckOutDate());
        lblNights.setText(ag.getNights() + " night(s)");
        lblTotal.setText(String.format("₱%,.2f", ag.getTotalCost()));
        lblArchivedOn.setText(ag.getArchivedOn());
    }

    // Re-populates the table, optionally filtering by search text. 
    private void applyFilter() {
        String q = tfSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        for (ArchivedGuest a : FileHandler.archivedGuests) {
            if (!q.isEmpty()) {
                boolean match =
                    a.getCustomerName().toLowerCase().contains(q)    ||
                    a.getCustomerId().toLowerCase().contains(q)       ||
                    a.getArchiveId().toLowerCase().contains(q)        ||
                    a.getReservationId().toLowerCase().contains(q)    ||
                    a.getRoomNumber().toLowerCase().contains(q)       ||
                    a.getCustomerContact().toLowerCase().contains(q)  ||
                    a.getCheckInDate().contains(q)                    ||
                    a.getCheckOutDate().contains(q);
                if (!match) continue;
            }
            tableModel.addRow(new Object[]{
                a.getArchiveId(),
                a.getCustomerName(),
                a.getCustomerContact(),
                a.getReservationId(),
                "Room " + a.getRoomNumber(),
                a.getRoomType(),
                a.getCheckInDate(),
                a.getCheckOutDate(),
                a.getNights() + "N",
                String.format("%,.2f", a.getTotalCost()),
                a.getArchivedOn()
            });
        }
    }

    // refresh 

    public void refresh() {
        applyFilter();

        // Stat bar
        int count = FileHandler.archivedGuests.size();
        double rev = FileHandler.archivedGuests.stream()
                        .mapToDouble(ArchivedGuest::getTotalCost).sum();
        lblStatCount.setText(String.valueOf(count));
        lblStatRevenue.setText(String.format("%,.2f", rev));

        // Clear detail pane
        for (JLabel l : new JLabel[]{
                lblArchId, lblCustId, lblName, lblContact,
                lblResId, lblRoom, lblType, lblIn, lblOut,
                lblNights, lblTotal, lblArchivedOn}) {
            if (l != null) l.setText("—");
        }
    }

    private JPanel makeCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
            }
        };
    }

    private JLabel detailKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    private JLabel detailVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TEXT);
        l.setForeground(Theme.TEXT_CREAM);
        return l;
    }
}