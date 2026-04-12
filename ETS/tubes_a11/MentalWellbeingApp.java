package tubes_a11;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;

public class MentalWellbeingApp extends JFrame {
    private User user;
    private String currentUsername;
    private JPanel tokenLabel, scoreLabel, screenTimeLabel;
    private DefaultTableModel activityModel;
    private JPanel contentPanel;
    
    // --- Palette Warna Merah Kalem ---
    private final Color COLOR_PRIMARY = new Color(224, 51, 72);   // #e03348 Merah utama
    private final Color COLOR_SUCCESS = new Color(180, 40, 60);   // Merah tua (aksi)
    private final Color COLOR_DANGER  = new Color(120, 20, 35);   // Merah sangat tua (logout)
    private final Color COLOR_BG      = new Color(248, 197, 176); // #f8c5b0 Peach latar
    private final Color COLOR_CARD    = new Color(255, 240, 232); // Peach muda card
    private final Color COLOR_SIDEBAR = new Color(100, 20, 35);   // Merah tua sidebar
    private final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_REGULAR   = new Font("Segoe UI", Font.PLAIN, 13);

    public MentalWellbeingApp() {
        loginScreen();
    }

    private void loginScreen() {
        JFrame login = new JFrame("Welcome Back");
        login.setSize(400, 400);
        login.setLocationRelativeTo(null);
        login.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Login Form
        JLabel loginTitle = new JLabel("Login Dashboard", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setForeground(COLOR_PRIMARY);

        JTextField loginUsername = new JTextField();
        JPasswordField loginPassword = new JPasswordField();
        JButton loginBtn = createStyledButton("LOGIN", COLOR_PRIMARY);
        JButton toRegisterBtn = new JButton("<html><u>Belum punya akun? Daftar disini</u></html>");
        toRegisterBtn.setForeground(COLOR_SIDEBAR);
        toRegisterBtn.setBorderPainted(false);
        toRegisterBtn.setContentAreaFilled(false);
        toRegisterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false);
        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.fill = GridBagConstraints.HORIZONTAL;
        loginGbc.insets = new Insets(5, 5, 5, 5);

        loginGbc.gridx = 0; loginGbc.gridy = 0; loginGbc.gridwidth = 2;
        loginPanel.add(loginTitle, loginGbc);
        
        loginGbc.gridy = 1; loginGbc.gridwidth = 1; loginGbc.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(new JLabel("Username:"), loginGbc);
        loginGbc.gridx = 1; loginPanel.add(loginUsername, loginGbc);

        loginGbc.gridx = 0; loginGbc.gridy = 2; loginGbc.insets = new Insets(5, 5, 5, 5);
        loginPanel.add(new JLabel("Password:"), loginGbc);
        loginGbc.gridx = 1; loginPanel.add(loginPassword, loginGbc);

        loginGbc.gridx = 0; loginGbc.gridy = 3; loginGbc.gridwidth = 2; loginGbc.insets = new Insets(20, 5, 5, 5);
        loginPanel.add(loginBtn, loginGbc);
        
        loginGbc.gridy = 4; loginGbc.insets = new Insets(10, 5, 5, 5);
        loginPanel.add(toRegisterBtn, loginGbc);

        // Register Form
        JLabel registerTitle = new JLabel("Daftar Akun Baru", SwingConstants.CENTER);
        registerTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        registerTitle.setForeground(COLOR_PRIMARY);

        JTextField regNamaLengkap = new JTextField();
        JTextField regUsername = new JTextField();
        JPasswordField regPassword = new JPasswordField();
        JButton registerBtn = createStyledButton("DAFTAR", COLOR_SUCCESS);
        JButton toLoginBtn = new JButton("<html><u>Sudah punya akun? Login disini</u></html>");
        toLoginBtn.setForeground(COLOR_SIDEBAR);
        toLoginBtn.setBorderPainted(false);
        toLoginBtn.setContentAreaFilled(false);
        toLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setOpaque(false);
        GridBagConstraints regGbc = new GridBagConstraints();
        regGbc.fill = GridBagConstraints.HORIZONTAL;
        regGbc.insets = new Insets(5, 5, 5, 5);

        regGbc.gridx = 0; regGbc.gridy = 0; regGbc.gridwidth = 2;
        registerPanel.add(registerTitle, regGbc);
        
        regGbc.gridy = 1; regGbc.gridwidth = 1; regGbc.insets = new Insets(20, 5, 5, 5);
        registerPanel.add(new JLabel("Nama Lengkap:"), regGbc);
        regGbc.gridx = 1; registerPanel.add(regNamaLengkap, regGbc);

        regGbc.gridx = 0; regGbc.gridy = 2; regGbc.insets = new Insets(5, 5, 5, 5);
        registerPanel.add(new JLabel("Username:"), regGbc);
        regGbc.gridx = 1; registerPanel.add(regUsername, regGbc);

        regGbc.gridx = 0; regGbc.gridy = 3;
        registerPanel.add(new JLabel("Password:"), regGbc);
        regGbc.gridx = 1; registerPanel.add(regPassword, regGbc);

        regGbc.gridx = 0; regGbc.gridy = 4; regGbc.gridwidth = 2; regGbc.insets = new Insets(20, 5, 5, 5);
        registerPanel.add(registerBtn, regGbc);
        
        regGbc.gridy = 5; regGbc.insets = new Insets(10, 5, 5, 5);
        registerPanel.add(toLoginBtn, regGbc);

        registerPanel.setVisible(false);

        // Actions
        loginBtn.addActionListener(e -> {
            String username = loginUsername.getText();
            String password = new String(loginPassword.getPassword());
            String namaLengkap = UserManager.login(username, password);
            
            if (namaLengkap != null) {
                currentUsername = username;
                user = new User(1, namaLengkap, 50);
                user.setUsername(username);
                login.dispose();
                initUI();
            } else {
                JOptionPane.showMessageDialog(login, "Username atau Password Salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> {
            String namaLengkap = regNamaLengkap.getText().trim();
            String username = regUsername.getText().trim();
            String password = new String(regPassword.getPassword());
            
            if (namaLengkap.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(login, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (UserManager.register(username, password, namaLengkap)) {
                JOptionPane.showMessageDialog(login, "Registrasi berhasil! Silakan login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                regNamaLengkap.setText("");
                regUsername.setText("");
                regPassword.setText("");
                registerPanel.setVisible(false);
                loginPanel.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(login, "Username sudah terdaftar!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        toRegisterBtn.addActionListener(e -> {
            loginPanel.setVisible(false);
            registerPanel.setVisible(true);
        });

        toLoginBtn.addActionListener(e -> {
            registerPanel.setVisible(false);
            loginPanel.setVisible(true);
        });

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(loginPanel, gbc);
        mainPanel.add(registerPanel, gbc);

        login.add(mainPanel);
        login.setVisible(true);
    }

    private void initUI() {
        setTitle("MindFull - Mental Health Tracker");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_BG);
        add(contentPanel, BorderLayout.CENTER);

        showHomePage();
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(COLOR_SIDEBAR);
        menuPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        // --- Avatar Profil ---
        JPanel avatarSection = new JPanel();
        avatarSection.setLayout(new BoxLayout(avatarSection, BoxLayout.Y_AXIS));
        avatarSection.setOpaque(false);
        avatarSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Lingkaran avatar dengan inisial
        JPanel avatar = createAvatarPanel(user.getNamaUser());
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarSection.add(avatar);
        avatarSection.add(Box.createRigidArea(new Dimension(0, 10)));

        // Nama lengkap di bawah avatar
        JLabel namaLabel = new JLabel(user.getNamaUser(), SwingConstants.CENTER);
        namaLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        namaLabel.setForeground(Color.WHITE);
        namaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        namaLabel.setMaximumSize(new Dimension(170, 20));
        avatarSection.add(namaLabel);
        avatarSection.add(Box.createRigidArea(new Dimension(0, 6)));

        // Tombol Edit Profil kecil
        JButton profileBtn = new JButton("Edit Profil");
        profileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        profileBtn.setForeground(new Color(255, 200, 190));
        profileBtn.setBorderPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileBtn.addActionListener(e -> showProfilePage());
        avatarSection.add(profileBtn);

        menuPanel.add(avatarSection);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Garis pemisah
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(170, 1));
        sep.setForeground(new Color(150, 60, 70));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(sep);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Menu Buttons
        JButton homeBtn = createSidebarButton("Home");
        homeBtn.addActionListener(e -> showHomePage());
        menuPanel.add(homeBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton activityBtn = createSidebarButton("Activity Tracker");
        activityBtn.addActionListener(e -> showActivityPage());
        menuPanel.add(activityBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton topupBtn = createSidebarButton("Top Up Balance");
        topupBtn.addActionListener(e -> showTopUpPage());
        menuPanel.add(topupBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton reportBtn = createSidebarButton("Health Report");
        reportBtn.addActionListener(e -> showReportPage());
        menuPanel.add(reportBtn);

        sidebar.add(menuPanel, BorderLayout.NORTH);

        // Logout Button
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(COLOR_SIDEBAR);
        logoutPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JButton logoutBtn = createSidebarButton("Logout");
        logoutBtn.setBackground(COLOR_DANGER);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new MentalWellbeingApp();
            }
        });
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createAvatarPanel(String namaUser) {
        // Ambil inisial: maks 2 huruf pertama dari setiap kata
        String[] parts = namaUser.trim().split("\\s+");
        String initials = "";
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) initials += parts[i].charAt(0);
        }
        final String inisial = initials.toUpperCase();

        JPanel circle = new JPanel() {
            private final int SZ = 80;
            @Override public Dimension getPreferredSize() { return new Dimension(SZ, SZ); }
            @Override public Dimension getMinimumSize()   { return new Dimension(SZ, SZ); }
            @Override public Dimension getMaximumSize()   { return new Dimension(SZ, SZ); }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int pad  = 4;
                int size = SZ - pad * 2;  // 72
                int x    = pad;
                int y    = pad;

                // Lingkaran isi merah
                g2.setColor(new Color(224, 100, 110));
                g2.fillOval(x, y, size, size);

                // Border ring putih dengan stroke — selalu presisi, tidak terpotong
                g2.setColor(new Color(255, 255, 255, 210));
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(x + 1, y + 1, size - 2, size - 2);

                // Teks inisial di tengah
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (size - fm.stringWidth(inisial)) / 2;
                int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(inisial, tx, ty);
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        return circle;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(130, 40, 55));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        return btn;
    }

    private void showHomePage() {
        contentPanel.removeAll();
        
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(COLOR_BG);
        homePanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_SIDEBAR);
        homePanel.add(title, BorderLayout.NORTH);

        // Cards vertikal (beriringan ke bawah)
        JPanel cardsPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        tokenLabel = createDashboardCard("Tokens Available", String.valueOf(user.getToken()), new Color(224, 51, 72));
        scoreLabel = createDashboardCard("Wellness Score", String.valueOf(user.hitungScoreKesehatan()), new Color(160, 35, 55));
        screenTimeLabel = createDashboardCard("Screen Time", user.hitungTotalScreenTime() + " min", new Color(120, 20, 40));

        cardsPanel.add(tokenLabel);
        cardsPanel.add(scoreLabel);
        cardsPanel.add(screenTimeLabel);

        // Wrap agar cards tidak memenuhi seluruh layar
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.add(cardsPanel, BorderLayout.NORTH);

        homePanel.add(cardWrapper, BorderLayout.CENTER);
        contentPanel.add(homePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfilePage() {
        contentPanel.removeAll();
        
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(COLOR_BG);
        profilePanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Profil Pengguna");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_SIDEBAR);
        profilePanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Nama Lengkap: " + user.getNamaUser());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JTextField newUsername = new JTextField(currentUsername, 20);
        JPasswordField newPassword = new JPasswordField(20);
        JButton updateBtn = createStyledButton("Update Credentials", COLOR_PRIMARY);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(nameLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.insets = new Insets(30, 10, 10, 10);
        formPanel.add(new JLabel("Username Baru:"), gbc);
        gbc.gridx = 1; formPanel.add(newUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(new JLabel("Password Baru:"), gbc);
        gbc.gridx = 1; formPanel.add(newPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            String username = newUsername.getText().trim();
            String password = new String(newPassword.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (UserManager.updateCredentials(currentUsername, username, password)) {
                currentUsername = username;
                user.setUsername(username);
                JOptionPane.showMessageDialog(this, "Credentials berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(formPanel);
        
        profilePanel.add(centerWrapper, BorderLayout.CENTER);
        contentPanel.add(profilePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showActivityPage() {
        contentPanel.removeAll();
        
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(COLOR_BG);
        activityPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Activity Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_SIDEBAR);
        activityPanel.add(title, BorderLayout.NORTH);

        JPanel content = createActivityPanel();
        content.setBorder(new EmptyBorder(20, 0, 0, 0));
        activityPanel.add(content, BorderLayout.CENTER);

        contentPanel.add(activityPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTopUpPage() {
        contentPanel.removeAll();
        
        JPanel topupPanel = new JPanel(new BorderLayout());
        topupPanel.setBackground(COLOR_BG);
        topupPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Top Up Balance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_SIDEBAR);
        topupPanel.add(title, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(15, 0, 0, 0));
        centerWrapper.add(createTopUpPanel());
        
        topupPanel.add(centerWrapper, BorderLayout.CENTER);
        contentPanel.add(topupPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReportPage() {
        contentPanel.removeAll();
        
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(COLOR_BG);
        reportPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Health Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_SIDEBAR);
        reportPanel.add(title, BorderLayout.NORTH);

        JPanel content = createReportPanel();
        content.setBorder(new EmptyBorder(15, 0, 0, 0));
        reportPanel.add(content, BorderLayout.CENTER);

        contentPanel.add(reportPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createDashboardCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(COLOR_CARD);
        card.setPreferredSize(new Dimension(0, 80));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 160, 150), 1),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, accent),
                BorderFactory.createEmptyBorder(0, 22, 0, 22)
            )
        ));

        // Kiri: judul
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLbl.setForeground(new Color(100, 30, 40));

        // Kanan: nilai besar
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 34));
        valueLbl.setForeground(accent);
        valueLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(titleLbl, BorderLayout.WEST);
        card.add(valueLbl, BorderLayout.EAST);
        return card;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 160, 150), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Form Section
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] apps = {"TikTok", "Instagram", "YouTube", "WhatsApp", "Netflix", "Spotify"};
        JComboBox<String> appName = new JComboBox<>(apps);
        JTextField duration = new JTextField();
        JTextField limit = new JTextField();
        
        JComboBox<Integer> dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayCombo.addItem(i);
        
        JComboBox<Integer> monthCombo = new JComboBox<>();
        for (int i = 1; i <= 12; i++) monthCombo.addItem(i);
        
        JComboBox<Integer> yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) yearCombo.addItem(i);
        yearCombo.setSelectedItem(currentYear);
        
        LocalDate today = LocalDate.now();
        dayCombo.setSelectedItem(today.getDayOfMonth());
        monthCombo.setSelectedItem(today.getMonthValue());
        
        JButton addBtn = createStyledButton("Log Activity (-5 Tokens)", COLOR_PRIMARY);

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("App Name:"), gbc);
        gbc.gridx = 1; form.add(appName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Duration (Min):"), gbc);
        gbc.gridx = 1; form.add(duration, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Daily Limit:"), gbc);
        gbc.gridx = 1; form.add(limit, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Tanggal:"), gbc);
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setOpaque(false);
        datePanel.add(dayCombo);
        datePanel.add(new JLabel("/"));
        datePanel.add(monthCombo);
        datePanel.add(new JLabel("/"));
        datePanel.add(yearCombo);
        gbc.gridx = 1; form.add(datePanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; form.add(addBtn, gbc);

        // Table Section
        activityModel = new DefaultTableModel(new String[]{"App", "Duration", "Limit", "Tanggal", "Status"}, 0);
        JTable table = new JTable(activityModel);
        styleTable(table);

        addBtn.addActionListener(e -> {
            try {
                if (user.getToken() < 5) {
                    JOptionPane.showMessageDialog(this, "Token tidak cukup!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int day = (Integer) dayCombo.getSelectedItem();
                int month = (Integer) monthCombo.getSelectedItem();
                int year = (Integer) yearCombo.getSelectedItem();
                LocalDate selectedDate = LocalDate.of(year, month, day);
                
                AktivitasDigital act = new AktivitasDigital(
                    appName.getSelectedItem().toString(),
                    Integer.parseInt(duration.getText()),
                    Integer.parseInt(limit.getText()),
                    selectedDate
                );
                user.tambahAktivitas(act);
                user.kurangiToken(5);
                
                String status = act.melebihiBatas() ? "OVER LIMIT" : "HEALTHY";
                String tanggalStr = String.format("%02d/%02d/%d", day, month, year);
                activityModel.addRow(new Object[]{act.getNamaAplikasi(), act.getDurasiMenit(), act.getBatasDurasi(), tanggalStr, status});
                showHomePage();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Mohon masukkan data yang valid: " + ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTopUpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 160, 150), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField jumlah = new JTextField(15);
        JComboBox<String> metode = new JComboBox<>(new String[]{"QRIS (Instant)", "Bank Transfer", "E-Wallet"});
        JButton btn = createStyledButton("Proses Top Up", COLOR_SUCCESS);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; panel.add(jumlah, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Method:"), gbc);
        gbc.gridx = 1; panel.add(metode, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(btn, gbc);

        btn.addActionListener(e -> {
            try {
                TopUp topUp = new TopUp(Integer.parseInt(jumlah.getText()), metode.getSelectedItem().toString());
                topUp.prosesTopUp(user);
                showHomePage();
                JOptionPane.showMessageDialog(this, "Top up berhasil ditambahkan!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak valid");
            }
        });

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);

        JButton generate = createStyledButton("Generate Summary Report", COLOR_PRIMARY);

        // Paper container - akan diisi setelah generate
        JPanel paperWrapper = new JPanel(new BorderLayout());
        paperWrapper.setBackground(COLOR_BG);
        paperWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));

        generate.addActionListener(e -> {
            String laporan = user.lihatLaporan().generateLaporan();
            paperWrapper.removeAll();
            paperWrapper.add(createPaperPanel(laporan), BorderLayout.CENTER);
            paperWrapper.revalidate();
            paperWrapper.repaint();
        });

        panel.add(generate, BorderLayout.NORTH);
        panel.add(new JScrollPane(paperWrapper), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaperPanel(String laporan) {
        // Bangun HTML untuk tampilan kertas yang rapi
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Segoe UI,Arial,sans-serif; margin:0; padding:0;'>");
        html.append("<div style='text-align:center; margin-bottom:14px;'>");
        html.append("<span style='font-size:20pt; font-weight:bold; color:#2c3e50;'>Health Summary Report</span><br>");
        html.append("<span style='font-size:11pt; color:#888;'>MindFull - Mental Wellbeing App</span>");
        html.append("</div>");
        html.append("<hr style='border:none; border-top:1px solid #ddd; margin:8px 0;'>");

        String[] lines = laporan.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                html.append("<br>");
            } else if (trimmed.startsWith("===") || trimmed.startsWith("---")) {
                html.append("<hr style='border:none; border-top:1px solid #e0e0e0; margin:6px 0;'>");
            } else if (trimmed.toUpperCase().equals(trimmed) && trimmed.length() > 3 && !trimmed.contains(":")) {
                html.append("<p style='margin:10px 0 5px 0; font-size:13pt; font-weight:bold; color:#2c3e50;'>").append(trimmed).append("</p>");
            } else if (trimmed.startsWith("-") || trimmed.startsWith("\u2022")) {
                html.append("<p style='margin:3px 0 3px 18px; font-size:12pt; color:#555;'>").append(trimmed).append("</p>");
            } else if (trimmed.contains(":")) {
                int idx = trimmed.indexOf(":");
                String key = trimmed.substring(0, idx + 1);
                String val = trimmed.substring(idx + 1);
                html.append("<p style='margin:4px 0; font-size:12pt;'><b style='color:#34495e;'>").append(key).append("</b><span style='color:#555;'>").append(val).append("</span></p>");
            } else {
                html.append("<p style='margin:3px 0; font-size:12pt; color:#555;'>").append(trimmed).append("</p>");
            }
        }

        html.append("<hr style='border:none; border-top:1px solid #ddd; margin:12px 0 6px 0;'>");
        html.append("<p style='font-size:8pt; color:#bbb; text-align:right;'>Generated by MindFull App</p>");
        html.append("</body></html>");

        // JEditorPane untuk render HTML yang rapi
        javax.swing.JEditorPane editorPane = new javax.swing.JEditorPane("text/html", html.toString());
        editorPane.setEditable(false);
        editorPane.setBackground(new Color(255, 245, 238));
        editorPane.putClientProperty(javax.swing.JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Bungkus dalam panel kertas
        JPanel paper = new JPanel(new BorderLayout());
        paper.setBackground(new Color(255, 245, 238));
        paper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 140, 130), 1),
            new EmptyBorder(25, 35, 25, 35)
        ));
        paper.add(editorPane, BorderLayout.CENTER);
        return paper;
    }

    // --- Helper UI Methods ---

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_REGULAR);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 200, 190));
        table.setBackground(COLOR_CARD);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(220, 160, 150));
    }

    private String getHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}