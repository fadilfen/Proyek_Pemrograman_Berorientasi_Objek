package tubes_a11;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MindFull – Mental Wellbeing App
 * Redesigned UI: Dark-glass futuristic aesthetic
 *   – Deep navy/midnight background
 *   – Glassmorphism cards with frosted translucency
 *   – Vibrant cyan/teal accent system
 *   – Soft aurora gradient effects in header areas
 *   – Rounded, airy form inputs with subtle glow on focus
 *   – Smooth sidebar with icon-aligned nav items
 */
public class MentalWellbeingApp extends JFrame {

    private User user;
    private String currentUsername;
    private JPanel tokenLabel, scoreLabel, screenTimeLabel;
    private DefaultTableModel activityModel;
    private JPanel contentPanel;

    // ── Palette ─────────────────────────────────────────────────────────────
    // Dark base
    private static final Color BG_DEEP      = new Color(10,  14,  30);   // near-black navy
    private static final Color BG_MID       = new Color(15,  21,  45);   // panel bg
    private static final Color BG_CARD      = new Color(22,  32,  63);   // card bg
    private static final Color BG_CARD2     = new Color(18,  27,  52);   // alt card

    // Sidebar
    private static final Color SIDEBAR_BG   = new Color(8,   12,  24);
    private static final Color SIDEBAR_SEL  = new Color(0,  195, 190);   // selected highlight

    // Accents
    private static final Color ACCENT_CYAN  = new Color(0,  210, 200);   // primary cyan
    private static final Color ACCENT_TEAL  = new Color(0,  175, 170);
    private static final Color ACCENT_BLUE  = new Color(70, 130, 255);   // secondary blue
    private static final Color ACCENT_MINT  = new Color(80, 235, 200);
    private static final Color ACCENT_PINK  = new Color(255, 90, 160);   // danger/alert

    // Text
    private static final Color TEXT_PRIMARY = new Color(220, 235, 255);
    private static final Color TEXT_SEC     = new Color(130, 155, 200);
    private static final Color TEXT_MUTED   = new Color(70,  95, 145);

    // Success / warning / danger
    private static final Color CLR_SUCCESS  = new Color(50,  215, 140);
    private static final Color CLR_WARN     = new Color(255, 185,  50);
    private static final Color CLR_DANGER   = new Color(255,  75, 110);

    // ── Typography ───────────────────────────────────────────────────────────
    private static final Font F_DISPLAY   = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font F_HEADING   = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font F_SUBHEAD   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_CAPTION   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_MONO      = new Font("Consolas",  Font.BOLD,  22);

    // ── State ────────────────────────────────────────────────────────────────
    private JButton activeNavBtn = null;

    // ════════════════════════════════════════════════════════════════════════
    public MentalWellbeingApp() {
        loginScreen();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOGIN / REGISTER
    // ════════════════════════════════════════════════════════════════════════
    private void loginScreen() {
        JFrame loginFrame = new JFrame("MindFull");
        loginFrame.setSize(900, 560);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        loginFrame.setUndecorated(false);

        // Root: split left (branding) + right (form)
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(BG_DEEP);

        // ── Left branding panel ──────────────────────────────────────────
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Aurora gradient
                GradientPaint gp = new GradientPaint(
                    0, 0,            new Color(0, 60, 100),
                    getWidth(), getHeight(), new Color(5, 20, 55));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Glowing orb
                RadialGradientPaint orb = new RadialGradientPaint(
                    new Point2D.Float(getWidth() / 2f, getHeight() / 2f),
                    260,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0, 200, 190, 60), new Color(0, 0, 0, 0)});
                g2.setPaint(orb);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        brand.setOpaque(false);

