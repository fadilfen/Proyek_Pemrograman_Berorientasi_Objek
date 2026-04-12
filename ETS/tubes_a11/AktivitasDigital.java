package tubes_a11;

public class AktivitasDigital {
    private String namaAplikasi;
    private int durasiMenit;
    private int batasDurasi;

    public AktivitasDigital(String namaAplikasi, int durasiMenit, int batasDurasi) {
        this.namaAplikasi = namaAplikasi;
        this.durasiMenit = durasiMenit;
        this.batasDurasi = batasDurasi;
    }

    public boolean melebihiBatas() {
        return durasiMenit > batasDurasi;
    }

    public String getNamaAplikasi() { return namaAplikasi; }
    public int getDurasiMenit() { return durasiMenit; }
    public int getBatasDurasi() { return batasDurasi; }
}
