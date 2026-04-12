package tubes_a11;

public class LaporanHarian {
    private int totalDurasi;
    private int skorHarian;

    public LaporanHarian(int totalDurasi, int skorHarian) {
        this.totalDurasi = totalDurasi;
        this.skorHarian = skorHarian;
    }

    public String generateLaporan() {
        return "=== LAPORAN HARIAN ===\n" +
               "Total Screen Time : " + totalDurasi + " menit\n" +
               "Skor Harian       : " + skorHarian + "\n" +
               "Status            : " + (skorHarian >= 70 ? "Sehat" : "Kurangi Screen Time");
    }
}