        JLabel logo   = new JLabel("🧠");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel appName = new JLabel("MindFull");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 38));
        appName.setForeground(ACCENT_CYAN);

        JLabel tagline  = new JLabel("<html><center>Track your digital habits.<br>Nurture your mental wellbeing.</center></html>");
        tagline.setFont(F_BODY);
        tagline.setForeground(TEXT_SEC);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = GridBagConstraints.RELATIVE;
        bc.insets = new Insets(8, 30, 8, 30);
        bc.anchor = GridBagConstraints.CENTER;
        brand.add(logo,    bc);
        brand.add(appName, bc);
        brand.add(tagline, bc);

        // ── Right form panel ─────────────────────────────────────────────
        JPanel formSide = new JPanel(new GridBagLayout());
        formSide.setBackground(BG_MID);

        // Card container
        JPanel card = createGlassCard(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 400));

        // Login form
        JPanel loginForm  = buildLoginForm(loginFrame, card);
        // Register form
        JPanel regForm    = buildRegisterForm(loginFrame, card, loginForm);
        loginForm.putClientProperty("regForm", regForm);

        card.add(loginForm);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.NONE;
        fc.anchor = GridBagConstraints.CENTER;
        formSide.add(card, fc);

        root.add(brand);
        root.add(formSide);
        loginFrame.setContentPane(root);
        loginFrame.setVisible(true);
    }

    private JPanel buildLoginForm(JFrame loginFrame, JPanel card) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = formGbc();

        JLabel title = label("Welcome back", F_HEADING, ACCENT_CYAN);
        JLabel sub   = label("Sign in to continue", F_CAPTION, TEXT_SEC);

        JTextField   usernameField  = styledField("Username");
        JPasswordField passField    = styledPassField("Password");
        JButton       loginBtn      = accentButton("Sign In", ACCENT_CYAN);
        JButton       toRegBtn      = linkButton("Don't have an account? Register");

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.insets = new Insets(0,0,4,0);
        p.add(title, g);
        g.gridy = 1; g.insets = new Insets(0,0,24,0);
        p.add(sub, g);
        g.gridy = 2; g.insets = new Insets(0,0,10,0);
        p.add(usernameField, g);
        g.gridy = 3; g.insets = new Insets(0,0,20,0);
        p.add(passField, g);
        g.gridy = 4; g.insets = new Insets(0,0,10,0);
        p.add(loginBtn, g);
        g.gridy = 5; g.insets = new Insets(4,0,0,0);
        p.add(toRegBtn, g);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passField.getPassword());
            String namaLengkap = UserManager.login(username, password);
            if (namaLengkap != null) {
                currentUsername = username;
                user = new User(1, namaLengkap, 50);
                user.setUsername(username);
                loginFrame.dispose();
                initUI();
            } else {
                showToast(loginFrame, "Username atau password salah!", CLR_DANGER);
            }
        });

        toRegBtn.addActionListener(e -> {
            card.removeAll();
            JPanel reg = (JPanel) p.getClientProperty("regForm");
            if (reg == null) reg = buildRegisterForm(loginFrame, card, p);
            card.add(reg);
            card.revalidate(); card.repaint();
        });

        return p;
    }

    private JPanel buildRegisterForm(JFrame loginFrame, JPanel card, JPanel loginForm) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = formGbc();

        JLabel title = label("Create Account", F_HEADING, ACCENT_CYAN);
        JLabel sub   = label("Join MindFull today", F_CAPTION, TEXT_SEC);

        JTextField     namaField = styledField("Nama Lengkap");
        JTextField     usrField  = styledField("Username");
        JPasswordField pwdField  = styledPassField("Password");
        JButton        regBtn    = accentButton("Register", CLR_SUCCESS);
        JButton        toLoginBtn = linkButton("Already have an account? Sign in");

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.insets = new Insets(0,0,4,0);
        p.add(title, g);
        g.gridy = 1; g.insets = new Insets(0,0,18,0);
        p.add(sub, g);
        g.gridy = 2; g.insets = new Insets(0,0,10,0);
        p.add(namaField, g);
        g.gridy = 3;
        p.add(usrField, g);
        g.gridy = 4; g.insets = new Insets(0,0,20,0);
        p.add(pwdField, g);
        g.gridy = 5; g.insets = new Insets(0,0,10,0);
        p.add(regBtn, g);
        g.gridy = 6; g.insets = new Insets(4,0,0,0);
        p.add(toLoginBtn, g);

        regBtn.addActionListener(e -> {
            String nama  = namaField.getText().trim();
            String uname = usrField.getText().trim();
            String pwd   = new String(pwdField.getPassword());
            if (nama.isEmpty() || uname.isEmpty() || pwd.isEmpty()) {
                showToast(loginFrame, "Semua field harus diisi!", CLR_WARN);
                return;
            }
            if (UserManager.register(uname, pwd, nama)) {
                showToast(loginFrame, "Registrasi berhasil! Silakan login.", CLR_SUCCESS);
                namaField.setText(""); usrField.setText(""); pwdField.setText("");
                card.removeAll();
                card.add(loginForm);
                card.revalidate(); card.repaint();
            } else {
                showToast(loginFrame, "Username sudah terdaftar!", CLR_DANGER);
            }
        });

        toLoginBtn.addActionListener(e -> {
            card.removeAll();
            card.add(loginForm);
            card.revalidate(); card.repaint();
        });

        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MAIN WINDOW SKELETON
    // ════════════════════════════════════════════════════════════════════════
    private void initUI() {
        setTitle("MindFull – Mental Wellbeing");
        setSize(1200, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DEEP);

        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_DEEP);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        showHomePage();
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));

        // Top: logo
        JPanel logoBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 18));
        logoBar.setOpaque(false);
        JLabel logoTxt = new JLabel("MindFull");
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoTxt.setForeground(ACCENT_CYAN);
        logoBar.add(logoTxt);
        sidebar.add(logoBar, BorderLayout.NORTH);

        // Mid: avatar + nav
        JPanel mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setOpaque(false);
        mid.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Avatar block
        JPanel avatarBlock = new JPanel();
        avatarBlock.setLayout(new BoxLayout(avatarBlock, BoxLayout.X_AXIS));
        avatarBlock.setOpaque(false);
        avatarBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        avatarBlock.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel ava = createAvatarCircle(user.getNamaUser(), 42);
        avatarBlock.add(ava);
        avatarBlock.add(Box.createRigidArea(new Dimension(12, 0)));

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);
        JLabel nameLbl  = new JLabel(user.getNamaUser());
        nameLbl.setFont(F_SUBHEAD);
        nameLbl.setForeground(TEXT_PRIMARY);
        JLabel roleLbl  = new JLabel("Member");
        roleLbl.setFont(F_CAPTION);
        roleLbl.setForeground(TEXT_MUTED);
        nameBlock.add(nameLbl);
        nameBlock.add(roleLbl);
        avatarBlock.add(nameBlock);

        mid.add(avatarBlock);
        mid.add(Box.createRigidArea(new Dimension(0, 16)));

        // Divider
        mid.add(sidebarDivider());
        mid.add(Box.createRigidArea(new Dimension(0, 16)));

        // Nav label
        JLabel navLbl = new JLabel("NAVIGATION");
        navLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLbl.setForeground(TEXT_MUTED);
        navLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLbl.setBorder(new EmptyBorder(0, 6, 8, 0));
        mid.add(navLbl);

        // Nav items: (label, emoji)
        String[][] navItems = {
            {"Dashboard",      "🏠"},
            {"Activity Tracker","📊"},
            {"Top Up Balance", "💳"},
            {"Health Report",  "📋"},
            {"Profile",        "👤"},
        };
        Runnable[] actions = {
            this::showHomePage,
            this::showActivityPage,
            this::showTopUpPage,
            this::showReportPage,
            this::showProfilePage
        };

        for (int i = 0; i < navItems.length; i++) {
            JButton btn = navButton(navItems[i][1] + "  " + navItems[i][0]);
            final Runnable action = actions[i];
            final JButton thisBtn = btn;
            btn.addActionListener(e -> {
                setActiveNav(thisBtn);
                action.run();
            });
            if (i == 0) { setActiveNav(btn); }
            mid.add(btn);
            mid.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        sidebar.add(mid, BorderLayout.CENTER);

        // Bottom: logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        bottomPanel.setOpaque(false);
        JButton logoutBtn = dangerButton("⬅  Logout");
        logoutBtn.setMaximumSize(new Dimension(180, 38));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new MentalWellbeingApp();
            }
        });
        bottomPanel.add(logoutBtn);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(new Color(0,0,0,0));
            activeNavBtn.setForeground(TEXT_SEC);
        }
        activeNavBtn = btn;
        btn.setBackground(new Color(0, 210, 200, 25));
        btn.setForeground(ACCENT_CYAN);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PAGE: HOME / DASHBOARD
    // ════════════════════════════════════════════════════════════════════════
    private void showHomePage() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(30, 32, 30, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = label("Dashboard", F_DISPLAY, TEXT_PRIMARY);
        JLabel date  = label(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")), F_CAPTION, TEXT_MUTED);
        header.add(title, BorderLayout.NORTH);
        header.add(date,  BorderLayout.SOUTH);
        page.add(header, BorderLayout.NORTH);

        // Stats row: 3 cards side by side
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 18, 0));
        cardsRow.setOpaque(false);
        cardsRow.setBorder(new EmptyBorder(28, 0, 28, 0));

        cardsRow.add(statCard("Tokens",       String.valueOf(user.getToken()),
                              "Available balance", "🪙", ACCENT_CYAN));
        cardsRow.add(statCard("Wellness Score", String.valueOf(user.hitungScoreKesehatan()),
                              "Overall health index", "💚", CLR_SUCCESS));
        cardsRow.add(statCard("Screen Time",   user.hitungTotalScreenTime() + " min",
                              "Total today",  "⏱", ACCENT_BLUE));

        page.add(cardsRow, BorderLayout.CENTER);

        // Bottom: quick-action pills
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quickActions.setOpaque(false);
        JLabel qaLabel = label("Quick actions:", F_CAPTION, TEXT_MUTED);
        quickActions.add(qaLabel);

        JButton qaAct    = pillButton("+ Log Activity");
        JButton qaTopup  = pillButton("+ Top Up");
        JButton qaReport = pillButton("View Report");
        qaAct.addActionListener(e   -> showActivityPage());
        qaTopup.addActionListener(e -> showTopUpPage());
        qaReport.addActionListener(e-> showReportPage());
        quickActions.add(qaAct); quickActions.add(qaTopup); quickActions.add(qaReport);

        page.add(quickActions, BorderLayout.SOUTH);

        contentPanel.add(page);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    private JPanel statCard(String title, String value, String sub, String icon, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card bg
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                // Left accent stripe
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight()-1, 4, 4);
                // Subtle glow at top-left
                RadialGradientPaint glow = new RadialGradientPaint(
                    new Point2D.Float(30, 30), 80,
                    new float[]{0f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30),
                                new Color(0, 0, 0, 0)});
                g2.setPaint(glow);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 140));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        // Icon + title row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel titleLbl = label(title.toUpperCase(), F_CAPTION, TEXT_MUTED);
        topRow.add(iconLbl,  BorderLayout.WEST);
        topRow.add(titleLbl, BorderLayout.EAST);

        // Value
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(accent);

        // Subtitle
        JLabel subLbl = label(sub, F_CAPTION, TEXT_MUTED);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.add(topRow);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));
        inner.add(valueLbl);
        inner.add(Box.createRigidArea(new Dimension(0, 4)));
        inner.add(subLbl);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PAGE: ACTIVITY TRACKER
    // ════════════════════════════════════════════════════════════════════════
    private void showActivityPage() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(30, 32, 30, 32));

        page.add(pageHeader("Activity Tracker", "Log your digital activities"), BorderLayout.NORTH);

        // Split: form left | table right
        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setOpaque(false);

        // ── Form card ────────────────────────────────────────────────────
        JPanel formCard = createGlassCard(new GridBagLayout());
        GridBagConstraints g = formGbc();

        String[] apps = {"TikTok","Instagram","YouTube","WhatsApp","Netflix","Spotify"};
        JComboBox<String> appName  = styledCombo(apps);
        JTextField        duration = styledField("Duration in minutes");
        JTextField        limit    = styledField("Daily limit (minutes)");

        JComboBox<Integer> dayCombo   = new JComboBox<>();
        JComboBox<Integer> monCombo   = new JComboBox<>();
        JComboBox<Integer> yearCombo  = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayCombo.addItem(i);
        for (int i = 1; i <= 12; i++) monCombo.addItem(i);
        int cy = LocalDate.now().getYear();
        for (int i = cy - 2; i <= cy + 1; i++) yearCombo.addItem(i);
        dayCombo.setSelectedItem(LocalDate.now().getDayOfMonth());
        monCombo.setSelectedItem(LocalDate.now().getMonthValue());
        yearCombo.setSelectedItem(cy);
        styleComboBox(dayCombo); styleComboBox(monCombo); styleComboBox(yearCombo);

        JPanel dateRow = new JPanel(new GridLayout(1, 5, 6, 0));
        dateRow.setOpaque(false);
        dateRow.add(dayCombo);
        dateRow.add(label("/", F_BODY, TEXT_MUTED));
        dateRow.add(monCombo);
        dateRow.add(label("/", F_BODY, TEXT_MUTED));
        dateRow.add(yearCombo);

        JButton addBtn = accentButton("Log Activity  (−5 Tokens)", ACCENT_CYAN);

        int row = 0;
        g.gridx = 0; g.gridy = row++; g.gridwidth = 2;
        formCard.add(sectionLabel("App & Duration"), g);
        g.gridy = row++;
        formCard.add(fieldLabel("Application"), g);
        g.gridy = row++;
        formCard.add(appName, g);
        g.gridy = row++; g.insets = new Insets(14,0,4,0);
        formCard.add(fieldLabel("Duration (min)"), g);
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        formCard.add(duration, g);
        g.gridy = row++; g.insets = new Insets(14,0,4,0);
        formCard.add(fieldLabel("Daily Limit (min)"), g);
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        formCard.add(limit, g);
        g.gridy = row++; g.insets = new Insets(14,0,4,0);
        formCard.add(fieldLabel("Date"), g);
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        formCard.add(dateRow, g);
        g.gridy = row++; g.insets = new Insets(22,0,0,0);
        formCard.add(addBtn, g);

        // ── Table card ───────────────────────────────────────────────────
        JPanel tableCard = createGlassCard(new BorderLayout(0, 14));

        JLabel tblTitle = label("Activity Log", F_HEADING, TEXT_PRIMARY);
        tableCard.add(tblTitle, BorderLayout.NORTH);

        // activityModel = new DefaultTableModel(
        //     new String[]{"App","Duration","Limit","Date","Status"}, 0) {
        //     @Override public boolean isCellEditable(int r, int c) { return false; }
        // };

        activityModel = new DefaultTableModel(
            new String[]{"App","Duration","Limit","Date","Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // LOAD DATA DARI LIST USER
        for (AktivitasDigital act : user.getAktivitasList()) {
            String status = act.melebihiBatas() ? "OVER LIMIT" : "HEALTHY";

            LocalDate date = act.getTanggal();
            String dateStr = String.format("%02d/%02d/%d",
                    date.getDayOfMonth(),
                    date.getMonthValue(),
                    date.getYear());

            activityModel.addRow(new Object[]{
                    act.getNamaAplikasi(),
                    act.getDurasiMenit(),
                    act.getBatasDurasi(),
                    dateStr,
                    status
            });
        }


        JTable table = new JTable(activityModel);
        styleTable(table);
        JScrollPane scroll = styledScroll(table);
        tableCard.add(scroll, BorderLayout.CENTER);

        // Action
        // addBtn.addActionListener(e -> {
        //     try {
        //         if (user.getToken() < 5) {
        //             showToast(this, "Token tidak cukup!", CLR_WARN);
        //             return;
        //         }
        //         int d = (Integer) dayCombo.getSelectedItem();
        //         int m = (Integer) monCombo.getSelectedItem();
        //         int y = (Integer) yearCombo.getSelectedItem();
        //         LocalDate date = LocalDate.of(y, m, d);
        //         AktivitasDigital act = new AktivitasDigital(
        //             appName.getSelectedItem().toString(),
        //             Integer.parseInt(duration.getText().trim()),
        //             Integer.parseInt(limit.getText().trim()),
        //             date
        //         );
        //         user.tambahAktivitas(act);
        //         user.kurangiToken(5);
        //         String status   = act.melebihiBatas() ? "OVER LIMIT" : "HEALTHY";
        //         String dateStr  = String.format("%02d/%02d/%d", d, m, y);
        //         activityModel.addRow(new Object[]{
        //             act.getNamaAplikasi(), act.getDurasiMenit(),
        //             act.getBatasDurasi(), dateStr, status});
        //         showHomePage();
        //         showActivityPage();
        //     } catch (Exception ex) {
        //         showToast(this, "Data tidak valid: " + ex.getMessage(), CLR_DANGER);
        //     }
        // });
        addBtn.addActionListener(e -> {
            try {
                if (user.getToken() < 5) {
                    showToast(this, "Token tidak cukup!", CLR_WARN);
                    return;
                }

                int d = (Integer) dayCombo.getSelectedItem();
                int m = (Integer) monCombo.getSelectedItem();
                int y = (Integer) yearCombo.getSelectedItem();

                LocalDate date = LocalDate.of(y, m, d);

                AktivitasDigital act = new AktivitasDigital(
                        appName.getSelectedItem().toString(),
                        Integer.parseInt(duration.getText().trim()),
                        Integer.parseInt(limit.getText().trim()),
                        date
                );

                user.tambahAktivitas(act);
                user.kurangiToken(5);

                showToast(this, "Aktivitas berhasil ditambahkan!", CLR_SUCCESS);

                showActivityPage();

            } catch (Exception ex) {
                showToast(this, "Data tidak valid: " + ex.getMessage(), CLR_DANGER);
            }
        });

        body.add(formCard);
        body.add(tableCard);

        page.add(body, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PAGE: TOP UP
    // ════════════════════════════════════════════════════════════════════════
    private void showTopUpPage() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(30, 32, 30, 32));

        page.add(pageHeader("Top Up Balance", "Add tokens to your account"), BorderLayout.NORTH);

        // Center card
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);

        JPanel card = createGlassCard(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 320));

        GridBagConstraints g = formGbc();
        int row = 0;

        JTextField amountField = styledField("e.g. 50000");
        String[] methods = {"QRIS (Instant)", "Bank Transfer", "E-Wallet"};
        JComboBox<String> metodeBox = styledCombo(methods);
        JButton topupBtn = accentButton("Process Top Up", CLR_SUCCESS);

        g.gridx = 0; g.gridy = row++; g.gridwidth = 2;
        card.add(sectionLabel("Top Up Details"), g);
        g.gridy = row++; g.insets = new Insets(14,0,4,0);
        card.add(fieldLabel("Amount (Rp)"), g);
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        card.add(amountField, g);
        g.gridy = row++; g.insets = new Insets(14,0,4,0);
        card.add(fieldLabel("Payment Method"), g);
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        card.add(metodeBox, g);
        g.gridy = row; g.insets = new Insets(24,0,0,0);
        card.add(topupBtn, g);

        topupBtn.addActionListener(e -> {
            try {
                TopUp topUp = new TopUp(
                    Integer.parseInt(amountField.getText().trim()),
                    metodeBox.getSelectedItem().toString());
                topUp.prosesTopUp(user);
                amountField.setText("");
                showToast(this, "Top up berhasil ditambahkan! 🎉", CLR_SUCCESS);
                showHomePage();
            } catch (Exception ex) {
                showToast(this, "Jumlah tidak valid", CLR_DANGER);
            }
        });

        centerWrap.add(card);
        page.add(centerWrap, BorderLayout.CENTER);

        contentPanel.add(page);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PAGE: HEALTH REPORT
    // ════════════════════════════════════════════════════════════════════════
    private void showReportPage() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(30, 32, 30, 32));

        page.add(pageHeader("Health Report", "Your mental wellbeing summary"), BorderLayout.NORTH);

        JPanel card = createGlassCard(new BorderLayout(0, 16));

        JButton genBtn = accentButton("Generate Summary Report", ACCENT_CYAN);

        JPanel reportArea = new JPanel(new BorderLayout());
        reportArea.setOpaque(false);

        genBtn.addActionListener(e -> {
            String laporan = user.lihatLaporan().generateLaporan();
            reportArea.removeAll();
            reportArea.add(buildReportView(laporan), BorderLayout.CENTER);
            reportArea.revalidate();
            reportArea.repaint();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);
        topBar.add(genBtn);

        card.add(topBar,    BorderLayout.NORTH);
        card.add(new JScrollPane(reportArea) {{
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            getViewport().setOpaque(false);
            getVerticalScrollBar().setUnitIncrement(16);
        }}, BorderLayout.CENTER);

        page.add(card, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    private JEditorPane buildReportView(String laporan) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Segoe UI,Arial,sans-serif; " +
                    "background:#16203f; color:#dce8ff; margin:0; padding:12px;'>");
        html.append("<div style='text-align:center; margin-bottom:16px;'>");
        html.append("<span style='font-size:18pt; font-weight:bold; color:#00d2c8;'>Health Summary Report</span><br>");
        html.append("<span style='font-size:10pt; color:#7090b8;'>MindFull · Mental Wellbeing App</span>");
        html.append("</div>");
        html.append("<hr style='border:none; border-top:1px solid #2a3a6a; margin:10px 0;'>");

        for (String line : laporan.split("\n")) {
            String t = line.trim();
            if (t.isEmpty()) { html.append("<br>"); }
            else if (t.startsWith("===") || t.startsWith("---")) {
                html.append("<hr style='border:none; border-top:1px solid #2a3a6a; margin:8px 0;'>");
            } else if (t.toUpperCase().equals(t) && t.length() > 3 && !t.contains(":")) {
                html.append("<p style='margin:12px 0 4px; font-size:12pt; font-weight:bold; color:#00d2c8;'>")
                    .append(t).append("</p>");
            } else if (t.startsWith("-") || t.startsWith("•")) {
                html.append("<p style='margin:3px 0 3px 20px; font-size:11pt; color:#8ab0d8;'>")
                    .append(t).append("</p>");
            } else if (t.contains(":")) {
                int idx = t.indexOf(":");
                html.append("<p style='margin:5px 0; font-size:11pt;'><b style='color:#a0caf0;'>")
                    .append(t, 0, idx + 1).append("</b><span style='color:#8ab0d8;'>")
                    .append(t.substring(idx + 1)).append("</span></p>");
            } else {
                html.append("<p style='margin:3px 0; font-size:11pt; color:#8ab0d8;'>").append(t).append("</p>");
            }
        }

        html.append("<hr style='border:none; border-top:1px solid #2a3a6a; margin:14px 0 6px;'>");
        html.append("<p style='font-size:9pt; color:#405080; text-align:right;'>Generated by MindFull</p>");
        html.append("</body></html>");

        JEditorPane ep = new JEditorPane("text/html", html.toString());
        ep.setEditable(false);
        ep.setBackground(BG_CARD);
        ep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        return ep;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PAGE: PROFILE
    // ════════════════════════════════════════════════════════════════════════
    private void showProfilePage() {
        contentPanel.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(30, 32, 30, 32));

        page.add(pageHeader("Profile", "Manage your account credentials"), BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);

        JPanel card = createGlassCard(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 340));

        GridBagConstraints g = formGbc();
        int row = 0;

        // Avatar + name display
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        avatarRow.setOpaque(false);
        avatarRow.add(createAvatarCircle(user.getNamaUser(), 52));
        JPanel nameInfo = new JPanel();
        nameInfo.setLayout(new BoxLayout(nameInfo, BoxLayout.Y_AXIS));
        nameInfo.setOpaque(false);
        nameInfo.add(label(user.getNamaUser(), F_HEADING, TEXT_PRIMARY));
        nameInfo.add(label("@" + currentUsername, F_CAPTION, TEXT_MUTED));
        avatarRow.add(nameInfo);

        g.gridx = 0; g.gridy = row++; g.gridwidth = 2;
        card.add(avatarRow, g);

        g.gridy = row++; g.insets = new Insets(20, 0, 4, 0);
        card.add(fieldLabel("New Username"), g);
        JTextField newUsername = styledField(currentUsername);
        newUsername.setText(currentUsername);
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        card.add(newUsername, g);

        g.gridy = row++; g.insets = new Insets(14, 0, 4, 0);
        card.add(fieldLabel("New Password"), g);
        JPasswordField newPass = styledPassField("New password");
        g.gridy = row++; g.insets = new Insets(0,0,0,0);
        card.add(newPass, g);

        JButton updateBtn = accentButton("Update Credentials", ACCENT_BLUE);
        g.gridy = row; g.insets = new Insets(24,0,0,0);
        card.add(updateBtn, g);

        updateBtn.addActionListener(e -> {
            String uname = newUsername.getText().trim();
            String pwd   = new String(newPass.getPassword());
            if (uname.isEmpty() || pwd.isEmpty()) {
                showToast(this, "Username dan password tidak boleh kosong!", CLR_WARN);
                return;
            }
            if (UserManager.updateCredentials(currentUsername, uname, pwd)) {
                currentUsername = uname;
                user.setUsername(uname);
                showToast(this, "Credentials berhasil diupdate! ✓", CLR_SUCCESS);
            } else {
                showToast(this, "Username sudah digunakan!", CLR_DANGER);
            }
        });

        centerWrap.add(card);
        page.add(centerWrap, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UI COMPONENT FACTORIES
    // ════════════════════════════════════════════════════════════════════════

    /** Glass-morphism card panel with dark translucent bg + rounded corners */
    private JPanel createGlassCard(LayoutManager layout) {
        JPanel card = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                // Subtle border
                g2.setColor(new Color(255, 255, 255, 12));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(26, 28, 26, 28));
        return card;
    }

    /** Modern text field */
    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField(20) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_MID);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
            }
        };
        f.setFont(F_BODY);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_CYAN);
        f.setBackground(new Color(0,0,0,0));
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(10, 12, 10, 12));
        f.setPreferredSize(new Dimension(320, 40));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return f;
    }

    /** Password field */
    private JPasswordField styledPassField(String placeholder) {
        JPasswordField f = new JPasswordField(20) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_MID);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
                g2.dispose();
            }
        };
        f.setFont(F_BODY);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_CYAN);
        f.setBackground(new Color(0,0,0,0));
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(10, 12, 10, 12));
        f.setPreferredSize(new Dimension(320, 40));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return f;
    }

    @SuppressWarnings("unchecked")
    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        styleComboBox(cb);
        return cb;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(F_BODY);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_MID);
        cb.setOpaque(true);
        cb.setPreferredSize(new Dimension(320, 38));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cb.setBorder(new EmptyBorder(4, 8, 4, 8));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? new Color(0, 180, 170, 60) : BG_MID);
                setForeground(TEXT_PRIMARY);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
    }

    /** Primary accent button */
    private JButton accentButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                    ? accent.brighter()
                    : getModel().isPressed() ? accent.darker() : accent;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_SUBHEAD);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(11, 22, 11, 22));
        btn.setPreferredSize(new Dimension(320, 42));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return btn;
    }

    /** Danger (red-tinted) button */
    private JButton dangerButton(String text) {
        JButton btn = accentButton(text, new Color(50, 15, 25));
        btn.setForeground(CLR_DANGER);
        return btn;
    }

    /** Sidebar nav button */
    private JButton navButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BODY);
        btn.setForeground(TEXT_SEC);
        btn.setBackground(new Color(0,0,0,0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 12, 10, 12));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn)
                    btn.setBackground(new Color(255,255,255, 8));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn)
                    btn.setBackground(new Color(0,0,0,0));
            }
        });
        return btn;
    }

    /** Small pill/tag button for quick actions */
    private JButton pillButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                    ? new Color(0, 210, 200, 40)
                    : new Color(0, 210, 200, 20);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.setColor(new Color(0, 210, 200, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_CAPTION);
        btn.setForeground(ACCENT_CYAN);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    /** Link-style underline button */
    private JButton linkButton(String text) {
        JButton btn = new JButton("<html><u>" + text + "</u></html>");
        btn.setFont(F_CAPTION);
        btn.setForeground(TEXT_SEC);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return btn;
    }

    /** Page header with title + subtitle */
    private JPanel pageHeader(String title, String sub) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel t = label(title, F_DISPLAY, TEXT_PRIMARY);
        JLabel s = label(sub,   F_CAPTION, TEXT_MUTED);
        p.add(t); p.add(Box.createRigidArea(new Dimension(0, 4))); p.add(s);
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = label(text, F_HEADING, TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 0, 10, 0));
        return l;
    }

    private JLabel fieldLabel(String text) {
        return label(text, F_CAPTION, TEXT_MUTED);
    }

    private JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /** Sidebar thin separator */
    private JSeparator sidebarDivider() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(255, 255, 255, 18));
        sep.setBackground(new Color(0, 0, 0, 0));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    /** Default GridBagConstraints for forms */
    private GridBagConstraints formGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        g.insets = new Insets(0, 0, 0, 0);
        return g;
    }

    /** Avatar circle with initials */
    private JPanel createAvatarCircle(String name, int size) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++)
            if (!parts[i].isEmpty()) sb.append(parts[i].charAt(0));
        final String ini = sb.toString().toUpperCase();

        JPanel circle = new JPanel() {
            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
            @Override public Dimension getMinimumSize()   { return getPreferredSize(); }
            @Override public Dimension getMaximumSize()   { return getPreferredSize(); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient fill
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_CYAN, size, size, ACCENT_BLUE);
                g2.setPaint(gp);
                g2.fillOval(0, 0, size-1, size-1);
                // Initials
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini,
                    (size - fm.stringWidth(ini)) / 2,
                    (size - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        return circle;
    }

    /** Table styling */
    private void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(F_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD2);
        table.setSelectionBackground(new Color(0, 210, 200, 40));
        table.setSelectionForeground(ACCENT_CYAN);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(255, 255, 255, 12));
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(F_SUBHEAD);
        table.getTableHeader().setBackground(BG_MID);
        table.getTableHeader().setForeground(TEXT_SEC);
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        table.getTableHeader().setReorderingAllowed(false);
        // Center renderer
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setBackground(BG_CARD2);
        center.setForeground(TEXT_PRIMARY);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        // Status column colored
        if (table.getColumnCount() >= 5) {
            table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(sel ? new Color(0, 210, 200, 40) : BG_CARD2);
                    String val = v == null ? "" : v.toString();
                    setForeground("HEALTHY".equals(val) ? CLR_SUCCESS : CLR_DANGER);
                    setFont(F_SUBHEAD);
                    return this;
                }
            });
        }
    }

    private JScrollPane styledScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(BG_CARD2);
        sp.getViewport().setBackground(BG_CARD2);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar().setBackground(BG_MID);
        return sp;
    }

    /** Floating toast notification */
    private void showToast(Component parent, String message, Color accent) {
        JWindow toast = new JWindow();
        toast.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(22, 32, 63, 230));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        JLabel lbl = new JLabel(message);
        lbl.setFont(F_BODY);
        lbl.setForeground(TEXT_PRIMARY);
        panel.add(lbl);
        toast.add(panel);
        toast.pack();

        // Position near bottom-center of parent window
        Window win = SwingUtilities.getWindowAncestor(parent);
        if (win == null) win = parent instanceof Window ? (Window) parent : null;
        if (win != null) {
            Point loc = win.getLocation();
            toast.setLocation(
                loc.x + win.getWidth()  / 2 - toast.getWidth()  / 2,
                loc.y + win.getHeight() - 80);
        }

        toast.setVisible(true);
        Timer timer = new Timer(2400, e -> toast.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MentalWellbeingApp::new);
    }
}