# MindFull - Screen Time Tracker

Aplikasi Java Swing untuk tracking screen time dengan sistem Parent-Child Account.

## Fitur Aplikasi

### Role: Parent (Orang Tua)
- **Registrasi otomatis sebagai Parent** saat daftar akun baru
- **Menambah akun Child (Anak)** dengan username dan password
- **Setting Timer per Aplikasi** untuk setiap child
  - Pilih aplikasi (TikTok, Instagram, YouTube, dll)
  - Set durasi (dalam menit)
  - Set jam mulai tracking
- **Tombol Track** untuk memulai monitoring (cost 5 token)
- **Top Up Token** untuk membeli token tambahan
- **Dashboard** menampilkan:
  - Token balance
  - Wellness score
  - Jumlah akun anak

### Role: Child (Anak)
- **Login dengan akun yang dibuat Parent**
- **My Apps Page** menampilkan aplikasi dummy dengan:
  - Icon aplikasi (emoji)
  - Timer countdown real-time
  - Warna hijau = Aman (masih > 20% waktu tersisa)
  - Warna merah = Peringatan (< 20% atau habis)
  - Jam mulai dan jam selesai
- **Dashboard** menampilkan:
  - Jumlah aplikasi yang ditrack
  - Jumlah aplikasi masih aman
  - Jumlah aplikasi warning

## Setup Database

### 1. Jalankan Laragon MySQL
Pastikan Laragon sudah running dengan MySQL aktif.

### 2. Import Database
Jalankan file SQL berikut di phpMyAdmin atau MySQL client:
```
gui/src/database/mindfull_updated.sql
```

File ini akan membuat:
- Database `mindfull_db`
- Tabel `users` (dengan field `role` dan `parent_id`)
- Tabel `app_timers` (untuk setting timer aplikasi)
- Tabel `aktivitas_digital` (log aktivitas)
- Tabel `topup` (riwayat top up)

### 3. Konfigurasi Database
Edit file `gui/src/database/DatabaseHelper.java` jika perlu mengubah:
- JDBC URL: `jdbc:mysql://localhost:3306/mindfull_db`
- Username: `root`
- Password: `` (kosong untuk Laragon default)

## Cara Menjalankan

### Metode 1: Menggunakan Script (RECOMMENDED)
```bash
cd gui
run.bat
```

### Metode 2: Compile Manual
```bash
cd gui
compile.bat
```
Setelah compile berhasil, jalankan:
```bash
java -cp "bin;lib/mysql-connector.jar" tubes_a11.Main
```

### Metode 3: Menggunakan IDE (Eclipse/IntelliJ)
1. Import project `gui` sebagai Java project
2. Add `lib/mysql-connector.jar` ke Build Path
3. Run `tubes_a11.Main`

### Troubleshooting
**Error: Driver MySQL tidak ditemukan**
- Pastikan file `lib/mysql-connector.jar` ada
- Pastikan classpath sudah benar

**Error: Could not connect to database**
- Pastikan Laragon MySQL sudah running
- Import database dari `docs/database.sql`

**Aplikasi loading lambat**
- Sudah dioptimasi dengan lazy loading
- Baca `docs/PERFORMANCE_OPTIMIZATION.md`

## Flow Aplikasi

### 1. Registrasi & Login Parent
```
1. Buka aplikasi → Klik "Belum punya akun? Daftar sekarang"
2. Isi data: Nama, Username, Password
3. Registrasi berhasil → Role otomatis "parent"
4. Login dengan username dan password
```

### 2. Parent Menambah Child
```
1. Login sebagai Parent
2. Klik menu "Manage Children" di sidebar
3. Isi form:
   - Nama Lengkap anak
   - Username anak
   - Password anak
4. Klik "Tambah Akun Anak"
```

### 3. Parent Setting Timer untuk Child
```
1. Di halaman "Manage Children"
2. Klik tombol "Set Timer" pada card anak
3. Isi form:
   - Pilih aplikasi (TikTok, Instagram, dll)
   - Durasi (menit): berapa lama boleh digunakan
   - Jam mulai: kapan tracking dimulai
4. Klik "Simpan" → timer tersimpan
5. Klik "Track (−5 Token)" → mulai tracking (potong 5 token)
```

### 4. Child Login & Lihat Apps
```
1. Logout dari Parent
2. Login dengan username anak
3. Klik menu "My Apps"
4. Lihat aplikasi dummy dengan timer countdown:
   - Hijau = Masih aman (> 20% waktu tersisa)
   - Merah = Peringatan (< 20% atau habis)
5. Klik icon aplikasi untuk detail
```

### 5. Parent Top Up Token
```
1. Klik menu "Top Up Balance"
2. Masukkan jumlah token yang ingin dibeli
3. Preview biaya muncul real-time (1 token = Rp 1.000)
4. Pilih metode pembayaran
5. Klik "Konfirmasi Top Up"
```

## Struktur Database

### Table: users
| Field     | Type   | Description                    |
|-----------|--------|--------------------------------|
| id        | BIGINT | Primary key                    |
| nama_user | VARCHAR| Nama lengkap                   |
| username  | VARCHAR| Username (unique)              |
| password  | VARCHAR| Password                       |
| token     | INT    | Jumlah token (default 50)      |
| role      | ENUM   | 'parent' atau 'child'          |
| parent_id | BIGINT | NULL jika parent, id parent jika child |

### Table: app_timers
| Field            | Type    | Description                        |
|------------------|---------|------------------------------------|
| id               | BIGINT  | Primary key                        |
| child_id         | BIGINT  | Foreign key ke users               |
| app_name         | VARCHAR | Nama aplikasi                      |
| duration_minutes | INT     | Durasi dalam menit                 |
| start_time       | TIME    | Jam mulai tracking                 |
| end_time         | TIME    | Jam selesai (start + duration)     |
| is_tracking      | BOOLEAN | Status tracking (0 atau 1)         |
| created_at       | DATETIME| Timestamp                          |

## Tema UI

### Warna (Light Theme)
- Background: `#f0f4f8` (abu terang)
- Card: `#ffffff` (putih)
- Sidebar: `#e8f0f8` (biru muda)
- Primary: `#3b82f6` (biru cerah)
- Success: `#10b981` (hijau emerald)
- Danger: `#ef4444` (merah)
- Text: `#1e293b` (gelap)

### Font
- Segoe UI untuk semua text
- Segoe UI Emoji untuk icon aplikasi

## Troubleshooting

### Database Connection Error
```
Error: Could not connect to database
```
**Solusi:**
- Pastikan Laragon/MySQL sudah running
- Cek kredensial database di `DatabaseHelper.java`
- Import ulang file SQL

### Token Tidak Berkurang
```
Token parent tidak berkurang saat Track
```
**Solusi:**
- Cek method `kurangiToken()` di class User
- Pastikan `DatabaseHelper.updateToken()` terpanggil

### Timer Tidak Muncul di Child
```
Child login tapi My Apps kosong
```
**Solusi:**
- Pastikan Parent sudah klik "Track" (is_tracking = 1)
- Cek data di table `app_timers` WHERE `is_tracking = 1`

## Credits

Developed by: Kelompok A11
- Tugas Besar OOP
- Politeknik Negeri Bandung
