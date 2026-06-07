package tubes_a11;

import database.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * MentalWellbeingApp — Aplikasi GUI MindFull berbasis Java Swing.
 * Desain mengikuti tema dark-indigo dari proyek Spring Boot (demo/).
 *
 * Warna utama:
 *   BG_DEEP   = #0f1117 (background utama)
 *   BG_SURFACE = #1a1d27 (card/panel)
 *   PRIMARY    = #6366f1 (indigo)
 *   SUCCESS    = #22c55e (hijau)
 *   DANGER     = #ef4444 (merah)
 *   TEXT_1     = #f1f5f9 (teks utama)
 *   TEXT_3     = #475569 (teks muted)
 */
public class MentalWellbeingApp extends JFrame {

    // ── Data pengguna yang sedang login ──────────────────────────────────
    private User   user;
    private String currentUsername;

    // ── Panel referensi untuk update stat cards di dashboard ─────────────
    private JPanel contentPanel;
    private DefaultTableModel activityModel;

    // ── Panel nav aktif di sidebar (JPanel, bukan JButton, agar bebas shadow Nimbus) ──
    private JPanel activeNavPanel = null;

    // ── Timer untuk simulasi My Apps ───────────────────────────────────────
    private Timer simulatorTimer = null;

    // ════════════════════════════════════════════════════════════════════════
    // PALET WARNA — Tema Cerah & Modern (Soft Blue & White)
    // ════════════════════════════════════════════════════════════════════════
    private static final Color BG_DEEP    = new Color(0xf0, 0xf4, 0xf8); // background terang
    private static final Color BG_SURFACE = new Color(0xff, 0xff, 0xff); // card putih
    private static final Color BG_SURFACE2= new Color(0xf8, 0xfa, 0xfc); // elevated
    private static final Color BG_SIDEBAR = new Color(0xe8, 0xf0, 0xf8); // sidebar biru muda

    private static final Color PRIMARY    = new Color(0x3b, 0x82, 0xf6); // biru cerah
    private static final Color PRIMARY_DIM= new Color(59, 130, 246, 50); // biru transparan
    private static final Color SUCCESS    = new Color(0x10, 0xb9, 0x81); // hijau emerald
    private static final Color SUCCESS_DIM= new Color(16, 185, 129, 40);
    private static final Color WARNING    = new Color(0xf5, 0x9e, 0x0b); // kuning
    private static final Color DANGER     = new Color(0xef, 0x44, 0x44); // merah
    private static final Color DANGER_DIM = new Color(239, 68, 68, 40);
    private static final Color INFO       = new Color(0x06, 0xb6, 0xd4); // cyan
    private static final Color INFO_DIM   = new Color(6, 182, 212, 40);

    private static final Color TEXT_1     = new Color(0x1e, 0x29, 0x3b); // heading gelap
    private static final Color TEXT_2     = new Color(0x47, 0x55, 0x69); // body abu
    private static final Color TEXT_3     = new Color(0x94, 0xa3, 0xb8); // muted
    private static final Color BORDER     = new Color(0xe5, 0xe7, 0xeb); // border abu terang

    // ── Font ──────────────────────────────────────────────────────────────
    private static final Font F_DISPLAY = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_SUBHEAD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_CAPTION = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_NUM     = new Font("Segoe UI", Font.BOLD, 30);

    // ════════════════════════════════════════════════════════════════════════
    // KONSTRUKTOR — langsung tampilkan layar login
    // ════════════════════════════════════════════════════════════════════════
    public MentalWellbeingApp() {
        loginScreen();
    }

    // ════════════════════════════════════════════════════════════════════════
    // LOGIN SCREEN — split panel kiri (branding) + kanan (form)
    // ════════════════════════════════════════════════════════════════════════
    private void loginScreen() {
        JFrame f = new JFrame("MindFull – Login");
        f.setSize(860, 520);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridLayout(1, 2));

