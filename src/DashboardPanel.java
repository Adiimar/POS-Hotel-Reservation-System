import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardPanel extends JPanel {

    private final MainFrame frame;
    private final String loggedInUser;   // ← stores username from login

    private JLabel valRooms, valAvail, valActiveRes, valRevenue;
    private DefaultTableModel recentModel;

    // accept username from MainFrame 
    public DashboardPanel(MainFrame frame, String loggedInUser) {
        this.frame        = frame;
        this.loggedInUser = loggedInUser;
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    // header 
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(Theme.BG_HEADER);
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JPanel accent = new JPanel();
        accent.setBackground(Theme.GOLD);
        accent.setPreferredSize(new Dimension(3, 20));
        JLabel title = new JLabel("Dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_WHITE);
        left.add(accent);
        left.add(title);

        JLabel clock = new JLabel();
        clock.setFont(Theme.FONT_SMALL);
        clock.setForeground(Theme.TEXT_MUTED);
        updateClock(clock);
        new Timer(1000, e -> updateClock(clock)).start();

        h.add(left,  BorderLayout.WEST);
        h.add(clock, BorderLayout.EAST);
        return h;
    }

    private void updateClock(JLabel l) {
        l.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy  |  hh:mm:ss a")));
    }

    // body 
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(Theme.BG_MAIN);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));

        body.add(buildWelcome());
        body.add(Box.createVerticalStrut(22));
        body.add(buildStatRow());
        body.add(Box.createVerticalStrut(22));
        body.add(buildQuickActions());
        body.add(Box.createVerticalStrut(20));
        body.add(buildRecentTable());
        return body;
    }

    // welcome part
    private JPanel buildWelcome() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Theme.GOLD_DIM),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        // ── Display logged-in username ──────────────────────────────
        JLabel greet = new JLabel("Welcome back, " + loggedInUser + "!");
        greet.setFont(new Font("Georgia", Font.BOLD, 16));
        greet.setForeground(Theme.TEXT_WHITE);

        JLabel sub = new JLabel("Live overview of Martinez Royal Suite operations.");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);

        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(greet);
        col.add(Box.createVerticalStrut(3));
        col.add(sub);

        p.add(col, BorderLayout.WEST);
        return p;
    }

    // 4 stat cards 
    private JPanel buildStatRow() {
        valRooms     = statVal("0");
        valAvail     = statVal("0");
        valActiveRes = statVal("0");
        valRevenue   = statVal("0");

        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        row.add(statCard("Total Rooms",           valRooms,     Theme.INFO));
        row.add(statCard("Available",             valAvail,     Theme.SUCCESS));
        row.add(statCard("Active Bookings",       valActiveRes, Theme.WARNING));
        row.add(statCard("Revenue (Checked Out)", valRevenue,   Theme.GOLD));
        return row;
    }

    private JPanel statCard(String label, JLabel valLbl, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(Theme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valLbl);
        return card;
    }

    private JLabel statVal(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 26));  // Arial supports ₱
        l.setForeground(Theme.TEXT_WHITE);
        return l;
    }

    // quick-action buttons 
    private JPanel buildQuickActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = RoyalComponents.makeSmall("Quick  →");
        lbl.setForeground(new Color(80, 65, 100));

        JButton toRooms = makeQuickBtn("+ Add Room",    new Color(52, 120, 250), Color.WHITE);
        JButton toCust  = makeQuickBtn("+ Add Guest",   new Color(180, 130, 30), Color.WHITE);
        JButton toRes   = makeQuickBtn("+ New Booking", new Color(52, 120, 250), Color.WHITE);
        JButton toBill  = makeQuickBtn("Checkout",      new Color(180, 130, 30),  Color.WHITE);

        toRooms.setPreferredSize(new Dimension(105, 32));
        toCust.setPreferredSize(new Dimension(110, 32));
        toRes.setPreferredSize(new Dimension(125, 32));
        toBill.setPreferredSize(new Dimension(100, 32));

        toRooms.addActionListener(e -> frame.showPanel(MainFrame.P_ROOMS));
        toCust.addActionListener(e  -> frame.showPanel(MainFrame.P_CUSTOMERS));
        toRes.addActionListener(e   -> frame.showPanel(MainFrame.P_RESERVATIONS));
        toBill.addActionListener(e  -> frame.showPanel(MainFrame.P_BILLING));

        p.add(lbl); p.add(toRooms); p.add(toCust); p.add(toRes); p.add(toBill);
        return p;
    }

    // colored rounded quick-action button 
    private JButton makeQuickBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // recent reservations table 
    private JPanel buildRecentTable() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("Recent Reservations");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] cols = {"Res. ID","Customer","Room","Check-In","Check-Out","Total","Status"};
        recentModel = new DefaultTableModel(cols, 0);
        JTable table = RoyalComponents.makeTable(recentModel, true);

        // ── Gold table header ───────────────────────────────────────
        JTableHeader header = table.getTableHeader();
        header.setBackground(Theme.BG_CARD);
        header.setForeground(Theme.GOLD);
        header.setFont(new Font("Arial", Font.BOLD, 11));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.GOLD_DIM),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, val, sel, focus, row, col);
                lbl.setBackground(Theme.BG_CARD);
                lbl.setForeground(Theme.GOLD);
                lbl.setFont(new Font("Arial", Font.BOLD, 11));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.GOLD_DIM),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
                lbl.setOpaque(true);
                return lbl;
            }
        });

        p.add(title,                                 BorderLayout.NORTH);
        p.add(RoyalComponents.makeScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // refresh 
    public void refresh() {
        long avail = FileHandler.rooms.stream().filter(Room::isAvailable).count();
        valRooms.setText(String.valueOf(FileHandler.rooms.size()));
        valAvail.setText(String.valueOf(avail));
        valActiveRes.setText(String.valueOf(FileHandler.countActiveReservations()));
        valRevenue.setText(String.format("₱%,.0f", FileHandler.getTotalRevenue()));

        recentModel.setRowCount(0);
        int start = Math.max(0, FileHandler.reservations.size() - 8);
        for (int i = FileHandler.reservations.size()-1; i >= start; i--) {
            Reservation r = FileHandler.reservations.get(i);
            recentModel.addRow(new Object[]{
                r.getReservationId(),
                FileHandler.getCustomerName(r.getCustomerId()),
                "Room " + r.getRoomNumber(),
                r.getCheckInDate(),
                r.getCheckOutDate(),
                String.format("₱%,.2f", r.getTotalCost()),
                r.getStatus()
            });
        }
    }
}