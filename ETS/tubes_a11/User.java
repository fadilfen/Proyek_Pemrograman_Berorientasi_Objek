package tubes_a11;

import java.util.ArrayList;

public class User {
    private int idUser;
    private String namaUser;
    private String username;
    private int token;
    private ArrayList<AktivitasDigital> aktivitasList = new ArrayList<>();

    public User(int idUser, String namaUser, int token) {
        this.idUser = idUser;
        this.namaUser = namaUser;
        this.token = token;
    }

    public void tambahAktivitas(AktivitasDigital aktivitas) {
        aktivitasList.add(aktivitas);
    }

    public int hitungTotalScreenTime() {
        int total = 0;
        for (AktivitasDigital a : aktivitasList) total += a.getDurasiMenit();
        return total;
    }

    public int hitungScoreKesehatan() {
        int score = 100;

        for (AktivitasDigital aktivitas : aktivitasList) {
            score -= aktivitas.getDurasiMenit() / 10;

            if (aktivitas.melebihiBatas()) {
                score -= 20;
            }
        }

        return Math.max(0, score);
    }

    public LaporanHarian lihatLaporan() {
        return new LaporanHarian(
            hitungTotalScreenTime(),
            hitungScoreKesehatan(),
            aktivitasList,
            namaUser
        );
    }

    public void tambahToken(int jumlah) { token += jumlah; }
    public void kurangiToken(int jumlah) { token -= jumlah; }
    public int getToken() { return token; }
    public String getNamaUser() { return namaUser; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}