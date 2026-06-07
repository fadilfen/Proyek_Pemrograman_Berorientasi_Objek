"""laporan/bab2_tinjauan.py - Bab 2 Tinjauan Pustaka"""
from .helpers import heading, para, separator, add_table


def tulis(doc):
    heading(doc, "BAB II TINJAUAN PUSTAKA")
    separator(doc)

    heading(doc, "2.1 Pemrograman Berorientasi Objek (PBO)", level=2)
    para(doc,
        "Pemrograman Berorientasi Objek (Object-Oriented Programming/OOP) adalah paradigma "
        "pemrograman yang mengorganisasikan program sebagai kumpulan objek yang saling berinteraksi. "
        "Menurut Deitel & Deitel (2020), OOP memiliki empat pilar utama, yaitu enkapsulasi, "
        "pewarisan (inheritance), polimorfisme, dan abstraksi. Paradigma ini memungkinkan pengembang "
        "untuk membuat program yang modular, mudah dipelihara, dan dapat digunakan kembali (reusable).")
    para(doc,
        "Dalam proyek MindFull, konsep OOP diterapkan melalui pembuatan kelas-kelas yang "
        "merepresentasikan entitas duata nyata seperti User, AktivitasDigital, TopUp, Notifikasi, "
        "dan LaporanHarian. Setiap kelas memiliki atribut (data) dan metode (perilaku) yang "
        "ter-enkapsulasi dengan baik menggunakan modifier akses private.")

    heading(doc, "2.2 Java dan Spring Boot", level=2)
    para(doc,
        "Java adalah bahasa pemrograman berorientasi objek yang bersifat platform-independent "
        "berkat mekanisme Java Virtual Machine (JVM). Java dipilih sebagai bahasa utama dalam "
        "proyek ini karena dukungannya terhadap OOP yang kuat, ekosistem library yang luas, "
        "serta performa yang baik untuk aplikasi enterprise.")
    para(doc,
        "Spring Boot adalah framework berbasis Spring Framework yang menyederhanakan konfigurasi "
        "dan deployment aplikasi Java. Spring Boot mengadopsi pola arsitektur MVC "
        "(Model-View-Controller) yang memisahkan logika bisnis, presentasi, dan akses data secara "
        "terstruktur. Fitur auto-configuration pada Spring Boot memungkinkan developer untuk "
        "fokus pada pengembangan logika bisnis tanpa perlu konfigurasi manual yang kompleks "
        "(Walls, 2022).")

    heading(doc, "2.3 Thymeleaf Template Engine", level=2)
    para(doc,
        "Thymeleaf adalah Java template engine modern yang digunakan untuk menghasilkan halaman "
        "web HTML secara dinamis. Thymeleaf terintegrasi secara native dengan Spring MVC dan "
        "memungkinkan penggunaan atribut khusus (th:text, th:each, th:if, dll.) langsung di dalam "
        "markup HTML sehingga template tetap valid sebagai dokumen HTML standar. Pendekatan ini "
        "memudahkan kolaborasi antara developer backend dan designer frontend.")

    heading(doc, "2.4 MySQL dan Relasi Database", level=2)
    para(doc,
        "MySQL adalah sistem manajemen basis data relasional (RDBMS) open-source yang banyak "
        "digunakan dalam pengembangan aplikasi web. MySQL mendukung penggunaan foreign key untuk "
        "menjaga integritas referensial antar tabel. Dalam proyek MindFull, MySQL digunakan untuk "
        "menyimpan data pengguna, aktivitas digital, transaksi top-up, notifikasi, dan laporan "
        "harian secara persisten.")

    heading(doc, "2.5 Konsep Screen Time dan Kesehatan Digital", level=2)
    para(doc,
        "Screen time merujuk pada total waktu yang dihabiskan seseorang di depan layar perangkat "
        "digital. Menurut American Academy of Pediatrics (AAP, 2023), penggunaan layar yang "
        "berlebihan dapat berdampak negatif pada pola tidur, konsentrasi, dan kesehatan mental. "
        "Aplikasi manajemen screen time seperti MindFull hadir sebagai solusi untuk membantu "
        "pengguna menetapkan batas waktu penggunaan dan memantau kebiasaan digital mereka.")

    heading(doc, "2.6 Arsitektur MVC (Model-View-Controller)", level=2)
    add_table(doc,
        ["Komponen", "Peran", "Implementasi di MindFull"],
        [
            ["Model",      "Merepresentasikan data dan logika bisnis", "Kelas entity Java (User, AktivitasDigital, dll.) + JPA Repository"],
            ["View",       "Menampilkan data kepada pengguna",         "Template Thymeleaf (HTML: dashboard, activity, report, dll.)"],
            ["Controller", "Menghubungkan Model dan View",             "Spring @Controller (UserController, ActivityController, dll.)"],
        ],
        col_widths=[1.5, 2.5, 2.8]
    )
