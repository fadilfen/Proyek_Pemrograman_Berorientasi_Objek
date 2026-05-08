"""laporan/config.py - Konfigurasi dan warna laporan MindFull"""
from docx.shared import RGBColor, Pt, Cm, Inches

PROYEK   = "MindFull"
MATKUL   = "IFB-202 Pemrograman Berbasis Objek"
KELAS    = "AA"
TANGGAL  = "12 Mei 2026"
EVALUASI = "Evaluasi 3 (SC-3)"
BOBOT    = "20%"

# Isi data anggota kelompok di sini
ANGGOTA = [
    {"no": 1, "nama": "", "jobdesk": "", "detail": ""},
    {"no": 2, "nama": "", "jobdesk": "", "detail": ""},
    {"no": 3, "nama": "", "jobdesk": "", "detail": ""},
    {"no": 4, "nama": "", "jobdesk": "", "detail": ""},
    {"no": 5, "nama": "", "jobdesk": "", "detail": ""},
]

# Warna
C_NAVY   = RGBColor(0x0F, 0x34, 0x60)
C_BLUE   = RGBColor(0x1D, 0x4E, 0xD8)
C_TEAL   = RGBColor(0x0E, 0x78, 0x9E)
C_WHITE  = RGBColor(0xFF, 0xFF, 0xFF)
C_GRAY   = RGBColor(0x6B, 0x72, 0x80)
C_BLACK  = RGBColor(0x1A, 0x1A, 0x2E)
C_DKCODE = RGBColor(0xC9, 0xD1, 0xD9)

# Ukuran halaman
PAGE_LEFT   = Cm(4)
PAGE_RIGHT  = Cm(3)
PAGE_TOP    = Cm(3)
PAGE_BOTTOM = Cm(3)
