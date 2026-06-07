package tubes_a11;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * AppTimer — Representasi timer aplikasi untuk child account
 * Setiap child memiliki timer per aplikasi dengan start time dan duration
 */
public class AppTimer {
    private long id;
    private long childId;
    private String appName;
    private int durationMinutes;
    private java.time.LocalDate tanggal;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isTracking;
    
    // Properti untuk mensimulasikan timer hitung mundur
    private long remainingSeconds;

    public AppTimer(long childId, String appName, int durationMinutes, java.time.LocalDate tanggal, LocalTime startTime) {
        this.childId = childId;
        this.appName = appName;
        this.durationMinutes = durationMinutes;
        this.tanggal = tanggal;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(durationMinutes);
        this.isTracking = false;
        this.remainingSeconds = durationMinutes * 60L;
    }

    public AppTimer(long id, long childId, String appName, int durationMinutes, 
                    java.time.LocalDate tanggal, LocalTime startTime, LocalTime endTime, boolean isTracking, long remainingSeconds) {
        this.id = id;
        this.childId = childId;
        this.appName = appName;
        this.durationMinutes = durationMinutes;
        this.tanggal = tanggal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isTracking = isTracking;
        this.remainingSeconds = remainingSeconds;
    }

    // ── Logika Simulasi Timer (Untuk GUI Dummy Phone) ─────────────
    
    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void decrementRemainingSeconds() {
        if (remainingSeconds > 0) remainingSeconds--;
    }

    public String getSimulatedRemainingTimeString() {
        if (remainingSeconds <= 0) return "Waktu habis";
        long hours = remainingSeconds / 3600;
        long mins = (remainingSeconds % 3600) / 60;
        long secs = remainingSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d tersisa", hours, mins, secs);
        } else {
            return String.format("%02d:%02d tersisa", mins, secs);
        }
    }
    
    public boolean isSimulatedSafe() {
        return remainingSeconds > (durationMinutes * 60L * 0.2);
    }
    
    // ──────────────────────────────────────────────────────────────

    /**
     * Menghitung sisa waktu dari sekarang sampai end time
     * @return sisa waktu dalam menit (negatif jika sudah lewat)
     */
    public long getRemainingMinutes() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(startTime)) {
            return durationMinutes; // belum mulai
        }
        if (now.isAfter(endTime)) {
            return 0; // sudah habis
        }
        // Hitung sisa menit
        return java.time.Duration.between(now, endTime).toMinutes();
    }

    /**
     * Mengecek apakah timer masih aman (hijau) atau sudah over (merah)
     * @return true jika aman (sisa > 20% dari total), false jika berbahaya
     */
    public boolean isSafe() {
        long remaining = getRemainingMinutes();
        return remaining > (durationMinutes * 0.2);
    }

    /**
     * Mendapat string representasi waktu yang tersisa
     * Format: "45 menit tersisa" atau "Waktu habis"
     */
    public String getRemainingTimeString() {
        long remaining = getRemainingMinutes();
        if (remaining <= 0) return "Waktu habis";
        if (remaining < 60) return remaining + " menit tersisa";
        long hours = remaining / 60;
        long mins = remaining % 60;
        return hours + " jam " + mins + " menit tersisa";
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getChildId() { return childId; }
    public String getAppName() { return appName; }
    public int getDurationMinutes() { return durationMinutes; }
    public java.time.LocalDate getTanggal() { return tanggal; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isTracking() { return isTracking; }
    public void setTracking(boolean tracking) { isTracking = tracking; }

    public String getStartTimeString() {
        return startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getEndTimeString() {
        return endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
