"""laporan/bab4_implementasi.py - Bab 4 Implementasi"""
from .helpers import heading, para, separator, add_table, code_block


def tulis(doc):
    heading(doc, "BAB IV IMPLEMENTASI")
    separator(doc)

    # ── 4.1 KELAS OOP ──────────────────────────────────────────────────────
    heading(doc, "4.1 Implementasi Kelas dan Relasi OOP", level=2)
    para(doc,
        "Proyek MindFull dibangun berdasarkan lima kelas utama yang merepresentasikan entitas "
        "dalam sistem manajemen screen time. Setiap kelas dirancang mengikuti prinsip enkapsulasi "
        "dengan atribut bersifat private dan diakses melalui metode getter/setter.")

    heading(doc, "4.1.1 Kelas User", level=3)
    para(doc,
        "Kelas User merupakan kelas inti yang merepresentasikan pengguna aplikasi. Kelas ini "
        "menyimpan data identitas pengguna (idUser, namaUser, username, token) beserta daftar "
        "aktivitas digital yang telah dicatat. Kelas User memiliki metode utama sebagai berikut:")
    code_block(doc,
        "public class User {\n"
        "    private int idUser;\n"
        "    private String namaUser;\n"
        "    private String username;\n"
        "    private int token;\n"
        "    private ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();\n\n"
        "    // Menghitung total screen time dari semua aktivitas\n"
        "    public int hitungTotalScreenTime() {\n"
        "        int total = 0;\n"
        "        for (AktivitasDigital a : aktivitasList) total += a.getDurasiMenit();\n"
        "        return total;\n"
        "    }\n\n"
        "    // Menghitung skor kesehatan digital (0–100)\n"
        "    public int hitungScoreKesehatan() {\n"
        "        int score = 100;\n"
        "        for (AktivitasDigital a : aktivitasList) {\n"
        "            score -= a.getDurasiMenit() / 10;  // kurangi per 10 menit\n"
        "            if (a.melebihiBatas()) score -= 20; // penalti jika over limit\n"
        "        }\n"
        "        return Math.max(0, score);\n"
        "    }\n"
        "}"
    )

    heading(doc, "4.1.2 Kelas AktivitasDigital", level=3)
    para(doc,
        "Kelas AktivitasDigital merepresentasikan satu sesi penggunaan aplikasi digital oleh "
        "pengguna. Atribut utamanya meliputi namaAplikasi, durasiMenit, batasDurasi, dan tanggal. "
        "Metode melebihiBatas() mengembalikan nilai true apabila durasi aktual melebihi batas "
        "yang telah ditetapkan pengguna, dan digunakan sebagai trigger penalti skor.")
    code_block(doc,
        "public class AktivitasDigital {\n"
        "    private String namaAplikasi;\n"
        "    private int durasiMenit;\n"
        "    private int batasDurasi;\n"
        "    private LocalDate tanggal;\n\n"
        "    public boolean melebihiBatas() {\n"
        "        return durasiMenit > batasDurasi;\n"
        "    }\n"
        "}"
    )

    heading(doc, "4.1.3 Kelas TopUp", level=3)
    para(doc,
        "Kelas TopUp merepresentasikan transaksi penambahan saldo token pengguna. Kelas ini "
        "memiliki dependency terhadap kelas User, karena metode prosesTopUp() menerima objek User "
        "sebagai parameter dan memanggil User.tambahToken() untuk menambah saldo token.")
    code_block(doc,
        "public class TopUp {\n"
        "    private int jumlahKoin;\n"
        "    private String metodePembayaran; // QRIS, Bank Transfer, E-Wallet\n\n"
        "    public void prosesTopUp(User user) {\n"
        "        user.tambahToken(jumlahKoin);\n"
        "    }\n\n"
        "    public boolean validasiPembayaran() {\n"
        "        return jumlahKoin > 0;\n"
        "    }\n"
        "}"
    )

    heading(doc, "4.1.4 Kelas Notifikasi", level=3)
    para(doc,
        "Kelas Notifikasi bertanggung jawab mengirimkan pesan peringatan kepada pengguna "
        "berdasarkan perbandingan antara total screen time dengan batas yang ditentukan. "
        "Terdapat tiga kondisi: screen time di bawah batas (aman), tepat di batas (peringatan), "
        "dan melewati batas (darurat).")
    code_block(doc,
        "public class Notifikasi {\n"
        "    private int idNotifikasi;\n"
        "    private String pesan;\n\n"
        "    public void kirimPeringatan(int totalScreenTime, int limit) {\n"
        "        if (totalScreenTime < limit)\n"
        "            pesan = \"Screen time masih dalam batas yang ditentukan.\";\n"
        "        else if (totalScreenTime == limit)\n"
        "            pesan = \"Anda sudah mencapai batas screen time hari ini.\";\n"
        "        else\n"
        "            pesan = \"Anda sudah melebihi batas screen time!\";\n"
        "        System.out.println(\"Notifikasi: \" + pesan);\n"
        "    }\n"
        "}"
    )

    heading(doc, "4.1.5 Kelas LaporanHarian", level=3)
    para(doc,
        "Kelas LaporanHarian merepresentasikan ringkasan aktivitas digital pengguna dalam satu "
        "hari. Kelas ini dibuat melalui metode lihatLaporan() pada kelas User (komposisi), "
        "sehingga memiliki ketergantungan langsung terhadap siklus hidup objek User.")
    code_block(doc,
        "public class LaporanHarian {\n"
        "    private int totalDurasi;\n"
        "    private int skorHarian;\n"
        "    private ArrayList<AktivitasDigital> aktivitasList;\n"
        "    private String namaUser;\n\n"
        "    public String generateLaporan() {\n"
        "        String laporan = \"=== LAPORAN HARIAN ===\\n\";\n"
        "        laporan += \"Nama User  : \" + namaUser + \"\\n\";\n"
        "        laporan += \"Total ST   : \" + totalDurasi + \" menit\\n\";\n"
        "        laporan += \"Skor Harian: \" + skorHarian + \"\\n\";\n"
        "        laporan += \"Status     : \" + (skorHarian >= 70 ? \"Sehat\" : \"Kurangi Screen Time\");\n"
        "        return laporan;\n"
        "    }\n"
        "}"
    )

    heading(doc, "4.1.6 Relasi Antar Kelas", level=3)
    add_table(doc,
        ["Kelas 1", "Jenis Relasi", "Kelas 2", "Penjelasan"],
        [
            ["User",  "Komposisi (1..*)", "AktivitasDigital",
             "User memiliki daftar aktivitas. Jika User dihapus, aktivitasnya ikut terhapus."],
            ["User",  "Komposisi (1..1)", "LaporanHarian",
             "LaporanHarian dibuat dari data User melalui lihatLaporan()."],
            ["User",  "Asosiasi (1..1)", "Notifikasi",
             "Notifikasi dikirim berdasarkan data screen time User."],
            ["TopUp", "Dependency (→)",  "User",
             "TopUp.prosesTopUp(User) bergantung pada objek User."],
        ],
        col_widths=[1.0, 1.6, 1.5, 2.7]
    )

    # ── 4.2 ANTARMUKA WEB ──────────────────────────────────────────────────
    heading(doc, "4.2 Implementasi Antarmuka Web", level=2)
    para(doc,
        "Antarmuka web dibangun menggunakan Thymeleaf sebagai template engine yang terintegrasi "
        "dengan Spring MVC. Terdapat delapan halaman utama yang masing-masing berfungsi untuk "
        "kebutuhan yang berbeda.")
    add_table(doc,
        ["No", "Halaman", "URL", "Fitur Utama"],
        [
            ["1", "Login",        "/login",          "Autentikasi pengguna dengan username dan password"],
            ["2", "Register",     "/register",       "Pendaftaran akun pengguna baru"],
            ["3", "Dashboard",    "/dashboard",      "Ringkasan screen time harian dan skor kesehatan"],
            ["4", "Aktivitas",    "/activity",       "Form input aktivitas digital dengan validasi"],
            ["5", "Top-Up",       "/topup",          "Pemilihan jumlah token dan metode pembayaran"],
            ["6", "Profil",       "/profile",        "Edit data profil dan ubah password"],
            ["7", "Laporan",      "/report",         "Laporan harian, riwayat aktivitas, dan tombol export PDF"],
            ["8", "QRIS Payment", "/topup/qris",     "Halaman konfirmasi pembayaran QRIS"],
        ],
        col_widths=[0.4, 1.2, 1.5, 3.7]
    )

    heading(doc, "Form Input Aktivitas Digital", level=3)
    para(doc,
        "Form aktivitas digital menerima input berupa nama aplikasi (pilihan dropdown: TikTok, "
        "YouTube, Instagram, WhatsApp, Netflix, Spotify, dll.), durasi penggunaan dalam menit, "
        "batas durasi harian yang diinginkan pengguna, serta tanggal aktivitas. Setiap submission "
        "form akan mengurangi saldo token pengguna sebesar 5 token sebagai bentuk insentif "
        "pencatatan yang konsisten.")

    # ── 4.3 DATABASE ───────────────────────────────────────────────────────
    heading(doc, "4.3 Implementasi Basis Data", level=2)
    para(doc,
        "Database mindfull_db dibuat menggunakan MySQL dengan charset utf8mb4 untuk mendukung "
        "karakter Unicode termasuk emoji pada kolom pesan notifikasi. Terdapat lima tabel dengan "
        "relasi foreign key yang menjaga integritas referensial data.")

    heading(doc, "Tabel users", level=3)
    add_table(doc,
        ["Kolom", "Tipe Data", "Constraint", "Keterangan"],
        [
            ["id",        "BIGINT",       "PK, AUTO_INCREMENT", "Identitas unik pengguna"],
            ["nama_user", "VARCHAR(100)", "NOT NULL",           "Nama lengkap pengguna"],
            ["username",  "VARCHAR(50)",  "NOT NULL, UNIQUE",   "Username untuk login"],
            ["password",  "VARCHAR(100)", "NOT NULL",           "Password pengguna"],
            ["token",     "INT",          "DEFAULT 50",         "Saldo token (default 50)"],
        ],
        col_widths=[1.3, 1.3, 1.7, 2.5]
    )

    heading(doc, "Tabel aktivitas_digital", level=3)
    add_table(doc,
        ["Kolom", "Tipe Data", "Constraint", "Keterangan"],
        [
            ["id",            "BIGINT",      "PK, AUTO_INCREMENT", "Identitas unik aktivitas"],
            ["user_id",       "BIGINT",      "FK → users(id)",     "Relasi ke pengguna pemilik"],
            ["nama_aplikasi", "VARCHAR(50)", "NOT NULL",           "Nama aplikasi (TikTok, dll.)"],
            ["durasi_menit",  "INT",         "NOT NULL",           "Durasi aktual dalam menit"],
            ["batas_durasi",  "INT",         "NOT NULL",           "Batas durasi harian"],
            ["tanggal",       "DATE",        "NOT NULL",           "Tanggal aktivitas dilakukan"],
        ],
        col_widths=[1.4, 1.2, 1.6, 2.6]
    )

    heading(doc, "DDL Pembuatan Tabel (Ringkasan)", level=3)
    code_block(doc,
        "CREATE TABLE aktivitas_digital (\n"
        "    id            BIGINT NOT NULL AUTO_INCREMENT,\n"
        "    user_id       BIGINT NOT NULL,\n"
        "    nama_aplikasi VARCHAR(50) NOT NULL,\n"
        "    durasi_menit  INT NOT NULL,\n"
        "    batas_durasi  INT NOT NULL,\n"
        "    tanggal       DATE NOT NULL,\n"
        "    PRIMARY KEY (id),\n"
        "    CONSTRAINT fk_aktivitas_user\n"
        "        FOREIGN KEY (user_id) REFERENCES users(id)\n"
        "        ON DELETE CASCADE\n"
        ");"
    )

    heading(doc, "Data Awal (Seed Data)", level=3)
    para(doc,
        "Database diinisialisasi dengan data awal (seed data) yang tersimpan dalam file "
        "database.sql. Data awal mencakup tiga akun pengguna (admin, budi, siti), lima data "
        "aktivitas digital untuk akun admin, dua data top-up token, tiga notifikasi, dan satu "
        "laporan harian. Contoh perhitungan skor: admin memiliki total 305 menit screen time "
        "dengan dua aktivitas melewati batas, sehingga skor = 100 - (305/10) - (2×20) = 30.")
    code_block(doc,
        "-- Contoh data aktivitas admin\n"
        "INSERT INTO aktivitas_digital (user_id, nama_aplikasi, durasi_menit, batas_durasi, tanggal)\n"
        "VALUES\n"
        "    (1, 'TikTok',    90,  60,  CURDATE()),  -- OVER LIMIT (90 > 60)\n"
        "    (1, 'YouTube',   45,  60,  CURDATE()),  -- HEALTHY\n"
        "    (1, 'Instagram', 30,  45,  CURDATE()),  -- HEALTHY\n"
        "    (1, 'WhatsApp',  20,  60,  CURDATE()),  -- HEALTHY\n"
        "    (1, 'Netflix',   120, 90,  CURDATE());  -- OVER LIMIT (120 > 90)"
    )
