package tubes_a11;

import java.time.LocalDate;

public class AktivitasDigital {
    private String namaAplikasi;
    private int durasiMenit;
    private int batasDurasi;
    private LocalDate tanggal;

    public AktivitasDigital(String namaAplikasi, int durasiMenit, int batasDurasi, LocalDate tanggal) {
        this.namaAplikasi = namaAplikasi;
        this.durasiMenit = durasiMenit;
        this.batasDurasi = batasDurasi;
        this.tanggal = tanggal;
    }

    public boolean melebihiBatas() {
        return durasiMenit > batasDurasi;
    }

    public String getNamaAplikasi() { return namaAplikasi; }
    public int getDurasiMenit() { return durasiMenit; }
    public int getBatasDurasi() { return batasDurasi; }
    public LocalDate getTanggal() { return tanggal; }
}