        // ── Panel kiri: branding ──────────────────────────────────────────
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Gradient latar biru cerah
                GradientPaint gp = new GradientPaint(0,0,new Color(59,130,246),
                        getWidth(),getHeight(),new Color(96,165,250));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                // Lingkaran blur dekoratif (putih glow)
                g2.setColor(new Color(255,255,255,60));
                g2.fillOval(getWidth()/2-140, getHeight()/2-140, 280, 280);
                g2.dispose();
            }
        };
        brand.setOpaque(false);
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx=0; bc.gridy=GridBagConstraints.RELATIVE;
        bc.insets=new Insets(8,30,8,30); bc.anchor=GridBagConstraints.CENTER;

        // Logo kotak biru
        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setPreferredSize(new Dimension(64,64));
        logoBox.setBackground(Color.WHITE);
        logoBox.setBorder(new EmptyBorder(0,0,0,0));
        JLabel logoEmoji = new JLabel("🧠", SwingConstants.CENTER);
        logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        logoBox.add(logoEmoji);

        JLabel appName = new JLabel("MindFull");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 32));
        appName.setForeground(Color.WHITE);

        JLabel tagline = new JLabel(
            "<html><center style='color:#e0f2fe'>Track digital habits.<br>Nurture mental wellbeing.</center></html>");
        tagline.setFont(F_BODY);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        brand.add(logoBox, bc); brand.add(appName, bc); brand.add(tagline, bc);

        // ── Panel kanan: form ─────────────────────────────────────────────
        JPanel formSide = new JPanel(new GridBagLayout());
        formSide.setBackground(new Color(0xf9, 0xfa, 0xfb));

        JPanel card = glassCard(new GridBagLayout());
        card.setPreferredSize(new Dimension(320, 360));

        JPanel loginForm = buildLoginForm(f, card);
        JPanel regForm   = buildRegisterForm(f, card, loginForm);
        loginForm.putClientProperty("regForm", regForm);
        card.add(loginForm);

        formSide.add(card);
        root.add(brand); root.add(formSide);
        f.setContentPane(root);
        f.setVisible(true);
    }

    // ── Form Login ────────────────────────────────────────────────────────
    private JPanel buildLoginForm(JFrame f, JPanel card) {
        JPanel p = new JPanel(new GridBagLayout()); p.setOpaque(false);
        GridBagConstraints g = gbc();

        JLabel title = lbl("Welcome back",   F_HEADING, TEXT_1);
        JLabel sub   = lbl("Masuk ke akun MindFull kamu", F_CAPTION, TEXT_3);
        JTextField    userField = field("Username");
        JPasswordField passField = passField("Password");
        JButton loginBtn  = btnPrimary("Sign In");
        JButton toRegBtn  = btnLink("Belum punya akun? Daftar sekarang");

        int row=0;
        g.gridy=row++; g.insets=new Insets(0,0,4,0);  p.add(title, g);
        g.gridy=row++; g.insets=new Insets(0,0,20,0); p.add(sub, g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); p.add(userField, g);
        g.gridy=row++; g.insets=new Insets(0,0,18,0); p.add(passField, g);
        g.gridy=row++; g.insets=new Insets(0,0,8,0);  p.add(loginBtn, g);
        g.gridy=row++;                                 p.add(toRegBtn, g);

        loginBtn.addActionListener(e -> {
            String uname = userField.getText().trim();
            String pwd   = new String(passField.getPassword());
            Object[] result = UserManager.login(uname, pwd);
            if (result != null) {
                currentUsername = uname;
                // result: [namaUser, idUser, token, role, parentId, umur]
                user = new User(
                    (Long) result[1],      // id
                    (String) result[0],    // namaUser
                    (Integer) result[2],   // token
                    (String) result[3],    // role
                    (Long) result[4],      // parentId
                    (Integer) result[5]    // umur
                );
                user.setUsername(uname);
                f.dispose();
                initUI();
            } else {
                toast(f, "Username atau password salah!", DANGER);
            }
        });

        toRegBtn.addActionListener(e -> {
            card.removeAll();
            JPanel reg = (JPanel) p.getClientProperty("regForm");
            card.add(reg); card.revalidate(); card.repaint();
        });
        return p;
    }

    // ── Form Register ─────────────────────────────────────────────────────
    private JPanel buildRegisterForm(JFrame f, JPanel card, JPanel loginForm) {
        JPanel p = new JPanel(new GridBagLayout()); p.setOpaque(false);
        GridBagConstraints g = gbc();

        JTextField    namaField = field("Nama Lengkap");
        JTextField    usrField  = field("Username");
        JPasswordField pwdField = passField("Password");
        JButton regBtn     = btnSuccess("Daftar");
        JButton toLoginBtn = btnLink("Sudah punya akun? Masuk");

        int row=0;
        g.gridy=row++; g.insets=new Insets(0,0,4,0);
        p.add(lbl("Buat Akun Baru", F_HEADING, TEXT_1), g);
        g.gridy=row++; g.insets=new Insets(0,0,18,0);
        p.add(lbl("Bergabung dengan MindFull hari ini", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,8,0);  p.add(namaField, g);
        g.gridy=row++; g.insets=new Insets(0,0,8,0);  p.add(usrField, g);
        g.gridy=row++; g.insets=new Insets(0,0,18,0); p.add(pwdField, g);
        g.gridy=row++; g.insets=new Insets(0,0,8,0);  p.add(regBtn, g);
        g.gridy=row++;                                 p.add(toLoginBtn, g);

        regBtn.addActionListener(e -> {
            String nama  = namaField.getText().trim();
            String uname = usrField.getText().trim();
            String pwd   = new String(pwdField.getPassword());
            if (nama.isEmpty() || uname.isEmpty() || pwd.isEmpty()) {
                toast(f, "Semua field harus diisi!", WARNING); return;
            }
            if (UserManager.register(uname, pwd, nama)) {
                toast(f, "Registrasi berhasil! Silakan login.", SUCCESS);
                card.removeAll(); card.add(loginForm);
                card.revalidate(); card.repaint();
            } else {
                toast(f, "Username sudah terdaftar!", DANGER);
            }
        });

        toLoginBtn.addActionListener(e -> {
            card.removeAll(); card.add(loginForm);
            card.revalidate(); card.repaint();
        });
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // MAIN WINDOW — sidebar + content panel
    // ════════════════════════════════════════════════════════════════════════
    private void initUI() {
        setTitle("MindFull – Mental Wellbeing");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DEEP);
        root.add(createSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_DEEP);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        showHomePage();
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createSidebar() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setBackground(BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(220, getHeight()));

        // ── Logo ───────────────────────────────────────────────────────────
        JPanel logoBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        logoBar.setOpaque(false);
        // Kotak ikon biru kecil
        JPanel ic = new JPanel(new BorderLayout());
        ic.setPreferredSize(new Dimension(28, 28));
        ic.setBackground(PRIMARY);
        JLabel icLbl = new JLabel("🧠", SwingConstants.CENTER);
        icLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        ic.add(icLbl);
        // Logo MindFull — font diperbesar jadi 18
        JLabel logoTxt = new JLabel("MindFull");
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoTxt.setForeground(PRIMARY);
        logoBar.add(ic); logoBar.add(logoTxt);
        sb.add(logoBar, BorderLayout.NORTH);

        // ── Tengah: avatar + nav ───────────────────────────────────────────
        JPanel mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setOpaque(false);
        mid.setBorder(new EmptyBorder(6, 12, 10, 12));

        // Avatar block — mirip .sidebar-profile di web
        JPanel avaBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        avaBlock.setOpaque(false);
        avaBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        avaBlock.setBackground(BG_SURFACE);
        JPanel ava = avatarBox(user.getNamaUser(), 34);
        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);
        JLabel nameLbl = lbl(user.getNamaUser(), F_SUBHEAD, TEXT_1);
        JLabel roleLbl = lbl("Member", F_CAPTION, TEXT_3);
        nameBox.add(nameLbl); nameBox.add(roleLbl);
        avaBlock.add(ava); avaBlock.add(nameBox);
        mid.add(avaBlock);
        mid.add(Box.createRigidArea(new Dimension(0, 10)));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        mid.add(sep);
        mid.add(Box.createRigidArea(new Dimension(0, 10)));

        // Label MENU — rata tengah sidebar
        JPanel menuRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        menuRow.setOpaque(false);
        menuRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel menuLbl = lbl("MENU", new Font("Segoe UI", Font.BOLD, 10), TEXT_3);
        menuRow.add(menuLbl);
        mid.add(menuRow);

        // ── Item navigasi sidebar ──────────────────────────────────────────────
        // Menu berbeda untuk parent dan child
        String[] nav;
        Runnable[] acts;
        
        if (user.isParent()) {
            nav = new String[]{"Dashboard", "Tracking Apps", "Manage Children", "Top Up Balance", "Health Report", "Profile"};
            acts = new Runnable[]{this::showHomePage, this::showParentAppsSimulatorPage, this::showManageChildrenPage,
                               this::showTopUpPage, this::showReportPage, this::showProfilePage};
        } else {
            nav = new String[]{"Dashboard", "My Apps", "Profile"};
            acts = new Runnable[]{this::showHomePage, this::showChildAppsPage, this::showProfilePage};
        }
        
        for (int i = 0; i < nav.length; i++) {
            // Buat nav item berupa JPanel kustom
            JPanel item = navItem(nav[i]);
            final Runnable act = acts[i];
            final JPanel  p   = item;
            item.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { setActiveNav(p); act.run(); }
                public void mouseEntered(MouseEvent e) {
                    // Hanya ubah warna jika bukan item aktif
                    if (p != activeNavPanel) {
                        p.setBackground(new Color(255, 255, 255, 18));
                    }
                }
                public void mouseExited(MouseEvent e) {
                    // Kembalikan ke warna asal jika bukan item aktif
                    if (p != activeNavPanel) {
                        p.setBackground(BG_SIDEBAR);
                    }
                }
            });
            if (i == 0) setActiveNav(item);
            mid.add(item);
            mid.add(Box.createRigidArea(new Dimension(0, 2)));
        }
        sb.add(mid, BorderLayout.CENTER);

        // ── Tombol logout — pakai JPanel kustom agar bebas shadow ──────────────
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bot.setBackground(BG_SIDEBAR);
        bot.setOpaque(true);
        bot.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));

        // Panel logout kustom (mirip navItem tapi warna danger saat hover)
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        logoutPanel.setBackground(BG_SIDEBAR);
        logoutPanel.setOpaque(true);
        logoutPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logoutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel logoutLbl = new JLabel("Logout");
        logoutLbl.setFont(F_BODY);
        logoutLbl.setForeground(TEXT_2);
        logoutPanel.add(logoutLbl);
        logoutPanel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutLbl.setForeground(DANGER);
                logoutPanel.setBackground(DANGER_DIM);
            }
            public void mouseExited(MouseEvent e) {
                logoutLbl.setForeground(TEXT_2);
                logoutPanel.setBackground(BG_SIDEBAR);
            }
            public void mouseClicked(MouseEvent e) {
                int ok = JOptionPane.showConfirmDialog(MentalWellbeingApp.this,
                        "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    DatabaseHelper.tutupKoneksi();
                    dispose();
                    new MentalWellbeingApp();
                }
            }
        });
        bot.add(logoutPanel);
        sb.add(bot, BorderLayout.SOUTH);
        return sb;
    }

    /**
     * Menandai item sidebar sebagai aktif (highlight biru).
     * Menggunakan JPanel kustom, bukan JButton, untuk menghindari shadow Nimbus.
     *
     * @param panel JPanel nav item yang diklik
     */
    private void setActiveNav(JPanel panel) {
        // Reset warna item yang sebelumnya aktif
        if (activeNavPanel != null) {
            activeNavPanel.setBackground(BG_SIDEBAR);
            // Reset label di dalam panel ke warna teks biasa
            for (Component c : activeNavPanel.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(TEXT_2);
            }
            activeNavPanel.repaint();
        }
        // Tandai item baru sebagai aktif dengan warna biru
        activeNavPanel = panel;
        panel.setBackground(PRIMARY_DIM);
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) ((JLabel) c).setForeground(PRIMARY);
        }
        panel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE: DASHBOARD
    // ════════════════════════════════════════════════════════════════════════
    private void showHomePage() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));

        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        header.add(lbl("Dashboard", F_DISPLAY, TEXT_1), BorderLayout.NORTH);
        header.add(lbl(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")), F_CAPTION, TEXT_3),
                BorderLayout.SOUTH);
        page.add(header, BorderLayout.NORTH);

        if (user.isParent()) {
            // Dashboard untuk Parent
            int avgScore = 100;
            int childrenCount = user.getChildAccounts().size();
            if (childrenCount > 0) {
                int totalScore = 0;
                for (User child : user.getChildAccounts()) {
                    child.memuatAktivitasDariDB();
                    totalScore += child.hitungScoreKesehatan();
                }
                avgScore = totalScore / childrenCount;
            }

            JPanel grid = new JPanel(new GridLayout(1, 3, 16, 0));
            grid.setOpaque(false);
            grid.setBorder(new EmptyBorder(0, 0, 20, 0));
            grid.add(statCard("TOKEN BALANCE",    String.valueOf(user.getToken()),
                              "Available tokens",      "🪙", PRIMARY, PRIMARY_DIM));
            grid.add(statCard("CHILDREN WELLNESS",   String.valueOf(avgScore),
                              avgScore >= 70 ? "Status: Aman ✓" : "Perlu perhatian",
                              "💚", SUCCESS, SUCCESS_DIM));
            grid.add(statCard("CHILDREN ACCOUNTS", String.valueOf(childrenCount),
                              "Total akun anak", "👶", INFO, INFO_DIM));
            page.add(grid, BorderLayout.CENTER);

            // Quick Actions untuk Parent
            JPanel qa = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            qa.setOpaque(false);
            qa.add(lbl("Quick actions:", F_CAPTION, TEXT_3));
            JButton qaChild = pillBtn("+ Add Child");
            JButton qaTopup = pillBtn("+ Top Up");
            JButton qaRep   = pillBtn("View Report");
            qaChild.addActionListener(e -> showManageChildrenPage());
            qaTopup.addActionListener(e -> showTopUpPage());
            qaRep.addActionListener(e -> showReportPage());
            qa.add(qaChild); qa.add(qaTopup); qa.add(qaRep);
            page.add(qa, BorderLayout.SOUTH);
        } else {
            // Dashboard untuk Child
            user.refreshAppTimers();
            int trackedApps = (int) user.getAppTimers().stream().filter(AppTimer::isTracking).count();
            long safeApps = user.getAppTimers().stream().filter(t -> t.isTracking() && t.isSafe()).count();
            
            JPanel grid = new JPanel(new GridLayout(1, 3, 16, 0));
            grid.setOpaque(false);
            grid.setBorder(new EmptyBorder(0, 0, 20, 0));
            grid.add(statCard("TRACKED APPS", String.valueOf(trackedApps),
                              "Aplikasi yang dimonitor", "📱", INFO, INFO_DIM));
            grid.add(statCard("SAFE APPS", String.valueOf(safeApps),
                              "Masih aman digunakan", "✅", SUCCESS, SUCCESS_DIM));
            grid.add(statCard("WARNING", String.valueOf(trackedApps - safeApps),
                              "Hampir/sudah habis", "⚠️", WARNING, new Color(245, 158, 11, 40)));
            page.add(grid, BorderLayout.CENTER);

            // Quick Actions untuk Child
            JPanel qa = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            qa.setOpaque(false);
            qa.add(lbl("Quick actions:", F_CAPTION, TEXT_3));
            JButton qaApps = pillBtn("View My Apps");
            qaApps.addActionListener(e -> showChildAppsPage());
            qa.add(qaApps);
            page.add(qa, BorderLayout.SOUTH);
        }

        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Stat card mirip .stat-card di style.css ───────────────────────────
    private JPanel statCard(String label, String value, String sub,
                            String icon, Color accent, Color dimColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                // Glow sudut kanan atas
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.fillOval(getWidth()-80, -30, 100, 100);
                // Border tipis
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 120));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Baris atas: label + badge ikon
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel labelLbl = lbl(label, F_CAPTION, TEXT_3);
        // Badge ikon kecil
        JPanel badge = new JPanel(new BorderLayout());
        badge.setPreferredSize(new Dimension(32, 32));
        badge.setBackground(dimColor);
        badge.setOpaque(true);
        badge.add(new JLabel(icon, SwingConstants.CENTER) {{
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        }});
        top.add(labelLbl, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);

        // Nilai besar
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(F_NUM);
        valLbl.setForeground(accent);
        valLbl.setBorder(new EmptyBorder(10, 0, 4, 0));

        // Sub-teks
        JLabel subLbl = lbl(sub, F_CAPTION, TEXT_3);

        // Progress bar tipis di bawah
        JPanel progress = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BORDER);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.setColor(accent);
                int w = Math.min(getWidth(), getWidth());
                g2.fillRoundRect(0, 0, w/2, 4, 4, 4);
                g2.dispose();
            }
        };
        progress.setOpaque(false);
        progress.setPreferredSize(new Dimension(0, 4));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.add(top); inner.add(valLbl); inner.add(subLbl);
        inner.add(Box.createRigidArea(new Dimension(0, 10)));
        inner.add(progress);

        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE: ACTIVITY TRACKER
    // ════════════════════════════════════════════════════════════════════════
    private void showActivityPage() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));
        page.add(pageHeader("Activity Tracker", "Catat aktivitas digital harian kamu"),
                BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 18, 0));
        body.setOpaque(false);

        // ── Form Card ──────────────────────────────────────────────────────
        JPanel formCard = glassCard(new GridBagLayout());
        GridBagConstraints g = gbc();

        String[] apps = {"TikTok","Instagram","YouTube","WhatsApp","Netflix","Spotify","Twitter","Facebook"};
        JComboBox<String> appCombo = styledCombo(apps);
        JTextField durField   = field("Durasi (menit)");
        JTextField limitField = field("Batas harian (menit)");

        // Pilih tanggal (hari/bulan/tahun)
        JComboBox<Integer> dayC  = new JComboBox<>();
        JComboBox<Integer> monC  = new JComboBox<>();
        JComboBox<Integer> yearC = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayC.addItem(i);
        for (int i = 1; i <= 12; i++) monC.addItem(i);
        int cy = LocalDate.now().getYear();
        for (int i = cy-1; i <= cy+1; i++) yearC.addItem(i);
        dayC.setSelectedItem(LocalDate.now().getDayOfMonth());
        monC.setSelectedItem(LocalDate.now().getMonthValue());
        yearC.setSelectedItem(cy);
        styleCombo(dayC); styleCombo(monC); styleCombo(yearC);

        JPanel dateRow = new JPanel(new GridLayout(1, 5, 6, 0));
        dateRow.setOpaque(false);
        dateRow.add(dayC); dateRow.add(lbl("/", F_BODY, TEXT_3));
        dateRow.add(monC); dateRow.add(lbl("/", F_BODY, TEXT_3));
        dateRow.add(yearC);

        JButton addBtn = btnPrimary("Log Activity  (−5 Token)");

        int row = 0;
        g.gridy=row++; formCard.add(lbl("App & Durasi", F_HEADING, TEXT_1), g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        formCard.add(lbl("Aplikasi", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); formCard.add(appCombo, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        formCard.add(lbl("Durasi (menit)", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); formCard.add(durField, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        formCard.add(lbl("Batas Harian (menit)", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); formCard.add(limitField, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        formCard.add(lbl("Tanggal (DD/MM/YYYY)", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,18,0); formCard.add(dateRow, g);
        g.gridy=row++;   g.insets=new Insets(0,0,0,0); formCard.add(addBtn, g);

        // ── Table Card ─────────────────────────────────────────────────────
        JPanel tableCard = glassCard(new BorderLayout(0, 14));
        tableCard.add(lbl("Activity Log", F_HEADING, TEXT_1), BorderLayout.NORTH);

        activityModel = new DefaultTableModel(
                new String[]{"Aplikasi","Durasi","Batas","Tanggal","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Muat data dari user (sudah di-load dari DB)
        user.memuatAktivitasDariDB();
        for (AktivitasDigital a : user.getAktivitasList()) {
            String status  = a.melebihiBatas() ? "OVER LIMIT" : "HEALTHY";
            LocalDate tgl  = a.getTanggal();
            String dateStr = String.format("%02d/%02d/%d",
                    tgl.getDayOfMonth(), tgl.getMonthValue(), tgl.getYear());
            activityModel.addRow(new Object[]{
                a.getNamaAplikasi(), a.getDurasiMenit(),
                a.getBatasDurasi(), dateStr, status});
        }

        JTable table = new JTable(activityModel);
        styleTable(table);
        tableCard.add(styledScroll(table), BorderLayout.CENTER);

        // ── Action: tambah aktivitas ───────────────────────────────────────
        addBtn.addActionListener(e -> {
            try {
                if (user.getToken() < 5) {
                    toast(this, "Token tidak cukup!", WARNING); return;
                }
                int d = (Integer) dayC.getSelectedItem();
                int m = (Integer) monC.getSelectedItem();
                int y = (Integer) yearC.getSelectedItem();
                AktivitasDigital act = new AktivitasDigital(
                        appCombo.getSelectedItem().toString(),
                        Integer.parseInt(durField.getText().trim()),
                        Integer.parseInt(limitField.getText().trim()),
                        LocalDate.of(y, m, d));
                user.tambahAktivitas(act);
                user.kurangiToken(5);
                toast(this, "Aktivitas berhasil dicatat!", SUCCESS);
                showActivityPage(); // refresh halaman
            } catch (Exception ex) {
                toast(this, "Data tidak valid: " + ex.getMessage(), DANGER);
            }
        });

        body.add(formCard); body.add(tableCard);
        page.add(body, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE: TOP UP BALANCE
    // ════════════════════════════════════════════════════════════════════════
    private void showTopUpPage() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));
        page.add(pageHeader("Top Up Balance", "Beli token untuk mencatat aktivitas"), BorderLayout.NORTH);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        JPanel card = glassCard(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 360));
        GridBagConstraints g = gbc();
        int row = 0;

        // ── Kotak info saldo saat ini ────────────────────────────────────
        JPanel balBox = new JPanel(new BorderLayout());
        balBox.setBackground(PRIMARY_DIM); balBox.setOpaque(true);
        balBox.setBorder(new EmptyBorder(12, 16, 12, 16));
        balBox.add(lbl("Token Saat Ini", F_CAPTION, TEXT_2), BorderLayout.WEST);
        balBox.add(lbl(user.getToken() + " token", F_HEADING, PRIMARY),
                BorderLayout.EAST);

        // ── Field input jumlah token ─────────────────────────────────────
        // User memasukkan berapa token yang ingin dibeli (bukan nominal Rupiah)
        JTextField tokenField = new JTextField();
        tokenField.setFont(F_BODY);
        tokenField.setForeground(TEXT_1);
        tokenField.setBackground(Color.WHITE);
        tokenField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));
        tokenField.setCaretColor(TEXT_1);

        // ── Label preview harga (berubah real-time saat user mengetik) ───
        // Contoh: user ketik "5" → tampil "Total: Rp 5.000"
        JLabel previewLbl = new JLabel("Total biaya: Rp 0");
        previewLbl.setFont(F_BODY);
        previewLbl.setForeground(PRIMARY);

        // Update preview setiap kali teks berubah
        tokenField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                try {
                    String teks = tokenField.getText().trim();
                    if (teks.isEmpty() || !teks.matches("[0-9]+")) {
                        previewLbl.setText("Total biaya: Rp 0");
                        return;
                    }
                    int jumlahToken = Integer.parseInt(teks);
                    int totalRupiah = jumlahToken * TopUp.HARGA_PER_TOKEN;
                    // Format angka dengan pemisah titik: 5000 → 5.000
                    String hargaFormat = String.format("%,d", totalRupiah).replace(",", ".");
                    previewLbl.setText("Total biaya: Rp " + hargaFormat);
                } catch (NumberFormatException ex) {
                    previewLbl.setText("Total biaya: Rp 0");
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        JComboBox<String> metodeBox = styledCombo(new String[]{"QRIS (Instant)", "Bank Transfer", "E-Wallet"});
        JButton topupBtn = btnSuccess("Konfirmasi Top Up");

        // ── Susun komponen ke dalam card ─────────────────────────────────
        g.gridy=row++; card.add(lbl("Detail Top Up", F_HEADING, TEXT_1), g);
        g.gridy=row++; g.insets=new Insets(10,0,10,0); card.add(balBox, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        card.add(lbl("Jumlah Token yang Dibeli", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,4,0);   card.add(tokenField, g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0);  card.add(previewLbl, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        card.add(lbl("Metode Pembayaran", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,18,0);  card.add(metodeBox, g);
        g.gridy=row++;  g.insets=new Insets(0,0,0,0);  card.add(topupBtn, g);

        // ── Listener tombol konfirmasi ───────────────────────────────────
        topupBtn.addActionListener(e -> {
            String inputTeks = tokenField.getText().trim();

            // Validasi: harus angka positif
            if (inputTeks.isEmpty() || !inputTeks.matches("[0-9]+")) {
                toast(this, "Masukkan jumlah token dalam angka (mis. 5)", DANGER);
                return;
            }
            int jumlahToken = Integer.parseInt(inputTeks);
            if (jumlahToken < 1) {
                toast(this, "Jumlah token minimal 1", DANGER);
                return;
            }

            // Hitung total biaya
            int totalRupiah = jumlahToken * TopUp.HARGA_PER_TOKEN;
            String hargaFormat = String.format("%,d", totalRupiah).replace(",", ".");
            String metode = metodeBox.getSelectedItem().toString();

            // Tampilkan dialog konfirmasi sebelum proses
            int konfirmasi = JOptionPane.showConfirmDialog(
                    this,
                    "Konfirmasi Top Up:\n\n"
                    + "  Jumlah Token : " + jumlahToken + " token\n"
                    + "  Total Biaya  : Rp " + hargaFormat + "\n"
                    + "  Pembayaran   : " + metode + "\n\n"
                    + "Lanjutkan pembayaran?",
                    "Konfirmasi Pembayaran",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (konfirmasi != JOptionPane.YES_OPTION) return;

            // Proses top up ke database
            try {
                TopUp topUp = new TopUp(jumlahToken, metode);
                topUp.prosesTopUp(user);
                tokenField.setText("");
                previewLbl.setText("Total biaya: Rp 0");
                toast(this, "Top up berhasil! +" + jumlahToken + " token ditambahkan", SUCCESS);
                showHomePage(); // kembali ke dashboard
            } catch (Exception ex) {
                toast(this, "Gagal memproses: " + ex.getMessage(), DANGER);
            }
        });

        wrap.add(card);
        page.add(wrap, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    // ════════════════════════════════════════════════════════════════════════
    // PAGE: HEALTH REPORT
    // ════════════════════════════════════════════════════════════════════════
    private void showReportPage() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));
        page.add(pageHeader("Health Report", "Ringkasan kesehatan digital kamu"), BorderLayout.NORTH);

        JPanel card = glassCard(new BorderLayout(0, 16));
        JButton genBtn = btnPrimary("Generate Summary Report");
        JPanel reportArea = new JPanel(new BorderLayout());
        reportArea.setOpaque(false);
        
        JComboBox<String> targetCombo = new JComboBox<>();
        if (!user.isParent()) {
            targetCombo.addItem("My Report");
        } else {
            for (User child : user.getChildAccounts()) {
                targetCombo.addItem("Anak: " + child.getNamaUser());
            }
        }
        styleCombo(targetCombo);

        genBtn.addActionListener(e -> {
            String laporan = "";
            if (!user.isParent()) {
                user.refreshAppTimers();
                laporan = buildAppTimerReport(user);
            } else {
                if (user.getChildAccounts().isEmpty()) {
                    toast(this, "Belum ada akun anak!", WARNING);
                    return;
                }
                int childIdx = targetCombo.getSelectedIndex();
                User child = user.getChildAccounts().get(childIdx);
                child.refreshAppTimers(); // Muat aktivitas real-time dari timer
                laporan = buildAppTimerReport(child);
            }
            
            reportArea.removeAll();
            JEditorPane ep = new JEditorPane("text/html", buildReportHtml(laporan));
            ep.setEditable(false);
            ep.setBackground(Color.WHITE);
            ep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            reportArea.add(ep, BorderLayout.CENTER);
            reportArea.revalidate(); reportArea.repaint();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        if (user.isParent() && targetCombo.getItemCount() > 0) {
            topBar.add(targetCombo);
        }
        topBar.add(genBtn);
        card.add(topBar, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(reportArea);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        card.add(sp, BorderLayout.CENTER);

        page.add(card, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private String buildAppTimerReport(User target) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN HARIAN ===\n");
        sb.append("Nama User         : ").append(target.getNamaUser()).append("\n");
        sb.append("Tanggal           : ").append(java.time.LocalDate.now().toString()).append("\n");
        sb.append("------------------------------\n");
        sb.append("Detail Aplikasi Real-Time:\n");

        int totalSetMinutes = 0;
        int totalUsedSeconds = 0;

        for (AppTimer t : target.getAppTimers()) {
            int usedSecs = (t.getDurationMinutes() * 60) - (int)t.getRemainingSeconds();
            int usedMins = usedSecs / 60;
            sb.append("- ").append(t.getAppName()).append(" : ")
              .append(usedMins).append(" menit (Digunakan dari ").append(t.getDurationMinutes()).append(" menit)\n");

            totalSetMinutes += t.getDurationMinutes();
            totalUsedSeconds += usedSecs;
        }

        sb.append("------------------------------\n");
        
        if (totalSetMinutes == 0) {
            sb.append("Belum ada aplikasi yang di-set.\n");
            sb.append("Wellness Score    : 0%\n");
        } else {
            int totalUsedMins = totalUsedSeconds / 60;
            int wellnessScore = (int)(((double)totalUsedMins / totalSetMinutes) * 100);
            sb.append("Total Diset       : ").append(totalSetMinutes).append(" menit\n");
            sb.append("Total Digunakan   : ").append(totalUsedMins).append(" menit\n");
            sb.append("Wellness Score    : ").append(wellnessScore).append("%\n");
        }
        
        return sb.toString();
    }

    // Konversi teks laporan ke HTML dengan styling cerah
    private String buildReportHtml(String laporan) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Segoe UI,Arial;background:#ffffff;" +
                "color:#1e293b;margin:0;padding:16px;'>");
        sb.append("<div style='text-align:center;margin-bottom:14px;'>" +
                "<span style='font-size:15pt;font-weight:bold;color:#3b82f6;'>" +
                "Health Summary Report</span><br>" +
                "<span style='font-size:10pt;color:#94a3b8;'>MindFull</span></div>");
        sb.append("<hr style='border:none;border-top:1px solid #e5e7eb;margin:8px 0;'>");
        for (String line : laporan.split("\n")) {
            String t = line.trim();
            if (t.isEmpty()) { sb.append("<br>"); continue; }
            if (t.startsWith("===") || t.startsWith("---")) {
                sb.append("<hr style='border:none;border-top:1px solid #e5e7eb;margin:6px 0;'>");
            } else if (t.contains(":")) {
                int idx = t.indexOf(":");
                sb.append("<p style='margin:4px 0;font-size:11pt;'><b style='color:#475569;'>")
                  .append(t, 0, idx+1)
                  .append("</b><span style='color:#64748b;'>")
                  .append(t.substring(idx+1)).append("</span></p>");
            } else {
                sb.append("<p style='margin:3px 0;font-size:11pt;color:#64748b;'>").append(t).append("</p>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE: PROFILE
    // ════════════════════════════════════════════════════════════════════════
    private void showProfilePage() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));
        page.add(pageHeader("Profile", "Kelola kredensial akun kamu"), BorderLayout.NORTH);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        JPanel card = glassCard(new GridBagLayout());
        card.setPreferredSize(new Dimension(400, 310));
        GridBagConstraints g = gbc();
        int row = 0;

        // Avatar + info nama
        JPanel avaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        avaRow.setOpaque(false);
        avaRow.add(avatarBox(user.getNamaUser(), 46));
        JPanel ni = new JPanel(); ni.setLayout(new BoxLayout(ni, BoxLayout.Y_AXIS)); ni.setOpaque(false);
        ni.add(lbl(user.getNamaUser(), F_HEADING, TEXT_1));
        ni.add(lbl("@" + currentUsername, F_CAPTION, TEXT_3));
        avaRow.add(ni);

        JTextField newUserField = field(currentUsername);
        newUserField.setText(currentUsername);
        JPasswordField newPassField = passField("Password baru");
        JButton updateBtn = btnPrimary("Update Credentials");

        g.gridy=row++; card.add(avaRow, g);
        g.gridy=row++; g.insets=new Insets(18,0,4,0);
        card.add(lbl("Username Baru", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); card.add(newUserField, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        card.add(lbl("Password Baru", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,18,0); card.add(newPassField, g);
        g.gridy=row++;   g.insets=new Insets(0,0,0,0); card.add(updateBtn, g);

        updateBtn.addActionListener(e -> {
            String uname = newUserField.getText().trim();
            String pwd   = new String(newPassField.getPassword());
            if (uname.isEmpty() || pwd.isEmpty()) {
                toast(this, "Username dan password tidak boleh kosong!", WARNING); return;
            }
            if (UserManager.updateCredentials(user.getId(), uname, pwd)) {
                currentUsername = uname; user.setUsername(uname);
                toast(this, "Credentials berhasil diupdate! ✓", SUCCESS);
            } else {
                toast(this, "Username sudah digunakan!", DANGER);
            }
        });

        wrap.add(card);
        page.add(wrap, BorderLayout.CENTER);
        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPER METHODS — komponen UI yang dipakai berulang
    // ════════════════════════════════════════════════════════════════════════

    /** Card dengan rounded corner dan background putih */
    private JPanel glassCard(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtle shadow effect
                g2.setColor(new Color(0, 0, 0, 5));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 24, 24);
                
                g2.setColor(BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 24, 24);
                
                g2.setColor(new Color(0xe2, 0xe8, 0xf0)); // lighter border
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 24, 24);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(28, 28, 28, 28));
        return p;
    }

    /** Header halaman: judul besar + subtitle kecil */
    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, 22, 0));
        h.add(lbl(title, F_DISPLAY, TEXT_1), BorderLayout.NORTH);
        h.add(lbl(sub, F_CAPTION, TEXT_3), BorderLayout.SOUTH);
        return h;
    }

    /** JLabel dengan font dan warna custom */
    private JLabel lbl(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    /** Input field dengan placeholder, styling cerah */
    private JTextField field(String placeholder) {
        JTextField tf = new JTextField(20);
        tf.setBackground(new Color(0xf8, 0xfa, 0xfc));
        tf.setForeground(TEXT_1);
        tf.setCaretColor(TEXT_1);
        tf.setFont(F_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xe2, 0xe8, 0xf0), 1, true),
                new EmptyBorder(12, 16, 12, 16)));
        tf.setPreferredSize(new Dimension(260, 44));
        // placeholder
        tf.setText(placeholder);
        tf.setForeground(TEXT_3);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText(""); tf.setForeground(TEXT_1);
                }
                tf.setBackground(Color.WHITE);
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2, true),
                    new EmptyBorder(11, 15, 11, 15)));
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder); tf.setForeground(TEXT_3);
                }
                tf.setBackground(new Color(0xf8, 0xfa, 0xfc));
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xe2, 0xe8, 0xf0), 1, true),
                    new EmptyBorder(12, 16, 12, 16)));
            }
        });
        return tf;
    }

    /** PasswordField dengan placeholder, styling cerah */
    private JPasswordField passField(String placeholder) {
        JPasswordField pf = new JPasswordField(20);
        pf.setBackground(new Color(0xf8, 0xfa, 0xfc));
        pf.setForeground(TEXT_3);
        pf.setCaretColor(TEXT_1);
        pf.setFont(F_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xe2, 0xe8, 0xf0), 1, true),
                new EmptyBorder(12, 16, 12, 16)));
        pf.setPreferredSize(new Dimension(260, 44));
        pf.setEchoChar((char)0); // tampilkan placeholder sebagai teks
        pf.setText(placeholder);
        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(pf.getPassword()).equals(placeholder)) {
                    pf.setText(""); pf.setEchoChar('●'); pf.setForeground(TEXT_1);
                }
                pf.setBackground(Color.WHITE);
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2, true),
                    new EmptyBorder(11, 15, 11, 15)));
            }
            public void focusLost(FocusEvent e) {
                if (pf.getPassword().length == 0) {
                    pf.setEchoChar((char)0);
                    pf.setText(placeholder); pf.setForeground(TEXT_3);
                }
                pf.setBackground(new Color(0xf8, 0xfa, 0xfc));
                pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xe2, 0xe8, 0xf0), 1, true),
                    new EmptyBorder(12, 16, 12, 16)));
            }
        });
        return pf;
    }

    private JButton createRoundedButton(String text, Color bg, Color hoverBg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24); // lebih melengkung
                super.paintComponent(g2); // Gambar teks
                g2.dispose();
            }
        };
        b.setFont(F_SUBHEAD);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(260, 42));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hoverBg); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    /** Tombol primary — biru cerah */
    private JButton btnPrimary(String text) {
        return createRoundedButton(text, PRIMARY, new Color(0x25, 0x63, 0xeb));
    }

    /** Tombol success — hijau emerald */
    private JButton btnSuccess(String text) {
        return createRoundedButton(text, SUCCESS, new Color(0x0d, 0x9e, 0x6d));
    }

    /** Tombol link teks kecil (teks saja, tanpa background) */
    private JButton btnLink(String text) {
        JButton b = new JButton(text);
        b.setFont(F_CAPTION);
        b.setForeground(PRIMARY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(new Color(0x1d, 0x4e, 0xd8)); }
            public void mouseExited(MouseEvent e)  { b.setForeground(PRIMARY); }
        });
        return b;
    }

    /** Tombol pill kecil untuk Quick Actions */
    private JButton pillBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(F_CAPTION);
        b.setForeground(TEXT_1);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(5, 14, 5, 14)));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(PRIMARY); b.setBackground(PRIMARY_DIM); }
            public void mouseExited(MouseEvent e)  { b.setForeground(TEXT_1); b.setBackground(Color.WHITE); }
        });
        return b;
    }

    /**
     * Membuat item navigasi sidebar menggunakan JPanel kustom.
     *
     * @param text label teks item navigasi
     * @return JPanel yang berfungsi sebagai nav item
     */
    private JPanel navItem(String text) {
        // Warna hover untuk tema cerah
        final Color HOVER_COLOR  = new Color(0xdb, 0xe9, 0xf7); // biru muda terang
        final Color NORMAL_COLOR = BG_SIDEBAR;

        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw pill background jika di-hover atau aktif
                if (getBackground() != NORMAL_COLOR) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(16, 0, getWidth() - 32, getHeight(), 24, 24);
                }
                
                // Indikator aktif di sebelah kiri
                if (this == activeNavPanel) {
                    g2.setColor(PRIMARY);
                    g2.fillRoundRect(16, getHeight()/4, 4, getHeight()/2, 4, 4);
                }
                g2.dispose();
            }
        };
        p.setBackground(NORMAL_COLOR);
        p.setOpaque(false);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.setPreferredSize(new Dimension(196, 44));
        p.setMinimumSize(new Dimension(0, 44));

        // Label teks rata kiri dengan padding
        JLabel lbl = new JLabel(text, SwingConstants.LEFT);
        lbl.setBorder(new EmptyBorder(0, 36, 0, 0));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_2);
        p.add(lbl, BorderLayout.CENTER);

        p.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (p != activeNavPanel) {
                    p.setBackground(HOVER_COLOR);
                    lbl.setForeground(PRIMARY);
                    p.repaint();
                }
            }
            public void mouseExited(MouseEvent e) {
                if (p != activeNavPanel) {
                    p.setBackground(NORMAL_COLOR);
                    lbl.setForeground(TEXT_2);
                    p.repaint();
                }
            }
        });
        return p;
    }


    /** Avatar bulat (kotak) dengan inisial huruf pertama */
    private JPanel avatarBox(String name, int size) {
        JPanel a = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,PRIMARY,size,size,new Color(0x60,0xa5,0xfa));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, size, size, size/3, size/3);
                g2.dispose();
            }
        };
        a.setOpaque(false);
        a.setPreferredSize(new Dimension(size, size));
        String ini = (name != null && !name.isEmpty()) ? name.substring(0,1).toUpperCase() : "?";
        JLabel l = new JLabel(ini, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, size/2));
        l.setForeground(Color.WHITE);
        a.add(l);
        return a;
    }

    /** ComboBox dengan styling dark */
    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        styleCombo(cb);
        return cb;
    }

    /** Terapkan style cerah ke ComboBox yang sudah ada */
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_1);
        cb.setFont(F_BODY);
        cb.setPreferredSize(new Dimension(260, 38));
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }

    /** JTable dengan styling cerah */
    private void styleTable(JTable table) {
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_1);
        table.setFont(F_BODY);
        table.setGridColor(BORDER);
        table.setRowHeight(38);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(PRIMARY_DIM);
        table.setSelectionForeground(PRIMARY);
        table.setFillsViewportHeight(true);
        // Header tabel
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_SURFACE2);
        header.setForeground(TEXT_2);
        header.setFont(F_CAPTION);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
    }

    /** JScrollPane transparan untuk tabel */
    private JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(false);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** GridBagConstraints default: satu kolom, fill horizontal */
    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;
        g.fill  = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(0, 0, 10, 0);
        return g;
    }

    /**
     * Toast notifikasi sementara di pojok kanan bawah window.
     * Muncul selama 2 detik lalu menghilang otomatis.
     */
    private void toast(Window parent, String msg, Color accent) {
        JWindow toast = new JWindow(parent);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2, true),
                new EmptyBorder(0, 0, 0, 4)));
        JLabel msgLbl = new JLabel("● " + msg);
        msgLbl.setFont(F_BODY);
        msgLbl.setForeground(accent);
        panel.add(msgLbl);
        toast.setContentPane(panel);
        toast.pack();
        // Posisi di pojok kanan bawah parent
        if (parent != null) {
            int px = parent.getX() + parent.getWidth()  - toast.getWidth()  - 20;
            int py = parent.getY() + parent.getHeight() - toast.getHeight() - 20;
            toast.setLocation(px, py);
        }
        toast.setVisible(true);
        // Timer hilangkan setelah 2 detik
        Timer t = new Timer(2000, e -> toast.dispose());
        t.setRepeats(false);
        t.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    // PAGE: MANAGE CHILDREN (Parent only)
    // ════════════════════════════════════════════════════════════════════════
    private void showManageChildrenPage() {
        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));
        page.add(pageHeader("Manage Children", "Kelola akun anak dan setting timer aplikasi"), BorderLayout.NORTH);
        
        JPanel body = new JPanel(new GridLayout(1, 2, 18, 0));
        body.setOpaque(false);
        
        // ── Left: Add Child Form ──────────────────────────────────────────
        JPanel leftCard = glassCard(new GridBagLayout());
        GridBagConstraints g = gbc();
        int row = 0;
        
        g.gridy=row++; leftCard.add(lbl("Tambah Akun Anak", F_HEADING, TEXT_1), g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        leftCard.add(lbl("Nama Lengkap", F_CAPTION, TEXT_3), g);
        JTextField childNameField = field("Nama anak");
        g.gridy=row++; g.insets=new Insets(0,0,10,0); leftCard.add(childNameField, g);
        
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        leftCard.add(lbl("Username", F_CAPTION, TEXT_3), g);
        JTextField childUserField = field("Username anak");
        g.gridy=row++; g.insets=new Insets(0,0,10,0); leftCard.add(childUserField, g);
        
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        leftCard.add(lbl("Umur", F_CAPTION, TEXT_3), g);
        JTextField childUmurField = field("Umur anak (contoh: 12)");
        g.gridy=row++; g.insets=new Insets(0,0,10,0); leftCard.add(childUmurField, g);
        
        g.gridy=row++; g.insets=new Insets(4,0,4,0);
        leftCard.add(lbl("Password", F_CAPTION, TEXT_3), g);
        JPasswordField childPassField = passField("Password anak");
        g.gridy=row++; g.insets=new Insets(0,0,18,0); leftCard.add(childPassField, g);
        
        JButton addChildBtn = btnSuccess("Tambah Akun Anak");
        g.gridy=row++; g.insets=new Insets(0,0,0,0); leftCard.add(addChildBtn, g);
        
        addChildBtn.addActionListener(e -> {
            String nama = childNameField.getText().trim();
            String username = childUserField.getText().trim();
            String umurStr = childUmurField.getText().trim();
            String password = new String(childPassField.getPassword());
            
            if (nama.isEmpty() || username.isEmpty() || password.isEmpty() || umurStr.isEmpty()) {
                toast(this, "Semua field harus diisi!", WARNING);
                return;
            }
            
            int umur = 0;
            try {
                umur = Integer.parseInt(umurStr);
            } catch (NumberFormatException ex) {
                toast(this, "Umur harus berupa angka!", WARNING);
                return;
            }
            
            if (UserManager.registerChild(user.getId(), username, password, nama, umur)) {
                toast(this, "Akun anak berhasil ditambahkan!", SUCCESS);
                childNameField.setText("");
                childUserField.setText("");
                childUmurField.setText("");
                childPassField.setText("");
                user.refreshChildAccounts();
                showManageChildrenPage(); // refresh
            } else {
                toast(this, "Username sudah digunakan!", DANGER);
            }
        });
        
        // ── Right: Children List ──────────────────────────────────────────
        JPanel rightCard = glassCard(new BorderLayout(0, 14));
        rightCard.add(lbl("Daftar Akun Anak", F_HEADING, TEXT_1), BorderLayout.NORTH);
        
        JPanel childrenPanel = new JPanel();
        childrenPanel.setLayout(new BoxLayout(childrenPanel, BoxLayout.Y_AXIS));
        childrenPanel.setOpaque(false);
        
        if (user.getChildAccounts().isEmpty()) {
            JLabel emptyLbl = lbl("Belum ada akun anak", F_BODY, TEXT_3);
            childrenPanel.add(emptyLbl);
        } else {
            for (User child : user.getChildAccounts()) {
                JPanel childCard = createChildCard(child);
                childrenPanel.add(childCard);
                childrenPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(childrenPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        rightCard.add(scrollPane, BorderLayout.CENTER);
        
        body.add(leftCard);
        body.add(rightCard);
        page.add(body, BorderLayout.CENTER);
        
        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Membuat card untuk child account dengan opsi set timer
     */
    private JPanel createChildCard(User child) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_SURFACE2);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Avatar + Info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        infoPanel.setOpaque(false);
        infoPanel.add(avatarBox(child.getNamaUser(), 40));
        
        JPanel textInfo = new JPanel();
        textInfo.setLayout(new BoxLayout(textInfo, BoxLayout.Y_AXIS));
        textInfo.setOpaque(false);
        textInfo.add(lbl(child.getNamaUser(), F_SUBHEAD, TEXT_1));
        textInfo.add(lbl("@" + child.getUsername(), F_CAPTION, TEXT_3));
        infoPanel.add(textInfo);
        
        card.add(infoPanel, BorderLayout.WEST);
        
        // Button Set Timer
        JButton setTimerBtn = new JButton("Set Timer");
        setTimerBtn.setFont(F_CAPTION);
        setTimerBtn.setBackground(PRIMARY);
        setTimerBtn.setForeground(Color.WHITE);
        setTimerBtn.setBorderPainted(false);
        setTimerBtn.setFocusPainted(false);
        setTimerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setTimerBtn.setPreferredSize(new Dimension(100, 30));
        
        setTimerBtn.addActionListener(e -> showSetTimerDialog(child));
        
        card.add(setTimerBtn, BorderLayout.EAST);
        
        return card;
    }
    
    /**
     * Dialog untuk setting timer aplikasi untuk child
     */
    private void showSetTimerDialog(User child) {
        JDialog dialog = new JDialog(this, "Set Timer - " + child.getNamaUser(), true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLbl = lbl("Setting Timer Aplikasi", F_HEADING, TEXT_1);
        panel.add(headerLbl, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints g = gbc();
        int row = 0;
        
        String[] apps = {"TikTok", "Instagram", "YouTube", "WhatsApp", "Netflix", "Spotify", "Twitter", "Facebook"};
        JComboBox<String> appCombo = styledCombo(apps);
        
        // Tanggal Picker
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(260, 38));
        dateSpinner.setFont(F_BODY);
        
        // Durasi Input
        JTextField durationField = field("Berapa menit?");
        
        g.gridy=row++; formPanel.add(lbl("Aplikasi", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); formPanel.add(appCombo, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0); formPanel.add(lbl("Tanggal", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); formPanel.add(dateSpinner, g);
        g.gridy=row++; g.insets=new Insets(4,0,4,0); formPanel.add(lbl("Durasi Waktu (Menit)", F_CAPTION, TEXT_3), g);
        g.gridy=row++; g.insets=new Insets(0,0,10,0); formPanel.add(durationField, g);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton trackBtn = btnSuccess("Generate Timer (−5 Token)");
        JButton cancelBtn = btnLink("Batal");
        
        trackBtn.addActionListener(e -> {
            if (user.getToken() < 5) {
                toast(this, "Token tidak cukup! Silakan top up.", WARNING);
                return;
            }
            
            try {
                String appName = appCombo.getSelectedItem().toString();
                
                java.util.Date dateVal = (java.util.Date) dateSpinner.getValue();
                java.time.LocalDate tanggal = dateVal.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                
                String durStr = durationField.getText().trim();
                if (durStr.equals("Berapa menit?")) durStr = "0"; // placeholder
                
                int duration;
                try {
                    duration = Integer.parseInt(durStr);
                } catch (NumberFormatException nfe) {
                    toast(this, "Durasi harus berupa angka bulat!", WARNING);
                    return;
                }
                
                if (duration <= 0) {
                    toast(this, "Durasi harus lebih dari 0 menit!", WARNING);
                    return;
                }
                
                java.time.LocalTime startTime = java.time.LocalTime.now();
                java.time.LocalTime endTime = startTime.plusMinutes(duration);
                
                DatabaseHelper.tambahAppTimer(child.getId(), appName, duration, tanggal, startTime, endTime);
                user.kurangiToken(5); // Deduct parent token
                
                toast(this, "Timer berhasil digenerate! −5 token", SUCCESS);
                dialog.dispose();
            } catch (Exception ex) {
                toast(this, "Error: Data tidak valid atau " + ex.getMessage(), DANGER);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(cancelBtn);
        btnPanel.add(trackBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // PAGE: MY APPS (Child only)
    // ════════════════════════════════════════════════════════════════════════
    private AppTimer activeSimulatedApp = null;
    private JPanel dummyPhoneContent = null;

    private void showParentAppsSimulatorPage() {
        if (user.getChildAccounts().isEmpty()) {
            contentPanel.removeAll();
            JPanel page = new JPanel(new BorderLayout(0, 20));
            page.setBackground(BG_DEEP);
            page.setBorder(new EmptyBorder(28, 32, 28, 32));
            page.add(pageHeader("Tracking Apps", "Pantau sisa waktu aplikasi anak"), BorderLayout.NORTH);
            JPanel empty = new JPanel(new FlowLayout());
            empty.setOpaque(false);
            empty.add(lbl("Belum ada akun anak untuk dipantau.", F_BODY, TEXT_3));
            page.add(empty, BorderLayout.CENTER);
            contentPanel.add(page);
            contentPanel.revalidate(); contentPanel.repaint();
        } else {
            showAppsSimulatorPage(user.getChildAccounts().get(0));
        }
    }

    private void showChildAppsPage() {
        showAppsSimulatorPage(user);
    }

    private void showAppsSimulatorPage(User targetUser) {
        if (simulatorTimer != null) {
            simulatorTimer.stop();
        }
        activeSimulatedApp = null;

        contentPanel.removeAll();
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG_DEEP);
        page.setBorder(new EmptyBorder(28, 32, 28, 32));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(pageHeader(user.isParent() ? "Tracking Apps" : "My Apps Simulator", 
            user.isParent() ? ("Memantau aktivitas " + targetUser.getNamaUser()) : "Simulasikan penggunaan aplikasi di handphone Anda dan pantau sisa batas waktu secara realtime."), BorderLayout.NORTH);
            
        if (user.isParent() && !user.getChildAccounts().isEmpty()) {
            JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topBar.setOpaque(false);
            topBar.add(lbl("Pilih Anak: ", F_BODY, TEXT_1));
            JComboBox<String> targetCombo = new JComboBox<>();
            int selectedIdx = 0;
            for (int i=0; i<user.getChildAccounts().size(); i++) {
                User child = user.getChildAccounts().get(i);
                targetCombo.addItem(child.getNamaUser());
                if (child.getId() == targetUser.getId()) selectedIdx = i;
            }
            targetCombo.setSelectedIndex(selectedIdx);
            styleCombo(targetCombo);
            targetCombo.addActionListener(e -> {
                int idx = targetCombo.getSelectedIndex();
                if (idx >= 0) {
                    showAppsSimulatorPage(user.getChildAccounts().get(idx));
                }
            });
            headerPanel.add(topBar, BorderLayout.SOUTH);
        }
        page.add(headerPanel, BorderLayout.NORTH);
        
        targetUser.refreshAppTimers();
        
        JPanel body = new JPanel(new GridLayout(1, user.isParent() ? 1 : 2, 24, 0));
        body.setOpaque(false);
        
        // ── KIRI: Daftar Aplikasi ───────────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout(0, 16));
        leftPanel.setOpaque(false);
        leftPanel.add(lbl("Daftar Aplikasi", F_HEADING, TEXT_1), BorderLayout.NORTH);
        
        JPanel appList = new JPanel();
        appList.setLayout(new BoxLayout(appList, BoxLayout.Y_AXIS));
        appList.setOpaque(false);
        
        // Buat map komponen untuk diupdate secara real-time oleh timer
        java.util.Map<AppTimer, JLabel> timeLabels = new java.util.HashMap<>();
        
        if (targetUser.getAppTimers().isEmpty()) {
            appList.add(lbl("Belum ada aplikasi yang di-track", F_BODY, TEXT_3));
        } else {
            for (AppTimer timer : targetUser.getAppTimers()) {
                if (timer.isTracking()) {
                    JLabel timeLbl = lbl(timer.getSimulatedRemainingTimeString(), F_SUBHEAD, timer.isSimulatedSafe() ? SUCCESS : WARNING);
                    timeLabels.put(timer, timeLbl);
                    JPanel row = createAppListRow(timer, timeLbl, () -> {
                        activeSimulatedApp = timer;
                        updateDummyPhoneView(body);
                    });
                    appList.add(row);
                    appList.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }
        
        JScrollPane scroll = new JScrollPane(appList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        leftPanel.add(scroll, BorderLayout.CENTER);
        
        body.add(leftPanel);
        
        if (!user.isParent()) {
            // ── KANAN: Dummy Phone ──────────────────────────────────────────
            JPanel rightPanel = buildDummyPhonePanel(body);
            body.add(rightPanel);
        }
        
        page.add(body, BorderLayout.CENTER);
        
        // ── Timer Global untuk Halaman Ini ──────────────────────────────
        simulatorTimer = new Timer(1000, e -> {
            if (activeSimulatedApp != null) {
                activeSimulatedApp.decrementRemainingSeconds();
                updateDummyPhoneView(body);
                // Sync to database
                try {
                    database.DatabaseHelper.updateAppTimerRemainingSeconds(activeSimulatedApp.getId(), activeSimulatedApp.getRemainingSeconds());
                } catch (Exception ex) {
                    System.err.println("[Timer] Gagal sync waktu ke DB: " + ex.getMessage());
                }
            }
            // Update label di list aplikasi menggunakan Map untuk memastikan object tepat
            for (java.util.Map.Entry<AppTimer, JLabel> entry : timeLabels.entrySet()) {
                AppTimer t = entry.getKey();
                JLabel lbl = entry.getValue();
                lbl.setText(t.getSimulatedRemainingTimeString());
                lbl.setForeground(t.isSimulatedSafe() ? SUCCESS : WARNING);
            }
            if (body != null) {
                body.revalidate();
                body.repaint();
            }
        });
        simulatorTimer.start();
        
        contentPanel.add(page);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createAppListRow(AppTimer timer, JLabel timeLbl, Runnable onOpen) {
        JPanel row = glassCard(new BorderLayout(14, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        
        // Icon
        JPanel iconBox = new JPanel(new BorderLayout());
        iconBox.setPreferredSize(new Dimension(48, 48));
        iconBox.setBackground(BG_DEEP);
        iconBox.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        JLabel icon = new JLabel(getAppEmoji(timer.getAppName()), SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconBox.add(icon);
        
        // Teks
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel nameLbl = lbl(timer.getAppName(), F_SUBHEAD, TEXT_1);
        JLabel subLbl = lbl("Simulasi penggunaan aplikasi.", F_CAPTION, TEXT_3);
        info.add(nameLbl);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(subLbl);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(timeLbl);
        
        // Tombol
        if (!user.isParent()) {
            JButton openBtn = btnPrimary("Buka");
            openBtn.setPreferredSize(new Dimension(80, 36));
            openBtn.addActionListener(e -> onOpen.run());
            row.add(openBtn, BorderLayout.EAST);
        }
        
        row.add(iconBox, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);
        return row;
    }
    
    /**
     * Mendapatkan emoji untuk aplikasi
     */
    private String getAppEmoji(String appName) {
        switch (appName) {
            case "TikTok": return "🎵";
            case "Instagram": return "📸";
            case "YouTube": return "🎥";
            case "WhatsApp": return "💬";
            case "Netflix": return "🎬";
            case "Spotify": return "🎶";
            case "Twitter": return "🐦";
            case "Facebook": return "👍";
            default: return "📱";
        }
    }
    
    private JPanel buildDummyPhonePanel(JPanel body) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        
        JPanel phone = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1e, 0x29, 0x3b)); // slate-800
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(new Color(0x33, 0x41, 0x55)); // slate-700
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 38, 38);
                g2.dispose();
            }
        };
        phone.setOpaque(false);
        phone.setPreferredSize(new Dimension(300, 560));
        phone.setBorder(new EmptyBorder(16, 12, 24, 12));
        
        // Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        statusBar.setBorder(new EmptyBorder(0, 10, 10, 10));
        statusBar.add(lbl(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), F_CAPTION, Color.WHITE), BorderLayout.WEST);
        statusBar.add(lbl("📶 100%", F_CAPTION, Color.WHITE), BorderLayout.EAST);
        
        // Content Area (Dynamic)
        dummyPhoneContent = new JPanel(new BorderLayout());
        dummyPhoneContent.setOpaque(false);
        
        phone.add(statusBar, BorderLayout.NORTH);
        phone.add(dummyPhoneContent, BorderLayout.CENTER);
        
        // Home indicator
        JPanel homeIndicatorPanel = new JPanel();
        homeIndicatorPanel.setOpaque(false);
        homeIndicatorPanel.setPreferredSize(new Dimension(0, 20));
        JPanel indicator = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 100));
                g.fillRoundRect(getWidth()/2 - 40, 8, 80, 4, 4, 4);
            }
        };
        indicator.setOpaque(false);
        indicator.setPreferredSize(new Dimension(100, 20));
        homeIndicatorPanel.add(indicator);
        phone.add(homeIndicatorPanel, BorderLayout.SOUTH);
        
        updateDummyPhoneView(body);
        wrapper.add(phone);
        return wrapper;
    }
    
    private void updateDummyPhoneView(JPanel body) {
        if (dummyPhoneContent == null) return;
        dummyPhoneContent.removeAll();
        
        if (activeSimulatedApp == null) {
            // Tampilan OS MindPhone
            JPanel home = new JPanel();
            home.setLayout(new BoxLayout(home, BoxLayout.Y_AXIS));
            home.setOpaque(false);
            
            JLabel title = lbl("MindPhone OS", new Font("Segoe UI", Font.BOLD, 18), Color.WHITE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sub = lbl("Pilih aplikasi di sebelah kiri untuk membukanya.", F_CAPTION, new Color(255,255,255,150));
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            home.add(Box.createRigidArea(new Dimension(0, 60)));
            home.add(title);
            home.add(Box.createRigidArea(new Dimension(0, 8)));
            home.add(sub);
            
            // Grid mini apps dekoratif
            JPanel grid = new JPanel(new GridLayout(3, 3, 16, 16));
            grid.setOpaque(false);
            grid.setMaximumSize(new Dimension(200, 200));
            grid.setBorder(new EmptyBorder(40, 0, 0, 0));
            
            String[] dummyApps = {"TikTok", "Instagram", "YouTube", "WhatsApp"};
            Color[] colors = {new Color(0x3b, 0x82, 0xf6), new Color(0xec, 0x48, 0x99), new Color(0xef, 0x44, 0x44), new Color(0x10, 0xb9, 0x81)};
            
            for (int i=0; i<dummyApps.length; i++) {
                String app = dummyApps[i];
                Color c = colors[i % colors.length];
                
                JPanel icon = new JPanel(new BorderLayout());
                icon.setOpaque(false);
                JLabel emoji = new JLabel(getAppEmoji(app), SwingConstants.CENTER);
                emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                JPanel bg = new JPanel(new BorderLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(c);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                        g2.dispose();
                    }
                };
                bg.setOpaque(false);
                bg.add(emoji);
                icon.add(bg, BorderLayout.CENTER);
                
                JLabel text = lbl(app, new Font("Segoe UI", Font.PLAIN, 10), Color.WHITE);
                text.setHorizontalAlignment(SwingConstants.CENTER);
                text.setBorder(new EmptyBorder(4,0,0,0));
                icon.add(text, BorderLayout.SOUTH);
                grid.add(icon);
            }
            home.add(grid);
            dummyPhoneContent.add(home, BorderLayout.CENTER);
        } else {
            // Tampilan Aplikasi Aktif
            JPanel appView = new JPanel();
            appView.setLayout(new BoxLayout(appView, BoxLayout.Y_AXIS));
            appView.setOpaque(false);
            
            JLabel emoji = new JLabel(getAppEmoji(activeSimulatedApp.getAppName()), SwingConstants.CENTER);
            emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
            emoji.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel title = lbl(activeSimulatedApp.getAppName(), new Font("Segoe UI", Font.BOLD, 24), Color.WHITE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel time = lbl(activeSimulatedApp.getSimulatedRemainingTimeString(), new Font("Segoe UI", Font.BOLD, 32), activeSimulatedApp.isSimulatedSafe() ? SUCCESS : WARNING);
            time.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            appView.add(Box.createRigidArea(new Dimension(0, 60)));
            appView.add(emoji);
            appView.add(Box.createRigidArea(new Dimension(0, 16)));
            appView.add(title);
            appView.add(Box.createRigidArea(new Dimension(0, 40)));
            appView.add(time);
            
            dummyPhoneContent.add(appView, BorderLayout.CENTER);
            
            JButton closeBtn = new JButton("Close App");
            closeBtn.setFont(F_SUBHEAD);
            closeBtn.setBackground(new Color(255,255,255,30));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
            closeBtn.setFocusPainted(false);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeBtn.addActionListener(e -> {
                activeSimulatedApp = null;
                updateDummyPhoneView(body);
            });
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setOpaque(false);
            btnPanel.setBorder(new EmptyBorder(0, 0, 40, 0));
            btnPanel.add(closeBtn);
            dummyPhoneContent.add(btnPanel, BorderLayout.SOUTH);
        }
        
        dummyPhoneContent.revalidate();
        dummyPhoneContent.repaint();
        if (body != null) body.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Terapkan Nimbus Look and Feel untuk tampilan yang lebih modern
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Jalankan di Event Dispatch Thread (EDT) agar thread-safe
        SwingUtilities.invokeLater(MentalWellbeingApp::new);
    }
}