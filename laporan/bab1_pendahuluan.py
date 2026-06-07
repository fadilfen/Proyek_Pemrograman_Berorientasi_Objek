"""laporan/bab1_pendahuluan.py"""
from .helpers import heading, para, separator


def tulis(doc):
    heading(doc, "BAB I PENDAHULUAN")
    separator(doc)

    heading(doc, "1.1 Latar Belakang", level=2)
    para(doc,
        "Di era digital yang terus berkembang pesat, penggunaan aplikasi digital seperti media "
        "sosial, platform streaming, dan aplikasi komunikasi telah menjadi bagian tak terpisahkan "
        "dari kehidupan sehari-hari. Fenomena ini membawa dampak positif maupun negatif terhadap "
        "kesehatan mental dan fisik penggunanya. Berdasarkan data We Are Social (2024), rata-rata "
        "pengguna internet di Indonesia menghabiskan sekitar 7 jam 42 menit per hari di depan layar, "
        "menempatkan Indonesia sebagai salah satu negara dengan screen time tertinggi di dunia.")
    para(doc,
        "Tingginya penggunaan layar yang tidak terkontrol dapat berdampak buruk, antara lain "
        "gangguan tidur, penurunan produktivitas, serta masalah kesehatan mata dan postur tubuh. "
        "Oleh karena itu, dibutuhkan sebuah alat bantu yang mampu membantu pengguna memantau dan "
        "mengelola waktu layar mereka secara efektif.")
    para(doc,
        "Berdasarkan permasalahan di atas, proyek ini mengembangkan aplikasi MindFull, yaitu "
        "sebuah aplikasi berbasis web yang dibangun menggunakan bahasa pemrograman Java dengan "
        "framework Spring Boot. Aplikasi ini dirancang untuk melacak screen time harian pengguna, "
        "menghitung skor kesehatan digital, serta memberikan notifikasi dan laporan berkala "
        "sebagai bentuk kesadaran digital bagi pengguna.")

    heading(doc, "1.2 Rumusan Masalah", level=2)
    items = [
        "Bagaimana merancang sistem yang mampu mencatat dan mengelola aktivitas digital pengguna secara efektif?",
        "Bagaimana mengimplementasikan konsep Pemrograman Berorientasi Objek (PBO) dalam pembangunan aplikasi MindFull?",
        "Bagaimana menghitung dan menampilkan skor kesehatan digital berdasarkan data screen time pengguna?",
        "Bagaimana mengintegrasikan antarmuka web berbasis Thymeleaf dengan logika bisnis pada lapisan backend Spring Boot?",
        "Bagaimana menyimpan dan mengelola data pengguna secara persisten menggunakan basis data MySQL?",
    ]
    for i, item in enumerate(items, 1):
        p = doc.add_paragraph(style='List Number')
        p.add_run(item).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)

    heading(doc, "1.3 Tujuan", level=2)
    tujuan = [
        "Merancang dan membangun aplikasi MindFull sebagai solusi manajemen screen time berbasis web.",
        "Mengimplementasikan konsep OOP (enkapsulasi, asosiasi, komposisi, dependency) dalam kode Java.",
        "Mengembangkan fitur perhitungan skor kesehatan digital secara otomatis.",
        "Mengintegrasikan Spring Boot, Thymeleaf, dan MySQL sebagai stack teknologi utama.",
        "Menghasilkan laporan harian yang informatif bagi pengguna.",
    ]
    for t in tujuan:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(t).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)

    heading(doc, "1.4 Manfaat", level=2)
    para(doc,
        "Manfaat dari pengembangan aplikasi MindFull ini antara lain: (1) Membantu pengguna "
        "memantau screen time secara real-time, (2) Meningkatkan kesadaran pengguna terhadap "
        "kebiasaan digital yang tidak sehat, (3) Memberikan pengalaman praktis dalam penerapan "
        "konsep PBO menggunakan Java dan Spring Boot, serta (4) Menjadi referensi implementasi "
        "sistem manajemen data berbasis web dengan arsitektur MVC.")

    heading(doc, "1.5 Batasan Masalah", level=2)
    batasan = [
        "Aplikasi hanya mencakup fitur pencatatan aktivitas digital, top-up token, notifikasi, dan laporan harian.",
        "Autentikasi pengguna menggunakan session sederhana tanpa enkripsi password (scope akademik).",
        "Aplikasi berjalan secara lokal (localhost) dan belum di-deploy ke server publik.",
        "Database yang digunakan adalah MySQL versi 8.0 ke atas.",
    ]
    for b in batasan:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(b).font.size = __import__('docx.shared', fromlist=['Pt']).Pt(11)
