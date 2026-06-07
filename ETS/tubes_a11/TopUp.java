package tubes_a11;

public class TopUp {
    private int jumlahKoin;
    private String metodePembayaran;

    public TopUp(int jumlahKoin, String metodePembayaran) {
        this.jumlahKoin = jumlahKoin;
        this.metodePembayaran = metodePembayaran;
    }

    public void prosesTopUp(User user) {
        user.tambahToken(jumlahKoin);
    }

    public boolean validasiPembayaran() {
        return jumlahKoin > 0;
    }

    public int getJumlahKoin() { return jumlahKoin; }
    public String getMetodePembayaran() { return metodePembayaran; }
}
