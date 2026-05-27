import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    public static final String P_DASHBOARD    = "dashboard";
    public static final String P_ROOMS        = "rooms";
    public static final String P_CUSTOMERS    = "customers";
    public static final String P_RESERVATIONS = "reservations";
    public static final String P_BILLING      = "billing";
    public static final String P_ARCHIVED     = "archived";
    public static final String P_GUEST_BOOKING = "guestbooking"; // NEW

    private final String loggedInUser;

    private DashboardPanel       dashPanel;
    private RoomPanel            roomPanel;
    private CustomerPanel        customerPanel;
    private ReservationPanel     reservPanel;
    private BillingPanel         billingPanel;
    private ArchivedGuestsPanel  archivedPanel;
    private GuestBookingPanel    guestBookingPanel; 

    private CardLayout cardLayout;
    private JPanel     contentArea;
    private NavBtn[]   navBtns;

    public MainFrame(String loggedInUser) {
        this.loggedInUser = loggedInUser;
        setTitle("Martinez Royal Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 720);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        buildUI();
        showPanel(P_DASHBOARD);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_MAIN);
        root.add(buildSidebar(),     BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // sidebar

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));

        sidebar.add(buildLogo(),   BorderLayout.NORTH);
        sidebar.add(buildNav(),    BorderLayout.CENTER);
        sidebar.add(buildBottom(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildLogo() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_SIDEBAR);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(24, 20, 20, 20)));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel crown = new JLabel("\uD83D\uDC51");
        crown.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        crown.setForeground(Theme.GOLD);

        JLabel name = new JLabel("Martinez");
        name.setFont(new Font("Georgia", Font.BOLD, 15));
        name.setForeground(Theme.GOLD);

        topRow.add(crown);
        topRow.add(name);

        JLabel sub = new JLabel("     Royal Suite");
        sub.setFont(new Font("Georgia", Font.ITALIC, 12));
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(topRow);
        p.add(Box.createVerticalStrut(2));
        p.add(sub);
        return p;
    }

    private JPanel buildNav() {
        navBtns = new NavBtn[]{
            new NavBtn("\uD83C\uDFE0", "Dashboard",       P_DASHBOARD),
            new NavBtn("\uD83D\uDECF", "Room Mgmt.",      P_ROOMS),
            new NavBtn("\uD83D\uDC64", "Customers",       P_CUSTOMERS),
            new NavBtn("\uD83D\uDCC5", "Reservations",    P_RESERVATIONS),
            new NavBtn("\uD83D\uDCB0", "Billing",         P_BILLING),
            new NavBtn("\uD83D\uDDC4\uFE0F", "Archived Guests", P_ARCHIVED),
            new NavBtn("\uD83D\uDECE", "Guest Booking",   P_GUEST_BOOKING), // NEW
        };
        JPanel nav = new JPanel();
        nav.setBackground(Theme.BG_SIDEBAR);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        for (NavBtn b : navBtns) nav.add(b);
        return nav;
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_SIDEBAR);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 18, 16)));

        JButton logout = RoyalComponents.makeRoyalBtn("Log Out");
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> doLogout());

        JLabel ver = new JLabel("v1.0", SwingConstants.CENTER);
        ver.setFont(Theme.FONT_SMALL);
        ver.setForeground(new Color(55, 42, 75));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(logout);
        p.add(Box.createVerticalStrut(8));
        p.add(ver);
        return p;
    }

    // content

    private JPanel buildContentArea() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Theme.BG_MAIN);

        dashPanel         = new DashboardPanel(this, loggedInUser);
        roomPanel         = new RoomPanel(this);
        customerPanel     = new CustomerPanel(this);
        reservPanel       = new ReservationPanel(this);
        billingPanel      = new BillingPanel(this);
        archivedPanel     = new ArchivedGuestsPanel(this);
        guestBookingPanel = new GuestBookingPanel(this); // NEW

        contentArea.add(dashPanel,         P_DASHBOARD);
        contentArea.add(roomPanel,         P_ROOMS);
        contentArea.add(customerPanel,     P_CUSTOMERS);
        contentArea.add(reservPanel,       P_RESERVATIONS);
        contentArea.add(billingPanel,      P_BILLING);
        contentArea.add(archivedPanel,     P_ARCHIVED);
        contentArea.add(guestBookingPanel, P_GUEST_BOOKING); // NEW
        return contentArea;
    }

    public void showPanel(String name) {
        cardLayout.show(contentArea, name);
        for (NavBtn b : navBtns) b.setSelected(b.panelName.equals(name));
        switch (name) {
            case P_DASHBOARD:     dashPanel.refresh();         break;
            case P_ROOMS:         roomPanel.refresh();         break;
            case P_CUSTOMERS:     customerPanel.refresh();     break;
            case P_RESERVATIONS:  reservPanel.refresh();       break;
            case P_BILLING:       billingPanel.refresh();      break;
            case P_ARCHIVED:      archivedPanel.refresh();     break;
            case P_GUEST_BOOKING: guestBookingPanel.refresh(); break; // NEW
        }
    }

    // called by BillingPanel after every checkout 
    public void refreshAllPanels() {
        dashPanel.refresh();
        customerPanel.refresh();
        reservPanel.refresh();
        billingPanel.refresh();
        archivedPanel.refresh();
        roomPanel.refresh();
        guestBookingPanel.refresh(); // NEW
    }

    public void refreshRooms() {
        roomPanel.refresh();
    }

    private void doLogout() {
        if (RoyalComponents.confirm(this, "Log out of Martinez Royal Suite?")) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        }
    }

    // navigation button

    class NavBtn extends JPanel {
        final String panelName;
        private final String label, icon;
        private boolean selected = false, hovered = false;

        NavBtn(String icon, String label, String panelName) {
            this.icon = icon; this.label = label; this.panelName = panelName;
            setOpaque(false);
            setPreferredSize(new Dimension(210, 44));
            setMaximumSize(new Dimension(210, 44));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { showPanel(panelName); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                g2.setColor(new Color(212, 175, 55, 18));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.GOLD);
                g2.fillRect(0, 0, 2, getHeight());
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 5));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            g2.setColor(selected ? Theme.GOLD : Theme.TEXT_MUTED);
            g2.drawString(icon, 18, 27);

            g2.setFont(selected ? new Font("Arial", Font.BOLD, 12)
                                : Theme.FONT_NAV);
            g2.setColor(selected ? Theme.TEXT_GOLD : Theme.TEXT_MUTED);
            g2.drawString(label, 44, 28);

            g2.dispose();
        }

        public void setSelected(boolean s) { selected = s; repaint(); }
    }
}