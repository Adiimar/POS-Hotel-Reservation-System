import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends JFrame {
    
    private static final Map<String, String> ACCOUNTS = new HashMap<>();
    static {
        ACCOUNTS.put("Karl Martinez", "adimar");     // Account 1
        ACCOUNTS.put("admin",   "admin123");    // Account 2
        ACCOUNTS.put("Bernabe Cabuhayan",   "serberns");   // Account 3
    }

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;

    public LoginScreen() {
        setTitle("Martinez Royal Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(900, 580);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARKEST);

        MouseAdapter drag = new MouseAdapter() {
            private Point origin;
            @Override public void mousePressed(MouseEvent e)  { origin = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e)  {
                if (origin != null) {
                    setLocation(
                        getLocation().x + e.getX() - origin.x,
                        getLocation().y + e.getY() - origin.y);
                }
            }
        };
        root.addMouseListener(drag);
        root.addMouseMotionListener(drag);

        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildLeft(),    BorderLayout.WEST);
        root.add(buildRight(),   BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        bar.setBackground(Theme.BG_HEADER);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel brand = new JLabel("M | Martinez Royal Suite");
        brand.setFont(new Font("Georgia", Font.PLAIN, 12));
        brand.setForeground(Theme.TEXT_CREAM);

        JButton close = new JButton("X");
        close.setFont(Theme.FONT_SMALL);
        close.setForeground(Theme.TEXT_MUTED);
        close.setContentAreaFilled(false);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { close.setForeground(Theme.DANGER); }
            @Override public void mouseExited(MouseEvent e)  { close.setForeground(Theme.TEXT_MUTED); }
        });
        close.addActionListener(e -> System.exit(0));

        bar.add(Box.createHorizontalGlue());
        bar.add(brand);
        bar.add(Box.createHorizontalGlue());
        bar.add(close);

        return bar;
    }

    // left: branding column 
    private JPanel buildLeft() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_DARKEST);
        p.setPreferredSize(new Dimension(420, 0));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        p.add(Box.createVerticalStrut(60));

        JLabel crown = new JLabel(Theme.CROWN);
        crown.setFont(new Font("Serif", Font.PLAIN, 64));
        crown.setForeground(Theme.GOLD);
        crown.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(crown);

        p.add(Box.createVerticalStrut(12));

        JLabel line1 = new JLabel("Martinez");
        line1.setFont(new Font("Georgia", Font.BOLD, 42));
        line1.setForeground(Theme.TEXT_WHITE);
        line1.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(line1);

        JLabel line2 = new JLabel("Royal Suite");
        line2.setFont(new Font("Georgia", Font.ITALIC, 30));
        line2.setForeground(Theme.TEXT_WHITE);
        line2.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(line2);

        p.add(Box.createVerticalStrut(14));

        JPanel rule = new JPanel();
        rule.setBackground(Theme.GOLD_DIM);
        rule.setMaximumSize(new Dimension(280, 1));
        rule.setPreferredSize(new Dimension(280, 1));
        rule.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(rule);

        p.add(Box.createVerticalStrut(16));

        JLabel tag1 = new JLabel("Curated Comfort.");
        tag1.setFont(new Font("Georgia", Font.ITALIC, 13));
        tag1.setForeground(Theme.TEXT_MUTED);
        tag1.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(tag1);

        JLabel tag2 = new JLabel("Crowned in Elegance.");
        tag2.setFont(new Font("Georgia", Font.ITALIC, 13));
        tag2.setForeground(Theme.TEXT_MUTED);
        tag2.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(tag2);

        p.add(Box.createVerticalGlue());

        return p;
    }

    // right: login form 
    private JPanel buildRight() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Theme.BG_DARKEST);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel form = new JPanel();
        form.setBackground(Theme.BG_DARKEST);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(310, 340));

        JLabel heading = new JLabel("Sign In");
        heading.setFont(new Font("Georgia", Font.BOLD, 24));
        heading.setForeground(Theme.TEXT_WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Enter your credentials to continue");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // username field
        JLabel uLbl = cap("USERNAME");
        usernameField = RoyalComponents.makeField(20);
        usernameField.setBackground(Theme.BG_DARKEST);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        usernameField.setPreferredSize(new Dimension(310, 28));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setHorizontalAlignment(JTextField.LEFT);
        usernameField.setMargin(new Insets(0, 0, 0, 0));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.GOLD),
            BorderFactory.createEmptyBorder(2, 0, 0, 0)
        ));

        // password field
        JLabel pLbl = cap("PASSWORD");
        passwordField = RoyalComponents.makePasswordField(20);
        passwordField.setBackground(Theme.BG_DARKEST);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        passwordField.setPreferredSize(new Dimension(310, 28));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMargin(new Insets(0, 0, 0, 0));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.GOLD),
            BorderFactory.createEmptyBorder(2, 0, 0, 0)
        ));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton signIn = new JButton("SIGN IN") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getModel().isPressed()  ? Theme.GOLD_DARK
                          : getModel().isRollover() ? Theme.GOLD_BRIGHT
                          : Theme.GOLD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setFont(Theme.FONT_BTN);
                g2.setColor(Theme.BG_DARKEST);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        signIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signIn.setContentAreaFilled(false);
        signIn.setBorderPainted(false);
        signIn.setFocusPainted(false);
        signIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        signIn.addActionListener(e -> doLogin());

        passwordField.addActionListener(e -> doLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        JLabel hint = new JLabel("Default: admin / admin123");
        hint.setFont(Theme.FONT_SMALL);
        hint.setForeground(Theme.TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(heading);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(30));
        form.add(uLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(usernameField);
        form.add(Box.createVerticalStrut(20));
        form.add(pLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(6));
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(signIn);
        form.add(Box.createVerticalStrut(14));
        form.add(hint);

        outer.add(form, gbc);
        return outer;
    }

    private JLabel cap(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 10));
        l.setForeground(Theme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void doLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter your username and password.");
            return;
        }
        // check against all accounts 
        if (ACCOUNTS.containsKey(user) && ACCOUNTS.get(user).equals(pass)) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
        } else {
            statusLabel.setText("Incorrect username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}