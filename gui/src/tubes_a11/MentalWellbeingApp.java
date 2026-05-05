package tubes_a11;

import database.DatabaseHelper;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
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


    // ════════════════════════════════════════════════════════════════════════
    // PALET WARNA — sama dengan style.css di proyek Spring Boot
    // ════════════════════════════════════════════════════════════════════════
    private static final Color BG_DEEP    = new Color(0x0f, 0x11, 0x17); // background
    private static final Color BG_SURFACE = new Color(0x1a, 0x1d, 0x27); // card
    private static final Color BG_SURFACE2= new Color(0x21, 0x25, 0x3a); // elevated
    private static final Color BG_SIDEBAR = new Color(0x13, 0x15, 0x1f); // sidebar

    private static final Color PRIMARY    = new Color(0x63, 0x66, 0xf1); // indigo
    private static final Color PRIMARY_DIM= new Color(99, 102, 241, 38); // indigo 15%
    private static final Color SUCCESS    = new Color(0x22, 0xc5, 0x5e); // hijau
    private static final Color SUCCESS_DIM= new Color(34, 197, 94, 30);
    private static final Color WARNING    = new Color(0xf5, 0x9e, 0x0b); // kuning
    private static final Color DANGER     = new Color(0xef, 0x44, 0x44); // merah
    private static final Color DANGER_DIM = new Color(239, 68, 68, 30);
    private static final Color INFO       = new Color(0x38, 0xbd, 0xf8); // biru
    private static final Color INFO_DIM   = new Color(56, 189, 248, 30);

    private static final Color TEXT_1     = new Color(0xf1, 0xf5, 0xf9); // heading
    private static final Color TEXT_2     = new Color(0x94, 0xa3, 0xb8); // body
    private static final Color TEXT_3     = new Color(0x47, 0x55, 0x69); // muted
    private static final Color BORDER     = new Color(255, 255, 255, 15);

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
                // Gradient latar
                GradientPaint gp = new GradientPaint(0,0,new Color(13,15,26),
                        getWidth(),getHeight(),new Color(18,20,40));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                // Lingkaran blur dekoratif (indigo glow)
                g2.setColor(new Color(99,102,241,35));
                g2.fillOval(getWidth()/2-140, getHeight()/2-140, 280, 280);
                g2.dispose();
            }
        };
        brand.setOpaque(false);
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx=0; bc.gridy=GridBagConstraints.RELATIVE;
        bc.insets=new Insets(8,30,8,30); bc.anchor=GridBagConstraints.CENTER;

        // Logo kotak indigo
        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setPreferredSize(new Dimension(64,64));
        logoBox.setBackground(PRIMARY);
        logoBox.setBorder(new EmptyBorder(0,0,0,0));
        JLabel logoEmoji = new JLabel("🧠", SwingConstants.CENTER);
        logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        logoBox.add(logoEmoji);

        JLabel appName = new JLabel("MindFull");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 32));
        appName.setForeground(TEXT_1);

        JLabel tagline = new JLabel(
            "<html><center style='color:#64748b'>Track digital habits.<br>Nurture mental wellbeing.</center></html>");
        tagline.setFont(F_BODY);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        brand.add(logoBox, bc); brand.add(appName, bc); brand.add(tagline, bc);

        // ── Panel kanan: form ─────────────────────────────────────────────
        JPanel formSide = new JPanel(new GridBagLayout());
        formSide.setBackground(BG_SURFACE);

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
                user = new User((Long) result[1], (String) result[0], (Integer) result[2]);
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
        // Kotak ikon indigo kecil
        JPanel ic = new JPanel(new BorderLayout());
        ic.setPreferredSize(new Dimension(28, 28));
        ic.setBackground(PRIMARY);
        JLabel icLbl = new JLabel("🧠", SwingConstants.CENTER);
        icLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        ic.add(icLbl);
        // Logo MindFull — font diperbesar jadi 18
        JLabel logoTxt = new JLabel("MindFull");
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoTxt.setForeground(TEXT_1);
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
        // Menggunakan JPanel bukan JButton untuk menghindari shadow artifact Nimbus L&F
        String[] nav  = {"Dashboard", "Activity Tracker", "Top Up Balance", "Health Report", "Profile"};
        Runnable[] acts = {this::showHomePage, this::showActivityPage,
                           this::showTopUpPage, this::showReportPage, this::showProfilePage};
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
     * Menandai item sidebar sebagai aktif (highlight indigo).
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
        // Tandai item baru sebagai aktif dengan warna indigo
        activeNavPanel = panel;
        panel.setBackground(PRIMARY_DIM);
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) ((JLabel) c).setForeground(new Color(0xa5, 0xb4, 0xfc));
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

        // ── 3 Stat Cards — mirip .stats-grid di web ─────────────────────────
        int score     = user.hitungScoreKesehatan();
        int screen    = user.hitungTotalScreenTime();

        JPanel grid = new JPanel(new GridLayout(1, 3, 16, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(0, 0, 20, 0));
        grid.add(statCard("TOKEN BALANCE",    String.valueOf(user.getToken()),
                          "Available tokens",      "🪙", PRIMARY, PRIMARY_DIM));
        grid.add(statCard("WELLNESS SCORE",   String.valueOf(score),
                          score >= 70 ? "Status: Sehat ✓" : "Perlu perhatian",
                          "💚", SUCCESS, SUCCESS_DIM));
        grid.add(statCard("SCREEN TIME",      screen + " min",
                          "Total semua aktivitas", "⏱", INFO, INFO_DIM));
        page.add(grid, BorderLayout.CENTER);

        // ── Quick Actions ──────────────────────────────────────────────────
        JPanel qa = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        qa.setOpaque(false);
        qa.add(lbl("Quick actions:", F_CAPTION, TEXT_3));
        JButton qaAct   = pillBtn("+ Log Activity");
        JButton qaTopup = pillBtn("+ Top Up");
        JButton qaRep   = pillBtn("View Report");
        qaAct.addActionListener(e -> showActivityPage());
        qaTopup.addActionListener(e -> showTopUpPage());
        qaRep.addActionListener(e -> showReportPage());
        qa.add(qaAct); qa.add(qaTopup); qa.add(qaRep);
        page.add(qa, BorderLayout.SOUTH);

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
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 20));
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
                g2.setColor(new Color(255,255,255,15));
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.setColor(accent);
                int w = Math.min(getWidth(), getWidth()); // selalu penuh untuk simplisitas
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
        balBox.add(lbl(user.getToken() + " token", F_HEADING, new Color(0xa5, 0xb4, 0xfc)),
                BorderLayout.EAST);

        // ── Field input jumlah token ─────────────────────────────────────
        // User memasukkan berapa token yang ingin dibeli (bukan nominal Rupiah)
        JTextField tokenField = new JTextField();
        tokenField.setFont(F_BODY);
        tokenField.setForeground(TEXT_1);
        tokenField.setBackground(BG_SURFACE);
        tokenField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)));
        tokenField.setCaretColor(TEXT_1);

        // ── Label preview harga (berubah real-time saat user mengetik) ───
        // Contoh: user ketik "5" → tampil "Total: Rp 5.000"
        JLabel previewLbl = new JLabel("Total biaya: Rp 0");
        previewLbl.setFont(F_BODY);
        previewLbl.setForeground(new Color(0xa5, 0xb4, 0xfc));

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

        genBtn.addActionListener(e -> {
            String laporan = user.lihatLaporan().generateLaporan();
            reportArea.removeAll();
            JEditorPane ep = new JEditorPane("text/html", buildReportHtml(laporan));
            ep.setEditable(false);
            ep.setBackground(BG_SURFACE);
            ep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            reportArea.add(ep, BorderLayout.CENTER);
            reportArea.revalidate(); reportArea.repaint();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false); topBar.add(genBtn);
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

    // Konversi teks laporan ke HTML dengan styling dark-indigo
    private String buildReportHtml(String laporan) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Segoe UI,Arial;background:#1a1d27;" +
                "color:#f1f5f9;margin:0;padding:16px;'>");
        sb.append("<div style='text-align:center;margin-bottom:14px;'>" +
                "<span style='font-size:15pt;font-weight:bold;color:#a5b4fc;'>" +
                "Health Summary Report</span><br>" +
                "<span style='font-size:10pt;color:#475569;'>MindFull</span></div>");
        sb.append("<hr style='border:none;border-top:1px solid rgba(255,255,255,0.06);margin:8px 0;'>");
        for (String line : laporan.split("\n")) {
            String t = line.trim();
            if (t.isEmpty()) { sb.append("<br>"); continue; }
            if (t.startsWith("===") || t.startsWith("---")) {
                sb.append("<hr style='border:none;border-top:1px solid rgba(255,255,255,0.06);margin:6px 0;'>");
            } else if (t.contains(":")) {
                int idx = t.indexOf(":");
                sb.append("<p style='margin:4px 0;font-size:11pt;'><b style='color:#94a3b8;'>")
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

    /** Card dengan rounded corner dan background BG_SURFACE */
    private JPanel glassCard(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
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

    /** Input field dengan placeholder, styling dark */
    private JTextField field(String placeholder) {
        JTextField tf = new JTextField(20);
        tf.setBackground(BG_DEEP);
        tf.setForeground(TEXT_1);
        tf.setCaretColor(TEXT_1);
        tf.setFont(F_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(9, 12, 9, 12)));
        tf.setPreferredSize(new Dimension(260, 38));
        // placeholder
        tf.setText(placeholder);
        tf.setForeground(TEXT_3);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText(""); tf.setForeground(TEXT_1);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder); tf.setForeground(TEXT_3);
                }
            }
        });
        return tf;
    }

    /** PasswordField dengan placeholder, styling dark */
    private JPasswordField passField(String placeholder) {
        JPasswordField pf = new JPasswordField(20);
        pf.setBackground(BG_DEEP);
        pf.setForeground(TEXT_3);
        pf.setCaretColor(TEXT_1);
        pf.setFont(F_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(9, 12, 9, 12)));
        pf.setPreferredSize(new Dimension(260, 38));
        pf.setEchoChar((char)0); // tampilkan placeholder sebagai teks
        pf.setText(placeholder);
        pf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(pf.getPassword()).equals(placeholder)) {
                    pf.setText(""); pf.setEchoChar('●'); pf.setForeground(TEXT_1);
                }
            }
            public void focusLost(FocusEvent e) {
                if (pf.getPassword().length == 0) {
                    pf.setEchoChar((char)0);
                    pf.setText(placeholder); pf.setForeground(TEXT_3);
                }
            }
        });
        return pf;
    }

    /** Tombol primary — indigo */
    private JButton btnPrimary(String text) {
        JButton b = new JButton(text);
        b.setFont(F_SUBHEAD);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(260, 40));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0x52,0x54,0xcc)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(PRIMARY); }
        });
        return b;
    }

    /** Tombol success — hijau */
    private JButton btnSuccess(String text) {
        JButton b = new JButton(text);
        b.setFont(F_SUBHEAD);
        b.setBackground(SUCCESS);
        b.setForeground(new Color(0x0c,0x1a,0x2e));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(260, 40));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0x16,0xa3,0x4a)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(SUCCESS); }
        });
        return b;
    }

    /** Tombol link teks kecil (teks saja, tanpa background) */
    private JButton btnLink(String text) {
        JButton b = new JButton(text);
        b.setFont(F_CAPTION);
        b.setForeground(new Color(0xa5, 0xb4, 0xfc));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(PRIMARY); }
            public void mouseExited(MouseEvent e)  { b.setForeground(new Color(0xa5,0xb4,0xfc)); }
        });
        return b;
    }

    /** Tombol pill kecil untuk Quick Actions */
    private JButton pillBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(F_CAPTION);
        b.setForeground(TEXT_2);
        b.setBackground(BG_SURFACE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(5, 14, 5, 14)));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(new Color(0xa5,0xb4,0xfc)); b.setBackground(PRIMARY_DIM); }
            public void mouseExited(MouseEvent e)  { b.setForeground(TEXT_2); b.setBackground(BG_SURFACE); }
        });
        return b;
    }

    /**
     * Membuat item navigasi sidebar menggunakan JPanel kustom.
     *
     * Fix ghost text:
     * - Override paintComponent untuk force-fill background sebelum rendering
     * - Gunakan warna solid (tanpa alpha) agar Nimbus tidak meninggalkan artefak
     * - min/pref/max height dikunci sama agar BoxLayout tidak tumpang-tindih
     *
     * @param text label teks item navigasi
     * @return JPanel yang berfungsi sebagai nav item
     */
    private JPanel navItem(String text) {
        // Warna hover solid (tanpa alpha) = BG_SIDEBAR dicampur putih tipis
        // Menghindari Nimbus meninggalkan ghost saat setBackground(alpha-color)
        final Color HOVER_COLOR  = new Color(0x23, 0x26, 0x38); // sidebar + sedikit lebih terang
        final Color NORMAL_COLOR = BG_SIDEBAR;

        JPanel p = new JPanel(new BorderLayout()) {
            // Override paintComponent: paksa background terfill penuh sebelum render isi
            @Override
            protected void paintComponent(Graphics g) {
                // Hapus area sebelumnya secara eksplisit (mencegah ghost text)
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                // Jangan panggil super.paintComponent agar Nimbus tidak ikut render
            }
        };
        p.setBackground(NORMAL_COLOR);
        p.setOpaque(false); // false agar kita yang handle painting sendiri
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Kunci 3 ukuran agar BoxLayout tidak menambah/mengurangi tinggi
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.setPreferredSize(new Dimension(196, 38));
        p.setMinimumSize(new Dimension(0, 38));

        // Label teks rata tengah sidebar
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(F_BODY);
        lbl.setForeground(TEXT_2);
        p.add(lbl, BorderLayout.CENTER);

        // Hover: ubah ke warna solid (bukan alpha) agar tidak ada ghost
        p.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (p != activeNavPanel) {
                    p.setBackground(HOVER_COLOR);
                    p.repaint();
                }
            }
            public void mouseExited(MouseEvent e) {
                if (p != activeNavPanel) {
                    p.setBackground(NORMAL_COLOR);
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
                GradientPaint gp = new GradientPaint(0,0,PRIMARY,size,size,new Color(0x81,0x8c,0xf8));
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

    /** Terapkan style dark ke ComboBox yang sudah ada */
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(BG_DEEP);
        cb.setForeground(TEXT_1);
        cb.setFont(F_BODY);
        cb.setPreferredSize(new Dimension(260, 38));
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }

    /** JTable dengan styling dark-indigo */
    private void styleTable(JTable table) {
        table.setBackground(BG_SURFACE);
        table.setForeground(TEXT_2);
        table.setFont(F_BODY);
        table.setGridColor(BORDER);
        table.setRowHeight(38);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(PRIMARY_DIM);
        table.setSelectionForeground(TEXT_1);
        table.setFillsViewportHeight(true);
        // Header tabel
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_SIDEBAR);
        header.setForeground(TEXT_3);
        header.setFont(F_CAPTION);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
    }

    /** JScrollPane transparan untuk tabel */
    private JScrollPane styledScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(false);
        sp.getViewport().setBackground(BG_SURFACE);
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
        panel.setBackground(BG_SURFACE2);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 1, true),
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