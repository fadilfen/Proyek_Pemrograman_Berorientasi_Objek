"""laporan/bab5_hasil.py - Bab 5 Hasil dan Pembahasan"""
from .helpers import heading, para, separator, add_table, code_block


def tulis(doc):
    heading(doc, "BAB V HASIL DAN PEMBAHASAN")
    separator(doc)

    heading(doc, "5.1 Hasil Implementasi", level=2)
    para(doc,
        "Aplikasi MindFull berhasil diimplementasikan sesuai dengan rancangan yang telah "
        "ditetapkan. Seluruh fitur yang direncanakan dapat berjalan dengan baik pada lingkungan "
        "pengembangan lokal (localhost:8080) dengan konfigurasi Spring Boot dan MySQL.")

    heading(doc, "5.2 Pengujian Fungsional", level=2)
    para(doc,
        "Pengujian dilakukan secara manual (black-box testing) dengan menguji setiap fitur "
        "melalui antarmuka web. Tabel berikut merangkum hasil pengujian fungsional:")
    add_table(doc,
        ["No", "Skenario Uji", "Input", "Output yang Diharapkan", "Status"],
        [
            ["1", "Login berhasil",       "username: admin, password: 12345",
             "Diarahkan ke halaman dashboard", "✅ Lulus"],
            ["2", "Login gagal",          "password salah",
             "Pesan error 'Username/password salah'", "✅ Lulus"],
            ["3", "Registrasi akun baru", "Data lengkap + username baru",
             "Akun tersimpan, redirect ke login", "✅ Lulus"],
            ["4", "Catat aktivitas",      "TikTok, 90 menit, batas 60",
             "Aktivitas tersimpan, token -5", "✅ Lulus"],
            ["5", "Hitung skor",          "Total 305 menit, 2 over limit",
             "Skor = 30 (sesuai formula)", "✅ Lulus"],
            ["6", "Top-Up QRIS",          "50 token, metode QRIS",
             "Token bertambah 50, riwayat tersimpan", "✅ Lulus"],
            ["7", "Lihat laporan",        "Login sebagai admin",
             "Laporan harian tampil dengan skor 30", "✅ Lulus"],
            ["8", "Export PDF",           "Klik tombol Print di halaman report",
             "Dialog print browser terbuka", "✅ Lulus"],
        ],
        col_widths=[0.3, 1.5, 1.7, 2.2, 0.9]
    )

    heading(doc, "5.3 Analisis Logika Perhitungan Skor Kesehatan", level=2)
    para(doc,
        "Formula perhitungan skor kesehatan digital pada aplikasi MindFull menggunakan pendekatan "
        "berbasis penalti. Skor awal adalah 100 dan akan berkurang berdasarkan dua faktor:")
    items = [
        "Pengurangan proporsional: setiap 10 menit screen time mengurangi skor sebesar 1 poin.",
        "Penalti over-limit: setiap aktivitas yang melebihi batas durasi mengurangi skor tambahan 20 poin.",
    ]
    for it in items:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(it).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)

    para(doc, "Simulasi perhitungan untuk data seed admin:")
    code_block(doc,
        "Aktivitas admin:\n"
        "- TikTok    : 90 menit (batas 60) → OVER LIMIT\n"
        "- YouTube   : 45 menit (batas 60) → OK\n"
        "- Instagram : 30 menit (batas 45) → OK\n"
        "- WhatsApp  : 20 menit (batas 60) → OK\n"
        "- Netflix   : 120 menit (batas 90)→ OVER LIMIT\n\n"
        "Total screen time = 90+45+30+20+120 = 305 menit\n"
        "Pengurangan proporsional = 305 / 10 = 30\n"
        "Penalti over-limit       = 2 aktivitas × 20 = 40\n"
        "Skor akhir = 100 - 30 - 40 = 30 → Status: KURANGI SCREEN TIME"
    )

    heading(doc, "5.4 Analisis Manajemen Token", level=2)
    para(doc,
        "Token berfungsi sebagai mata uang virtual dalam aplikasi MindFull. Token digunakan "
        "sebagai mekanisme gamifikasi untuk mendorong pengguna mencatat aktivitas secara konsisten.")
    add_table(doc,
        ["Aktivitas", "Perubahan Token", "Keterangan"],
        [
            ["Registrasi akun",        "+50 token", "Token awal yang diberikan gratis"],
            ["Catat aktivitas digital","-5 token",  "Biaya per pencatatan aktivitas"],
            ["Top-up token",           "+N token",  "Sesuai jumlah yang dipilih pengguna"],
        ],
        col_widths=[2.5, 1.5, 2.8]
    )

    heading(doc, "5.5 Kelebihan dan Kekurangan Sistem", level=2)
    heading(doc, "Kelebihan", level=3)
    kelebihan = [
        "Struktur kode terorganisir menggunakan prinsip OOP dan arsitektur MVC.",
        "Antarmuka web intuitif dan mudah digunakan oleh pengguna non-teknis.",
        "Skor kesehatan digital dihitung secara otomatis tanpa input manual tambahan.",
        "Fitur laporan harian dapat langsung diekspor sebagai PDF melalui browser.",
        "Database menggunakan foreign key dengan ON DELETE CASCADE untuk menjaga integritas data.",
    ]
    for k in kelebihan:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(k).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)

    heading(doc, "Kekurangan dan Saran Pengembangan", level=3)
    kekurangan = [
        "Password tidak dienkripsi (plain text) — disarankan menggunakan BCrypt pada versi produksi.",
        "Belum ada fitur grafik/visualisasi tren screen time harian atau mingguan.",
        "Notifikasi masih bersifat pasif (tersimpan di database), belum ada push notification real-time.",
        "Belum mendukung multi-device dan belum di-deploy ke cloud server.",
    ]
    for k in kekurangan:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(k).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)
