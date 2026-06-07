package testing;

import org.junit.jupiter.api.*;
import tubes_a11.User;
import tubes_a11.AktivitasDigital;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk class User (Non-Database Operations)
 * Total: 15 skenario testing
 */
public class UserTest {

    @Test
    @DisplayName("Test 1: Constructor dengan parameter valid untuk parent")
    public void testConstructorParent() {
        // Membuat user parent dengan nama John Doe
        User user = new User(1, "John Doe", 100, "parent", null, 0);
        assertNotNull(user);
        assertEquals("John Doe", user.getNamaUser());
        assertEquals(100, user.getToken());
        assertEquals("parent", user.getRole());
        System.out.println("Skenario 1: Constructor parent valid - BERHASIL");
    }

    @Test
    @DisplayName("Test 2: Constructor dengan parameter valid untuk child")
    public void testConstructorChild() {
        // Membuat user child dengan umur 10 tahun
        User user = new User(2, "Jane Doe", 50, "child", 1L, 10);
        assertNotNull(user);
        assertEquals("child", user.getRole());
        assertEquals(1L, user.getParentId());
        assertEquals(10, user.getUmur());
        System.out.println("Skenario 2: Constructor child valid - BERHASIL");
    }

    @Test
    @DisplayName("Test 3: Constructor sederhana untuk child")
    public void testConstructorSederhana() {
        // Membuat child dengan constructor sederhana
        User user = new User(3, "Alice", "alice123", "child", 8);
        assertNotNull(user);
        assertEquals("Alice", user.getNamaUser());
        assertEquals("alice123", user.getUsername());
        System.out.println("Skenario 3: Constructor sederhana child - BERHASIL");
    }

    @Test
    @DisplayName("Test 4: getId() mengembalikan id yang benar")
    public void testGetId() {
        // Verifikasi getter id user
        User user = new User(5, "Bob", 75, "parent", null, 0);
        assertEquals(5, user.getId());
        System.out.println("Skenario 4: getId() mengembalikan id benar - BERHASIL");
    }

    @Test
    @DisplayName("Test 5: getNamaUser() mengembalikan nama yang benar")
    public void testGetNamaUser() {
        // Verifikasi getter nama user
        User user = new User(1, "Charlie Brown", 100, "parent", null, 0);
        assertEquals("Charlie Brown", user.getNamaUser());
        System.out.println("Skenario 5: getNamaUser() mengembalikan nama benar - BERHASIL");
    }

    @Test
    @DisplayName("Test 6: getToken() mengembalikan token yang benar")
    public void testGetToken() {
        // Verifikasi getter token user
        User user = new User(1, "David", 150, "parent", null, 0);
        assertEquals(150, user.getToken());
        System.out.println("Skenario 6: getToken() mengembalikan token benar - BERHASIL");
    }

    @Test
    @DisplayName("Test 7: isParent() mengembalikan true untuk parent")
    public void testIsParentTrue() {
        // Verifikasi user adalah parent
        User user = new User(1, "Emma", 100, "parent", null, 0);
        assertTrue(user.isParent());
        System.out.println("Skenario 7: isParent() true untuk parent - BERHASIL");
    }

    @Test
    @DisplayName("Test 8: isParent() mengembalikan false untuk child")
    public void testIsParentFalse() {
        // Verifikasi user bukan parent
        User user = new User(2, "Frank", 50, "child", 1L, 12);
        assertFalse(user.isParent());
        System.out.println("Skenario 8: isParent() false untuk child - BERHASIL");
    }

    @Test
    @DisplayName("Test 9: isChild() mengembalikan true untuk child")
    public void testIsChildTrue() {
        // Verifikasi user adalah child
        User user = new User(2, "Grace", 0, "child", 1L, 9);
        assertTrue(user.isChild());
        System.out.println("Skenario 9: isChild() true untuk child - BERHASIL");
    }

    @Test
    @DisplayName("Test 10: isChild() mengembalikan false untuk parent")
    public void testIsChildFalse() {
        // Verifikasi user bukan child
        User user = new User(1, "Henry", 200, "parent", null, 0);
        assertFalse(user.isChild());
        System.out.println("Skenario 10: isChild() false untuk parent - BERHASIL");
    }

    @Test
    @DisplayName("Test 11: getUmur() mengembalikan umur yang benar")
    public void testGetUmur() {
        // Verifikasi getter umur child
        User user = new User(3, "Ivy", 0, "child", 1L, 7);
        assertEquals(7, user.getUmur());
        System.out.println("Skenario 11: getUmur() mengembalikan umur benar - BERHASIL");
    }

    @Test
    @DisplayName("Test 12: getParentId() mengembalikan null untuk parent")
    public void testGetParentIdNull() {
        User user = new User(1, "Jack", 100, "parent", null, 0);
        assertNull(user.getParentId());
        System.out.println("Skenario 12: getParentId() null untuk parent - BERHASIL");
    }

    @Test
    @DisplayName("Test 13: getParentId() mengembalikan id parent untuk child")
    public void testGetParentIdValue() {
        User user = new User(3, "Kate", 0, "child", 5L, 11);
        assertEquals(5L, user.getParentId());
        System.out.println("Skenario 13: getParentId() mengembalikan id parent - BERHASIL");
    }

    @Test
    @DisplayName("Test 14: setUsername() mengubah username")
    public void testSetUsername() {
        // Mengubah username dari leo123 ke leo_new
        User user = new User(1, "Leo", "leo123", "parent", 0);
        user.setUsername("leo_new");
        assertEquals("leo_new", user.getUsername());
        System.out.println("Skenario 14: setUsername() mengubah username - BERHASIL");
    }

    @Test
    @DisplayName("Test 15: hitungScoreKesehatan() anak umur < 5 tahun")
    public void testHitungScoreKesehatanAnakKecil() {
        // Anak umur 4 tahun tanpa aktivitas
        User user = new User(1, "Baby", 0, "child", 1L, 4);
        int score = user.hitungScoreKesehatan();
        assertEquals(100, score);
        System.out.println("Skenario 15: hitungScoreKesehatan() anak < 5 tahun - BERHASIL");
    }
}
