"""laporan/bab3_metodologi.py - Bab 3 Metodologi"""
from .helpers import heading, para, separator, add_table, code_block


def tulis(doc):
    heading(doc, "BAB III METODOLOGI")
    separator(doc)

    heading(doc, "3.1 Metode Pengembangan", level=2)
    para(doc,
        "Pengembangan aplikasi MindFull menggunakan pendekatan iteratif. Proses dimulai dari "
        "analisis kebutuhan, perancangan arsitektur sistem, implementasi kode, pengujian fungsional, "
        "hingga penulisan dokumentasi. Pendekatan ini dipilih karena memungkinkan perbaikan "
        "berkelanjutan pada setiap iterasi pengembangan.")

    heading(doc, "3.2 Analisis Kebutuhan Sistem", level=2)
    heading(doc, "Kebutuhan Fungsional", level=3)
    kf = [
        "Pengguna dapat mendaftarkan akun baru dan melakukan login.",
        "Pengguna dapat mencatat aktivitas digital (nama aplikasi, durasi, batas durasi, tanggal).",
        "Sistem menghitung total screen time dan skor kesehatan digital secara otomatis.",
        "Pengguna dapat melakukan top-up token melalui QRIS, Bank Transfer, atau E-Wallet.",
        "Sistem mengirimkan notifikasi peringatan jika screen time melebihi batas.",
        "Sistem menghasilkan laporan harian yang dapat diekspor sebagai PDF.",
        "Pengguna dapat melihat riwayat aktivitas dan top-up.",
        "Pengguna dapat mengedit profil dan kredensial akun.",
    ]
    for f in kf:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)

    heading(doc, "Kebutuhan Non-Fungsional", level=3)
    knf = [
        "Sistem berjalan pada port 8080 (localhost) menggunakan Spring Boot embedded Tomcat.",
        "Database menggunakan MySQL 8.0 dengan charset utf8mb4.",
        "Antarmuka pengguna responsif dan mudah digunakan.",
        "Kode terstruktur mengikuti prinsip OOP dan arsitektur MVC.",
    ]
    for n in knf:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(n).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)

    heading(doc, "3.3 Perancangan Sistem", level=2)
    heading(doc, "Alur Sistem (Use Case Utama)", level=3)
    add_table(doc,
        ["Aktor", "Use Case", "Deskripsi"],
        [
            ["Pengguna", "Register / Login",       "Membuat akun baru atau masuk ke sistem"],
            ["Pengguna", "Catat Aktivitas",         "Input nama aplikasi, durasi, dan batas waktu"],
            ["Pengguna", "Lihat Dashboard",         "Melihat ringkasan screen time dan skor kesehatan"],
            ["Pengguna", "Top-Up Token",            "Menambah saldo token via QRIS/Bank Transfer"],
            ["Pengguna", "Lihat Laporan",           "Melihat dan mengekspor laporan harian"],
            ["Sistem",   "Hitung Skor Kesehatan",   "Menghitung skor otomatis berdasarkan screen time"],
            ["Sistem",   "Kirim Notifikasi",        "Mengirim peringatan jika screen time melebihi batas"],
        ],
        col_widths=[1.2, 2.0, 3.6]
    )

    heading(doc, "3.4 Teknologi yang Digunakan", level=2)
    add_table(doc,
        ["Teknologi", "Versi", "Fungsi"],
        [
            ["Java JDK",      "17+",    "Bahasa pemrograman utama"],
            ["Spring Boot",   "3.x",    "Framework backend (MVC, REST, auto-config)"],
            ["Thymeleaf",     "3.x",    "Template engine untuk halaman HTML"],
            ["MySQL",         "8.0+",   "Sistem manajemen basis data"],
            ["Maven",         "3.8+",   "Build tool dan manajemen dependency"],
            ["HTML/CSS",      "-",      "Struktur dan tampilan antarmuka web"],
            ["Spring Data JPA","3.x",  "Abstraksi akses database (ORM)"],
        ],
        col_widths=[1.8, 0.8, 4.2]
    )

    heading(doc, "3.5 Struktur Direktori Proyek", level=2)
    code_block(doc,
        "demo/\n"
        "├── src/main/java/com/example/demo/\n"
        "│   ├── controller/     ← Spring @Controller (routing HTTP)\n"
        "│   ├── model/          ← Entity JPA (User, AktivitasDigital, dll.)\n"
        "│   ├── repository/     ← JPA Repository interface\n"
        "│   ├── service/        ← Logika bisnis\n"
        "│   └── DemoApplication.java\n"
        "├── src/main/resources/\n"
        "│   ├── templates/      ← Template Thymeleaf (.html)\n"
        "│   ├── static/         ← CSS, JS, gambar\n"
        "│   ├── application.properties\n"
        "│   └── database.sql\n"
        "└── pom.xml             ← Konfigurasi Maven"
    )
