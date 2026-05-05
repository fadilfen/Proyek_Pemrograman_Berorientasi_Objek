package tubes_a11;

import java.util.ArrayList;
import java.time.LocalDate;

public class LaporanHarian {
    private int totalDurasi;
    private int skorHarian;
    private ArrayList<AktivitasDigital> aktivitasList;
    private String namaUser;

    public LaporanHarian(int totalDurasi, int skorHarian, ArrayList<AktivitasDigital> aktivitasList, String namaUser) {
        this.totalDurasi = totalDurasi;
        this.skorHarian = skorHarian;
        this.aktivitasList = aktivitasList;
        this.namaUser = namaUser;
    }

    public String generateLaporan() {
        String laporan = "=== LAPORAN HARIAN ===\n";
        laporan += "Nama User         : " + namaUser + "\n";

        if (!aktivitasList.isEmpty()) {
            LocalDate tanggal = aktivitasList.get(0).getTanggal();
            laporan += "Tanggal           : " + tanggal + "\n";
        }

        laporan += "------------------------------\n";
        laporan += "Detail Aplikasi:\n";

        for (AktivitasDigital aktivitas : aktivitasList) {
            laporan += "- " + aktivitas.getNamaAplikasi() +
                       " : " + aktivitas.getDurasiMenit() + " menit\n";
        }

        laporan += "------------------------------\n";
        laporan += "Total Screen Time : " + totalDurasi + " menit\n";
        laporan += "Skor Harian       : " + skorHarian + "\n";
        laporan += "Status            : " +
                (skorHarian >= 70 ? "Sehat" : "Kurangi Screen Time");

        return laporan;
    }
}