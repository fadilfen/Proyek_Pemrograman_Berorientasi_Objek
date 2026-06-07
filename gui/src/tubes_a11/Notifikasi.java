package tubes_a11;

public class Notifikasi {

	private int idNotifikasi;
	private String pesan;

	public void kirimPeringatan(int totalScreenTime, int limit) {

	    if (totalScreenTime < limit) {
	        pesan = "Screen time masih dalam batas yang ditentukan.";
	    } 
	    else if (totalScreenTime == limit) {
	        pesan = "Anda sudah mencapai batas screen time hari ini.";
	    } 
	    else {
	        pesan = "Anda sudah melebihi batas screen time!";
	    }

	    System.out.println("Notifikasi: " + pesan);
	}

	public String getPesan() {
		return pesan;
	}

	public void setPesan(String pesan) {
		this.pesan = pesan;
	}
}