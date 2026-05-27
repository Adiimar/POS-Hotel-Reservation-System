import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class RoyalComponents {

    // ── Buttons ───────────────────────────────────────────────────────

    public static JButton makeGoldBtn(String text) {
        return flatBtn(text, Theme.GOLD, Theme.GOLD_BRIGHT, Theme.GOLD_DARK, Theme.BG_DARKEST);
    }

    public static JButton makeDangerBtn(String text) {
        return flatBtn(text, Theme.DANGER,
                new Color(225, 80, 80), new Color(160, 45, 45), Theme.TEXT_WHITE);
    }

    public static JButton makeSuccessBtn(String text) {
        return flatBtn(text, Theme.SUCCESS,
                new Color(65, 215, 130), new Color(35, 150, 80), Theme.BG_DARKEST);
    }

    public static JButton makeRoyalBtn(String text) {
        return flatBtn(text,
                new Color(30,  60, 160),
                new Color(45,  85, 200),
                new Color(18,  40, 110),
                Theme.GOLD);
    }

    public static JButton makeOutlineBtn(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(212, 175, 55, 30));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(212, 175, 55, 15));
                } else {
                    g2.setColor(new Color(0, 0, 0, 0));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.GOLD_DIM);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setFont(Theme.FONT_BTN);
                g2.setColor(Theme.GOLD_DIM);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        applyDefaults(btn);
        return btn;
    }

    private static JButton flatBtn(String text,
            Color normal, Color hover, Color pressed, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getModel().isPressed()  ? pressed
                          : getModel().isRollover() ? hover
                          : normal);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setFont(Theme.FONT_BTN);
                g2.setColor(fg);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        applyDefaults(btn);
        return btn;
    }

    private static void applyDefaults(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // text fields 

    public static JTextField makeField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    public static JPasswordField makePasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        styleField(f);
        return f;
    }

    public static void styleField(JTextField f) {
        f.setBackground(Theme.BG_INPUT);
        f.setForeground(Theme.TEXT_CREAM);
        f.setCaretColor(Theme.GOLD);
        f.setFont(Theme.FONT_TEXT);
        Border normal  = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 0, 6, 8));
        Border focused = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.GOLD_DIM),
                BorderFactory.createEmptyBorder(6, 0, 5, 8));
        f.setBorder(normal);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBorder(focused); }
            @Override public void focusLost(FocusEvent e)   { f.setBorder(normal);  }
        });
    }

    // combo boxes

    public static <T> JComboBox<T> makeCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(Theme.BG_INPUT);
        cb.setForeground(Theme.TEXT_CREAM);
        cb.setFont(Theme.FONT_TEXT);
        cb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected, boolean hasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
                setBackground(isSelected ? Theme.TBL_SELECT : Theme.BG_INPUT);
                setForeground(Theme.TEXT_CREAM);
                setFont(Theme.FONT_TEXT);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cb;
    }

    // labels 

    public static JLabel makeTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TITLE);
        l.setForeground(Theme.TEXT_GOLD);
        return l;
    }

    public static JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_CREAM);
        return l;
    }

    public static JLabel makeSmall(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    // Card panel

    public static JPanel makeCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
    }

    // table 

    public static JTable makeTable(DefaultTableModel model, boolean useStatusRenderer) {
        JTable table = new JTable(model) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setBackground(Theme.TBL_ODD);
        table.setForeground(Theme.TEXT_CREAM);
        table.setGridColor(Theme.TBL_GRID);
        table.setFont(Theme.FONT_TABLE);
        table.setRowHeight(30);
        table.setSelectionBackground(Theme.TBL_SELECT);
        table.setSelectionForeground(Theme.TEXT_WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(Theme.TBL_HEADER);
        hdr.setForeground(Theme.TEXT_MUTED);
        hdr.setFont(Theme.FONT_TBLHDR);
        hdr.setReorderingAllowed(false);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GOLD));

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Theme.TBL_ODD : Theme.TBL_EVEN);
                    setForeground(Theme.TEXT_CREAM);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        for (int i = 0; i < model.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);

        if (useStatusRenderer && model.getColumnCount() > 0)
            table.getColumnModel().getColumn(model.getColumnCount() - 1)
                    .setCellRenderer(new StatusCellRenderer());

        return table;
    }

    public static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            String s = (val != null) ? val.toString() : "";
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Arial", Font.BOLD, 10));
            setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            if (!sel) {
                switch (s) {
                    case "Active":
                    case "Available":
                        setBackground(new Color(52, 199, 115, 30));
                        setForeground(Theme.SUCCESS);
                        break;
                    case "Checked Out":
                        setBackground(new Color(80, 160, 230, 30));
                        setForeground(Theme.INFO);
                        break;
                    case "Cancelled":
                        setBackground(new Color(210, 65, 65, 30));
                        setForeground(Theme.DANGER);
                        break;
                    case "Occupied":
                        setBackground(new Color(220, 140, 40, 30));
                        setForeground(Theme.WARNING);
                        break;
                    default:
                        setBackground(row % 2 == 0 ? Theme.TBL_ODD : Theme.TBL_EVEN);
                        setForeground(Theme.TEXT_CREAM);
                }
            }
            return this;
        }
    }

    // scroll pane 

    public static JScrollPane makeScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Theme.BORDER));
        sp.setBackground(Theme.BG_MAIN);
        sp.getViewport().setBackground(Theme.TBL_ODD);
        sp.getVerticalScrollBar().setBackground(Theme.BG_MAIN);
        return sp;
    }

    // section header 

    public static JPanel makeSectionHeader(String icon, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_HEADER);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JPanel accent = new JPanel();
        accent.setBackground(Theme.GOLD);
        accent.setPreferredSize(new Dimension(3, 22));

        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_TITLE);
        lbl.setForeground(Theme.TEXT_WHITE);

        left.add(accent);
        left.add(lbl);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    // dialogs 

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}