/*Hello sir, I am fairly new to using jpanes and javax....admittedly I had to use AI to get these lines to work. I used the documentation and AI

We also apologize for passing late*/
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // ── Palette ──────────────────────────────────────────────────────
    static final Color C_BG      = new Color(0x0F0F13);
    static final Color C_SURFACE = new Color(0x17171E);
    static final Color C_CARD    = new Color(0x1E1E28);
    static final Color C_BORDER  = new Color(0x2A2A38);
    static final Color C_GOLD    = new Color(0xD4A853);
    static final Color C_GOLD2   = new Color(0xF0C97A);
    static final Color C_GREEN   = new Color(0x4ADE80);
    static final Color C_RED     = new Color(0xFF6B6B);
    static final Color C_TEXT    = new Color(0xF0F0F5);
    static final Color C_MUTED   = new Color(0x7878A0);
    static final Color C_FIELD   = new Color(0x12121A);

    // ── State ────────────────────────────────────────────────────────
    private AccountManager manager = new AccountManager();
    private Account loggedIn = null;
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private List<NavButton> navButtons = new ArrayList<>();
    private JLabel overviewBalLabel;
    private DefaultTableModel historyModel;

    // ════════════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new Main().start();
        });
    }

    private void start() {
        manager.loadFromFile();
        showAuthWindow();
    }

    // ════════════════════════════════════════════════════════════════
    //  AUTH WINDOW
    // ════════════════════════════════════════════════════════════════
    private void showAuthWindow() {
        // Increase frame size for better visibility
        mainFrame = buildFrame("NeoBank", 600, 780);

        JPanel root = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(getWidth() / 2f, 60), 260,
                    new float[]{0f, 1f},
                    new Color[]{new Color(212, 168, 83, 28), new Color(0, 0, 0, 0)}
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setLayout(new BorderLayout());
        root.setBackground(C_BG);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(52, 0, 30, 0));

        JLabel wordmark = new JLabel("NEOBANK", SwingConstants.CENTER);
        wordmark.setFont(new Font("Segoe UI", Font.BOLD, 48));
        wordmark.setForeground(C_GOLD);
        wordmark.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("YOUR MONEY, ELEVATED", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        tagline.setForeground(C_MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thin gold divider
        JPanel divider = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, new Color(0, 0, 0, 0),
                    getWidth() / 2f, 0, C_GOLD
                ));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(220, 1)); // slightly wider
        divider.setPreferredSize(new Dimension(220, 1));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(wordmark);
        header.add(Box.createVerticalStrut(6));
        header.add(tagline);
        header.add(Box.createVerticalStrut(22));
        header.add(divider);

        // Tab area
        JPanel tabArea = new JPanel();
        tabArea.setOpaque(false);
        tabArea.setLayout(new BorderLayout());
        tabArea.setBorder(new EmptyBorder(4, 32, 36, 32));
        tabArea.add(new AuthTabPanel(buildLoginPanel(), buildCreateAccountPanel()), BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(tabArea, BorderLayout.CENTER);

        mainFrame.setContentPane(root);
        mainFrame.setVisible(true);
    }

    // ── Login Panel ───────────────────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel() {
            public Dimension getPreferredSize() { return new Dimension(400, super.getPreferredSize().height); }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(28, 8, 28, 8));

        // Increase size of sub label
        JLabel sub = mkLabel("Sign in to your account", 16, Font.PLAIN, C_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField accField = modernField();
        accField.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Increase input font size
        accField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // increase height
        accField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField pinField = modernPassField();
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Increase input font size
        pinField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // increase height
        pinField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel errLabel = mkLabel("", 11, Font.PLAIN, C_RED);
        errLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GoldButton loginBtn = new GoldButton("SIGN IN", null);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String userInput = accField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            

            if (userInput.isEmpty() || pin.isEmpty()) { 
                errLabel.setText("Please fill in all fields."); 
                return; 
            }


            Account acc = manager.loginGUI(userInput, pin); 
            if (acc == null) {
                errLabel.setText("Invalid credentials or account not found."); 
                return; 
            }

            // Successful login 
            loggedIn = acc;
            mainFrame.dispose();
            showDashboard();
        });
        // --------------------------------------

        p.add(sub);
        p.add(Box.createVerticalStrut(26));

        // Use custom sizes for labels on this specific screen
        JLabel accLbl = mkLabel("ACCOUNT NUMBER OR FULL NAME", 12, Font.BOLD, C_MUTED);
        accLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pinLbl = mkLabel("PIN", 12, Font.BOLD, C_MUTED);
        pinLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(accLbl); p.add(Box.createVerticalStrut(6)); p.add(accField);
        p.add(Box.createVerticalStrut(14));
        p.add(pinLbl); p.add(Box.createVerticalStrut(6)); p.add(pinField);
        p.add(Box.createVerticalStrut(10));
        p.add(errLabel);
        p.add(Box.createVerticalStrut(22));
        p.add(loginBtn);

        return p;
    }

    // ── Create Account Panel ──────────────────────────────────────────
    private JPanel buildCreateAccountPanel() {
        JPanel p = new JPanel() {
            public Dimension getPreferredSize() { return new Dimension(400, super.getPreferredSize().height); }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 8, 20, 8));

        // Define larger inputs for this form
        JTextField nameField  = modernField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField bdField    = modernField();
        bdField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        bdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        bdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField pinField = modernPassField();
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pinField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pinField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField balField   = modernField();
        balField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        balField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        balField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel    = mkLabel("", 11, Font.PLAIN, C_GREEN);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GoldButton createBtn = new GoldButton("OPEN ACCOUNT", null);
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- OPTIMIZED CREATE ACCOUNT BUTTON LOGIC ---
        createBtn.addActionListener(e -> {
            // Read inputs from fields
            String name  = nameField.getText().trim();
            String bdate = bdField.getText().trim();
            String pin   = new String(pinField.getPassword()).trim();
            String balStr= balField.getText().trim();
            
            // GUI basic validation
            if (name.isEmpty() || bdate.isEmpty() || pin.isEmpty() || balStr.isEmpty()) {
                statusLabel.setForeground(C_RED); statusLabel.setText("All fields are required."); return; }
            
            // Sanitization: Convert Balance String to double
            double bal;
            try { bal = Double.parseDouble(balStr); }
            catch (NumberFormatException ex) {
                statusLabel.setForeground(C_RED); statusLabel.setText("Invalid deposit amount."); return; }

            // --- CALL THE LOGIC LAYER ---
            // Pass the sanitized data to the logic method created in AccountManager
            int newAccNum = manager.createAccountGUI(name, bdate, pin, bal);
            // ------------------------------------

            // --- Handle Logic Result ---
            if (newAccNum == -1) {
                // If the logic returns -1, we know a business rule was broken (eligibility, PIN format, etc.)
                statusLabel.setForeground(C_RED);
                statusLabel.setText("Failed to create account. Check eligibility, PIN format, and balance.");
            } else {
                // Success
                manager.saveToFile(); // Save the data (logic remains the same)
                
                // Clear the fields (GUI remains the same)
                nameField.setText(""); bdField.setText(""); pinField.setText(""); balField.setText("");
                statusLabel.setForeground(C_GREEN);
                statusLabel.setText("Account #" + newAccNum + " created!");
            }
        });
        // ----------------------------------------------

        // Use custom sizes for labels on this specific screen
        JLabel nameLbl = mkLabel("FULL NAME", 12, Font.BOLD, C_MUTED);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bdLbl   = mkLabel("BIRTHDATE (YYYY-MM-DD)", 12, Font.BOLD, C_MUTED);
        bdLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pinLbl  = mkLabel("PIN", 12, Font.BOLD, C_MUTED);
        pinLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel balLbl  = mkLabel("INITIAL DEPOSIT (PHP)", 12, Font.BOLD, C_MUTED);
        balLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(nameLbl); p.add(Box.createVerticalStrut(6)); p.add(nameField);
        p.add(Box.createVerticalStrut(12));
        p.add(bdLbl);   p.add(Box.createVerticalStrut(6)); p.add(bdField);
        p.add(Box.createVerticalStrut(12));
        p.add(pinLbl);  p.add(Box.createVerticalStrut(6)); p.add(pinField);
        p.add(Box.createVerticalStrut(12));
        p.add(balLbl);  p.add(Box.createVerticalStrut(6)); p.add(balField);
        p.add(Box.createVerticalStrut(10));
        p.add(statusLabel);
        p.add(Box.createVerticalStrut(18));
        p.add(createBtn);

        return p;
    }

    // ════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ════════════════════════════════════════════════════════════════
    private void showDashboard() {
        mainFrame = buildFrame("NeoBank — " + loggedIn.getName(), 880, 580);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_BG);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        mainFrame.setContentPane(root);
        mainFrame.setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(C_SURFACE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(C_BORDER);
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        sb.setOpaque(false);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(220, 0));

        // Wordmark
        JPanel logoArea = new JPanel();
        logoArea.setOpaque(false);
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBorder(new EmptyBorder(30, 22, 22, 22));
        logoArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel logo = mkLabel("NEOBANK", 18, Font.BOLD, C_GOLD);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = mkLabel("Personal Banking", 10, Font.PLAIN, C_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoArea.add(logo); logoArea.add(Box.createVerticalStrut(2)); logoArea.add(sub);

        // Avatar card
        GlassCard avatarCard = new GlassCard();
        avatarCard.setLayout(new BoxLayout(avatarCard, BoxLayout.Y_AXIS));
        avatarCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        avatarCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        avatarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        InitialsAvatar av = new InitialsAvatar(getInitials(loggedIn.getName()));
        av.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nameLabel = mkLabel(loggedIn.getName(), 13, Font.BOLD, C_TEXT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel accLabel  = mkLabel("ACC #" + loggedIn.getAccountNumber(), 10, Font.PLAIN, C_MUTED);
        accLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        avatarCard.add(av); avatarCard.add(Box.createVerticalStrut(10));
        avatarCard.add(nameLabel); avatarCard.add(Box.createVerticalStrut(2)); avatarCard.add(accLabel);

        JPanel avWrap = new JPanel();
        avWrap.setOpaque(false);
        avWrap.setLayout(new BoxLayout(avWrap, BoxLayout.Y_AXIS));
        avWrap.setBorder(new EmptyBorder(0, 12, 14, 12));
        avWrap.add(avatarCard);
        avWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nav label
        JLabel navLbl = mkLabel("MENU", 9, Font.BOLD, C_MUTED);
        navLbl.setBorder(new EmptyBorder(4, 22, 8, 22));
        navLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nav buttons
        String[][] items = {
            {"","Overview"},{"","Deposit"},{"","Withdraw"},{"","History"},{"","Report"}
        };
        navButtons.clear();
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String[] item : items) {
            NavButton btn = new NavButton(item[0], item[1]);
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, item[1]);
                setActiveNav(btn);
                if (item[1].equals("Overview")) refreshOverview();
                if (item[1].equals("History"))  refreshHistory();
            });
            navButtons.add(btn);
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(2));
        }
        navButtons.get(0).setActive(true);

        sb.add(logoArea);
        sb.add(avWrap);
        sb.add(navLbl);
        sb.add(navPanel);
        sb.add(Box.createVerticalGlue());

        // Logout
        JPanel sep = new JPanel() {
            protected void paintComponent(Graphics g) { g.setColor(C_BORDER); g.fillRect(0,0,getWidth(),1); }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(1, 1));

        JPanel logoutArea = new JPanel();
        logoutArea.setOpaque(false);
        logoutArea.setLayout(new BoxLayout(logoutArea, BoxLayout.Y_AXIS));
        logoutArea.setBorder(new EmptyBorder(0, 10, 20, 10));
        logoutArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutArea.add(Box.createVerticalStrut(10)); logoutArea.add(sep); logoutArea.add(Box.createVerticalStrut(12));

        NavButton logoutBtn = new NavButton("x", "Sign Out");
        logoutBtn.setDanger(true);
        logoutBtn.addActionListener(e -> { loggedIn = null; mainFrame.dispose(); showAuthWindow(); });
        logoutArea.add(logoutBtn);
        sb.add(logoutArea);
        return sb;
    }

    private JPanel buildMainContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(C_BG);
        contentPanel.add(buildOverviewPanel(), "Overview");
        contentPanel.add(buildDepositPanel(),  "Deposit");
        contentPanel.add(buildWithdrawPanel(), "Withdraw");
        contentPanel.add(buildHistoryPanel(),  "History");
        contentPanel.add(buildReportPanel(),   "Report");
        return contentPanel;
    }

    // ── Overview ──────────────────────────────────────────────────────
    private JPanel buildOverviewPanel() {
        JPanel p = bgPanel(); p.setLayout(new BorderLayout(0, 0));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Top row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel heading = mkLabel("Overview", 22, Font.BOLD, C_TEXT);
        JLabel dateLabel = mkLabel(LocalDate.now().toString(), 12, Font.PLAIN, C_MUTED);
        topRow.add(heading, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);

        // Balance hero
        GlassCard hero = new GlassCard() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(212, 168, 83, 18), getWidth(), getHeight(), new Color(240, 201, 122, 6)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel balCaption = mkLabel("TOTAL BALANCE", 9, Font.BOLD, C_MUTED);
        balCaption.setAlignmentX(Component.LEFT_ALIGNMENT);

        overviewBalLabel = mkLabel(formatMoney(loggedIn.getBalance()), 30, Font.BOLD, C_GOLD2);
        overviewBalLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        overviewBalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel accInfo = mkLabel("Account #" + loggedIn.getAccountNumber() + "  ·  " + loggedIn.getName(), 11, Font.PLAIN, C_MUTED);
        accInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(balCaption);
        hero.add(Box.createVerticalStrut(8));
        hero.add(overviewBalLabel);
        hero.add(Box.createVerticalStrut(6));
        hero.add(accInfo);

        // Quick action cards
        JPanel actions = new JPanel(new GridLayout(1, 3, 14, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(16, 0, 0, 0));
        actions.add(quickCard("↑", "Deposit",  "Add funds",    C_GREEN,  "Deposit"));
        actions.add(quickCard("↓", "Withdraw", "Take out cash", C_RED,   "Withdraw"));
        actions.add(quickCard("=", "History",  "View activity", C_GOLD,  "History"));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(hero);
        center.add(actions);

        p.add(topRow, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private GlassCard quickCard(String ico, String title, String sub, Color color, String target) {
        GlassCard c = new GlassCard();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(20, 20, 20, 20));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icoLabel = mkLabel(ico, 22, Font.BOLD, color);
        icoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titleLabel = mkLabel(title, 13, Font.BOLD, C_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subLabel = mkLabel(sub, 10, Font.PLAIN, C_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        c.add(icoLabel); c.add(Box.createVerticalStrut(12));
        c.add(titleLabel); c.add(Box.createVerticalStrut(3)); c.add(subLabel);

        c.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(contentPanel, target);
                navButtons.forEach(b -> b.setActive(b.getLabel().equals(target)));
            }
            public void mouseEntered(MouseEvent e) { c.setHovered(true); }
            public void mouseExited (MouseEvent e) { c.setHovered(false); }
        });
        return c;
    }

    private void refreshOverview() {
        if (overviewBalLabel != null)
            overviewBalLabel.setText(formatMoney(loggedIn.getBalance()));
    }

    // ── Deposit ───────────────────────────────────────────────────────
    private JPanel buildDepositPanel() {
        JPanel outer = bgPanel(); outer.setLayout(new BorderLayout());
        outer.setBorder(new EmptyBorder(32, 32, 32, 32));
        outer.add(mkLabel("Deposit Funds", 22, Font.BOLD, C_TEXT), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout()); center.setOpaque(false);
        GlassCard card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 38, 32, 38));
        card.setPreferredSize(new Dimension(400, 300));

        JLabel ico = mkLabel("↑", 30, Font.BOLD, C_GREEN); ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = mkLabel("Add money to your account", 12, Font.PLAIN, C_MUTED); sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField amtField = modernField(); amtField.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel amtLbl = mkLabel("AMOUNT (PHP)", 9, Font.BOLD, C_MUTED); amtLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel status = mkLabel("", 11, Font.PLAIN, C_GREEN); status.setAlignmentX(Component.CENTER_ALIGNMENT);
        GoldButton btn = new GoldButton("DEPOSIT FUNDS", C_GREEN); btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- OPTIMIZED DEPOSIT BUTTON LOGIC ---
        btn.addActionListener(e -> {
            try {
                // Read and sanitize input (GUI task)
                double amt = Double.parseDouble(amtField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

                loggedIn.getTransactionManager().deposit(loggedIn, amt); 
                // ------------------------------------

                // Finalize process (GUI task)
                manager.saveToFile(); 
                amtField.setText("");
                status.setForeground(C_GREEN);
                status.setText("✓ Deposited " + formatMoney(amt));
                refreshOverview();
            } catch (NumberFormatException ex) { status.setForeground(C_RED); status.setText("Enter a valid positive amount."); }
        });
        // ---------------------------------------

        card.add(ico); card.add(Box.createVerticalStrut(4));
        card.add(sub); card.add(Box.createVerticalStrut(28));
        card.add(amtLbl); card.add(Box.createVerticalStrut(6)); card.add(amtField);
        card.add(Box.createVerticalStrut(8)); card.add(status);
        card.add(Box.createVerticalStrut(20)); card.add(btn);
        center.add(card); outer.add(center, BorderLayout.CENTER);
        return outer;
    }

    // ── Withdraw ─────────────────────────────────────────────────────
    private JPanel buildWithdrawPanel() {
        JPanel outer = bgPanel(); outer.setLayout(new BorderLayout());
        outer.setBorder(new EmptyBorder(32, 32, 32, 32));
        outer.add(mkLabel("Withdraw Funds", 22, Font.BOLD, C_TEXT), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout()); center.setOpaque(false);
        GlassCard card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 38, 32, 38));
        card.setPreferredSize(new Dimension(400, 300));

        JLabel ico = mkLabel("↓", 30, Font.BOLD, C_RED); ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = mkLabel("Withdraw from your account", 12, Font.PLAIN, C_MUTED); sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField amtField = modernField(); amtField.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel amtLbl = mkLabel("AMOUNT (PHP)", 9, Font.BOLD, C_MUTED); amtLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel status = mkLabel("", 11, Font.PLAIN, C_RED); status.setAlignmentX(Component.CENTER_ALIGNMENT);
        GoldButton btn = new GoldButton("WITHDRAW FUNDS", C_RED); btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- OPTIMIZED WITHDRAW BUTTON LOGIC ---
        btn.addActionListener(e -> {
            try {

                double amt = Double.parseDouble(amtField.getText().trim());
                if (amt <= 0) throw new NumberFormatException();

 
                if (amt > loggedIn.getBalance()) { status.setForeground(C_RED); status.setText("Insufficient balance."); return; }


                loggedIn.getTransactionManager().withdraw(loggedIn, amt); 
                // ------------------------------------


                manager.saveToFile(); 
                amtField.setText("");
                status.setForeground(C_GREEN);
                status.setText("✓ Withdrew " + formatMoney(amt));
                refreshOverview();
            } catch (NumberFormatException ex) { status.setForeground(C_RED); status.setText("Enter a valid positive amount."); }
        });
        // ---------------------------------------

        card.add(ico); card.add(Box.createVerticalStrut(4));
        card.add(sub); card.add(Box.createVerticalStrut(28));
        card.add(amtLbl); card.add(Box.createVerticalStrut(6)); card.add(amtField);
        card.add(Box.createVerticalStrut(8)); card.add(status);
        card.add(Box.createVerticalStrut(20)); card.add(btn);
        center.add(card); outer.add(center, BorderLayout.CENTER);
        return outer;
    }

    // ── History ───────────────────────────────────────────────────────
    private JPanel buildHistoryPanel() {
        JPanel p = bgPanel(); p.setLayout(new BorderLayout(0, 16));
        p.setBorder(new EmptyBorder(32, 32, 32, 32));

        JPanel topRow = new JPanel(new BorderLayout()); topRow.setOpaque(false);
        topRow.add(mkLabel("Transaction History", 22, Font.BOLD, C_TEXT), BorderLayout.WEST);
        GoldButton ref = new GoldButton("↻  REFRESH", null);
        ref.addActionListener(e -> refreshHistory());
        topRow.add(ref, BorderLayout.EAST);

        String[] cols = {"DATE & TIME", "TYPE", "AMOUNT (PHP)"};
        historyModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(historyModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? C_CARD : C_SURFACE);
                c.setForeground(C_TEXT);
                if (col == 1) {
                    String v = (String) getValueAt(row, col);
                    if (v != null && v.contains("Deposit"))  c.setForeground(C_GREEN);
                    if (v != null && v.contains("Withdraw")) c.setForeground(C_RED);
                }
                if (col == 2) c.setFont(new Font("Consolas", Font.PLAIN, 13));
                return c;
            }
        };
        table.setBackground(C_CARD); table.setForeground(C_TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38); table.setGridColor(C_BORDER);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(212,168,83,60));
        table.setSelectionForeground(C_TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setBackground(C_SURFACE); th.setForeground(C_MUTED);
        th.setFont(new Font("Segoe UI", Font.BOLD, 10));
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,C_BORDER));
        th.setPreferredSize(new Dimension(0, 36));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_CARD);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            protected void configureScrollBarColors() { thumbColor = C_BORDER; trackColor = C_CARD; }
        });

        p.add(topRow, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        refreshHistory();
        return p;
    }

    private void refreshHistory() {
        if (historyModel == null) return;
        historyModel.setRowCount(0);
        TransactionManager tm = loggedIn.getTransactionManager();
        Transaction[] txns = tm.getTransactions();
        for (int i = tm.getCount() - 1; i >= 0; i--) {
            Transaction t = txns[i];
            historyModel.addRow(new Object[]{
                t.getDateTime(),
                t.getType().equalsIgnoreCase("deposit") ? "↑  Deposit" : "↓  Withdraw",
                String.format("%,.2f", t.getAmount())
            });
        }
        if (tm.getCount() == 0) historyModel.addRow(new Object[]{"—", "No transactions yet", "—"});
    }

    // ── Report ────────────────────────────────────────────────────────
    private JPanel buildReportPanel() {
        JPanel outer = bgPanel(); outer.setLayout(new BorderLayout());
        outer.setBorder(new EmptyBorder(32, 32, 32, 32));
        outer.add(mkLabel("Save Report", 22, Font.BOLD, C_TEXT), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout()); center.setOpaque(false);
        GlassCard card = new GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 44, 36, 44));
        card.setPreferredSize(new Dimension(400, 280));

        JLabel ico = mkLabel("*", 32, Font.BOLD, C_GOLD); ico.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = mkLabel("Bank Statement", 16, Font.BOLD, C_TEXT); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = mkLabel("Exports a report to your Downloads folder", 11, Font.PLAIN, C_MUTED); sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel status = mkLabel("", 11, Font.PLAIN, C_GREEN); status.setAlignmentX(Component.CENTER_ALIGNMENT);
        GoldButton btn = new GoldButton("EXPORT REPORT", null); btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> {
            saveReport(loggedIn);
            status.setForeground(C_GREEN);
            status.setText("✓ Saved to Downloads/BankReport.txt");
        });

        card.add(ico); card.add(Box.createVerticalStrut(8));
        card.add(title); card.add(Box.createVerticalStrut(6));
        card.add(sub); card.add(Box.createVerticalStrut(24));
        card.add(status); card.add(Box.createVerticalStrut(10));
        card.add(btn);
        center.add(card); outer.add(center, BorderLayout.CENTER);
        return outer;
    }

    // ════════════════════════════════════════════════════════════════
    //  CUSTOM COMPONENTS
    // ════════════════════════════════════════════════════════════════

    /** Glass-morphism card with hover highlight */
    static class GlassCard extends JPanel {
        private boolean hovered = false;
        GlassCard() { setOpaque(false); }
        void setHovered(boolean h) { hovered = h; repaint(); }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D rr = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(hovered ? new Color(0x252533) : C_CARD);
            g2.fill(rr);
            g2.setColor(hovered ? C_GOLD : C_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(rr);
            // top glass sheen
            g2.setPaint(new GradientPaint(0, 0, new Color(255,255,255,14), 0, getHeight()/3, new Color(255,255,255,0)));
            g2.fill(rr);
            g2.dispose();
        }
    }

    /** Circular initials badge */
    static class InitialsAvatar extends JPanel {
        private final String text;
        InitialsAvatar(String text) {
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(44, 44));
            setMaximumSize(new Dimension(44, 44));
            setMinimumSize(new Dimension(44, 44));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, C_GOLD, 44, 44, C_GOLD2));
            g2.fillOval(0, 0, 44, 44);
            g2.setColor(C_BG);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (44 - fm.stringWidth(text)) / 2, (44 - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    class NavButton extends JButton {
        private boolean active  = false;
        private boolean danger  = false;
        private final String lbl;
        private final int iconType; // 0=grid, 1=up, 2=down, 3=lines, 4=doc, 5=power

        NavButton(String icon, String label) {
            this.lbl = label;
            this.iconType = iconIndex(label);
            setText("        " + label); // left-pad to leave room for drawn icon
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(C_MUTED);
            setFocusPainted(false); setBorderPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setOpaque(false); setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (!active) setForeground(C_TEXT); }
                public void mouseExited (MouseEvent e) { if (!active) setForeground(danger ? C_RED : C_MUTED); }
            });
        }

        private int iconIndex(String label) {
            switch (label) {
                case "Overview": return 0;
                case "Deposit":  return 1;
                case "Withdraw": return 2;
                case "History":  return 3;
                case "Report":   return 4;
                case "Sign Out": return 5;
                default:         return 0;
            }
        }

        public String getLabel() { return lbl; }
        void setActive(boolean a) { active = a; setForeground(a ? C_GOLD : C_MUTED); repaint(); }
        void setDanger(boolean d) { danger = d; setForeground(C_RED); repaint(); }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(new Color(C_GOLD.getRed(), C_GOLD.getGreen(), C_GOLD.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_GOLD);
                g2.fillRoundRect(0, (getHeight() - 20) / 2, 3, 20, 2, 2);
            }
            // Draw icon
            Color iconColor = danger ? C_RED : (active ? C_GOLD : C_MUTED);
            g2.setColor(iconColor);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int ix = 20, iy = getHeight() / 2;
            drawIcon(g2, iconType, ix, iy);
            g2.dispose();
            super.paintComponent(g);
        }

        private void drawIcon(Graphics2D g2, int type, int cx, int cy) {
            int s = 7; // half-size
            switch (type) {
                case 0: // Overview: small grid 2x2
                    g2.drawRect(cx - s, cy - s, s - 1, s - 1);
                    g2.drawRect(cx + 1, cy - s, s - 1, s - 1);
                    g2.drawRect(cx - s, cy + 1, s - 1, s - 1);
                    g2.drawRect(cx + 1, cy + 1, s - 1, s - 1);
                    break;
                case 1: // Deposit: up arrow
                    g2.drawLine(cx, cy - s, cx, cy + s);
                    g2.drawLine(cx, cy - s, cx - s + 2, cy - 1);
                    g2.drawLine(cx, cy - s, cx + s - 2, cy - 1);
                    break;
                case 2: // Withdraw: down arrow
                    g2.drawLine(cx, cy + s, cx, cy - s);
                    g2.drawLine(cx, cy + s, cx - s + 2, cy + 1);
                    g2.drawLine(cx, cy + s, cx + s - 2, cy + 1);
                    break;
                case 3: // History: 3 horizontal lines
                    g2.drawLine(cx - s, cy - 4, cx + s, cy - 4);
                    g2.drawLine(cx - s, cy,     cx + s, cy);
                    g2.drawLine(cx - s, cy + 4, cx + s, cy + 4);
                    break;
                case 4: // Report: document
                    g2.drawRoundRect(cx - s + 1, cy - s, s * 2 - 2, s * 2, 2, 2);
                    g2.drawLine(cx - s + 4, cy - 3, cx + s - 4, cy - 3);
                    g2.drawLine(cx - s + 4, cy + 1, cx + s - 4, cy + 1);
                    break;
                case 5: // Sign Out: door with arrow
                    // Door rectangle
                    g2.drawRoundRect(cx - s, cy - s, s * 2, s * 2, 2, 2);
                    // Arrow pointing right (exit)
                    g2.drawLine(cx - 2, cy, cx + s - 2, cy);
                    g2.drawLine(cx + s - 5, cy - 3, cx + s - 2, cy);
                    g2.drawLine(cx + s - 5, cy + 3, cx + s - 2, cy);
                    break;
            }
        }
    }

    /** Gold gradient button */
    static class GoldButton extends JButton {
        private final Color overrideBg;
        GoldButton(String text, Color bg) {
            super(text);
            this.overrideBg = bg;
            setFont(new Font("Segoe UI", Font.BOLD, 14)); // increased size
            setForeground(bg == null ? C_BG : Color.WHITE);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setBorder(new EmptyBorder(12, 30, 12, 30));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Paint p = overrideBg != null
                ? new GradientPaint(0, 0, overrideBg.brighter(), getWidth(), getHeight(), overrideBg.darker())
                : new GradientPaint(0, 0, C_GOLD2, getWidth(), getHeight(), C_GOLD);
            g2.setPaint(p);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Two-tab switcher used on the auth screen */
    static class AuthTabPanel extends JPanel {
        private int selected = 0;
        private final JPanel[] panels;
        private final String[] labels = {"Sign In", "Open Account"};
        private final JPanel holder;

        AuthTabPanel(JPanel login, JPanel create) {
            panels = new JPanel[]{login, create};
            setOpaque(false);
            setLayout(new BorderLayout(0, 12));

            // Tab bar
            JPanel bar = new JPanel(new GridLayout(1, 2, 0, 0)) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(C_SURFACE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
            };
            bar.setOpaque(false);
            bar.setPreferredSize(new Dimension(1, 44));
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            bar.setBorder(new EmptyBorder(4, 4, 4, 4));

            for (int i = 0; i < labels.length; i++) {
                final int idx = i;
                JButton tb = new JButton(labels[i]) {
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (selected == idx) {
                            g2.setPaint(new GradientPaint(0, 0, C_GOLD2, getWidth(), getHeight(), C_GOLD));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                            setForeground(C_BG);
                        } else {
                            setForeground(C_MUTED);
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                tb.setFont(new Font("Segoe UI", Font.BOLD, 12));
                tb.setFocusPainted(false); tb.setBorderPainted(false);
                tb.setContentAreaFilled(false); tb.setOpaque(false);
                tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                tb.addActionListener(e -> { selected = idx; swap(); bar.repaint(); });
                bar.add(tb);
            }

            holder = new GlassCard();
            holder.setLayout(new GridBagLayout());

            add(bar, BorderLayout.NORTH);
            add(holder, BorderLayout.CENTER);
            swap();
        }

        private void swap() {
            holder.removeAll();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            holder.add(panels[selected], gbc);
            holder.revalidate(); holder.repaint();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════
    private JFrame buildFrame(String title, int w, int h) {
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(w, h); f.setMinimumSize(new Dimension(w, h));
        f.setLocationRelativeTo(null);
        return f;
    }

    private JPanel bgPanel() { JPanel p = new JPanel(); p.setBackground(C_BG); return p; }

    private JLabel mkLabel(String t, int sz, int style, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", style, sz));
        l.setForeground(c);
        return l;
    }

    private JTextField modernField() {
        JTextField tf = new JTextField(20) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false); tf.setBackground(C_FIELD);
        tf.setForeground(C_TEXT); tf.setCaretColor(C_GOLD);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(new EmptyBorder(10, 14, 10, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JPasswordField modernPassField() {
        JPasswordField pf = new JPasswordField(20) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FIELD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pf.setOpaque(false); pf.setBackground(C_FIELD);
        pf.setForeground(C_TEXT); pf.setCaretColor(C_GOLD);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setBorder(new EmptyBorder(10, 14, 10, 14));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    private void setActiveNav(NavButton target) {
        navButtons.forEach(b -> b.setActive(b == target));
    }

    private String getInitials(String name) {
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, Math.min(2, p[0].length())).toUpperCase();
        return ("" + p[0].charAt(0) + p[p.length-1].charAt(0)).toUpperCase();
    }

    private String formatMoney(double amount) { return String.format("PHP %,.2f", amount); }

    /**
     * Cross-platform getter for the user's Downloads folder.
     */
    private static String getDownloadPath() {
        String userHome = System.getProperty("user.home");
        // Combine home + Downloads + filename using platform-specific separator
        return userHome + File.separator + "Downloads" + File.separator + "BankReport.txt";
    }

    // save report
    private void saveReport(Account acc) {
        // Use the dynamic path
        String reportPath = getDownloadPath();

        try (PrintWriter pw = new PrintWriter(new FileWriter(reportPath))) {
            pw.println("==============================");
            pw.println("         BANK REPORT          ");
            pw.println("==============================");
            pw.println("Account Number : " + acc.getAccountNumber());
            pw.println("Name           : " + acc.getName());
            pw.printf( "Balance        : PHP %,.2f%n", acc.getBalance());
            pw.println("\nTRANSACTION HISTORY");
            pw.println("------------------------------");
            TransactionManager tm = acc.getTransactionManager();
            if (tm.getCount() == 0) {
                pw.println("No transactions available.");
            } else {
                Transaction[] txns = tm.getTransactions();
                for (int i = 0; i < tm.getCount(); i++) pw.println(txns[i]);
            }
            pw.println("\n==============================");
            pw.println("       END OF REPORT          ");
            pw.println("==============================");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error saving report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}