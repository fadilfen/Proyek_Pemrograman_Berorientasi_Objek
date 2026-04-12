package tubes_a11;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;

public class MentalWellbeingApp extends JFrame {
    private User user;
    private JLabel tokenLabel, scoreLabel, screenTimeLabel;
    private DefaultTableModel activityModel;
    
    // --- Palette Warna Modern ---
    private final Color COLOR_PRIMARY = new Color(74, 144, 226); // Blue
    private final Color COLOR_SUCCESS = new Color(46, 204, 113); // Green
    private final Color COLOR_DANGER  = new Color(231, 76, 60);  // Red
    private final Color COLOR_BG      = new Color(248, 249, 250); // Light Grayish Blue
    private final Color COLOR_CARD    = Color.WHITE;
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
        toRegisterBtn.setForeground(COLOR_PRIMARY);
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
        toLoginBtn.setForeground(COLOR_PRIMARY);
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
                user = new User(1, namaLengkap, 50);
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
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(20, 20));

        // --- Header / Dashboard Cards ---
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(25, 25, 10, 25));

        tokenLabel = createDashboardCard("Tokens Available", "0", new Color(155, 89, 182));
        scoreLabel = createDashboardCard("Wellness Score", "0", COLOR_SUCCESS);
        screenTimeLabel = createDashboardCard("Screen Time", "0 min", COLOR_PRIMARY);

        headerPanel.add(tokenLabel);
        headerPanel.add(scoreLabel);
        headerPanel.add(screenTimeLabel);

        // --- Tabs ---
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.contentAreaColor", Color.WHITE);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBorder(new EmptyBorder(10, 25, 25, 25));

        tabs.addTab("  Activity Tracker  ", createActivityPanel());
        tabs.addTab("  Top Up Balance  ", createTopUpPanel());
        tabs.addTab("  Health Report  ", createReportPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        
        refreshDashboard();
        setVisible(true);
    }

    private JLabel createDashboardCard(String title, String value, Color accent) {
        JLabel label = new JLabel("<html><div style='text-align: center; padding: 10px;'>"
                + "<span style='font-size: 10px; color: gray;'>" + title + "</span><br>"
                + "<span style='font-size: 18px; font-weight: bold; color: " + getHex(accent) + ";'>" + value + "</span>"
                + "</div></html>");
        label.setOpaque(true);
        label.setBackground(COLOR_CARD);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, accent));
        return label;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

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
                refreshDashboard();
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
                refreshDashboard();
                JOptionPane.showMessageDialog(this, "Top up berhasil ditambahkan!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak valid");
            }
        });

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(250, 250, 250));
        
        JButton generate = createStyledButton("Generate Summary Report", COLOR_PRIMARY);
        generate.addActionListener(e -> area.setText(user.lihatLaporan().generateLaporan()));

        panel.add(generate, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // --- Helper UI Methods ---
    
    private void refreshDashboard() {
        tokenLabel.setText("<html><div style='text-align: center;'><span style='font-size: 10px; color: gray;'>Tokens Available</span><br>"
                + "<span style='font-size: 18px; font-weight: bold; color: #9b59b6;'>" + user.getToken() + "</span></div></html>");
        scoreLabel.setText("<html><div style='text-align: center;'><span style='font-size: 10px; color: gray;'>Wellness Score</span><br>"
                + "<span style='font-size: 18px; font-weight: bold; color: #2ecc71;'>" + user.hitungScoreKesehatan() + "</span></div></html>");
        screenTimeLabel.setText("<html><div style='text-align: center;'><span style='font-size: 10px; color: gray;'>Screen Time</span><br>"
                + "<span style='font-size: 18px; font-weight: bold; color: #4a90e2;'>" + user.hitungTotalScreenTime() + " min</span></div></html>");
    }

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
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
    }

    private String getHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}