package testing;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite untuk menjalankan semua unit tests
 * Total: 90 test cases
 */
@Suite
@SuiteDisplayName("Mental Wellbeing App - All Tests")
@SelectClasses({
    AktivitasDigitalTest.class,
    AppTimerTest.class,
    TopUpTest.class,
    NotifikasiTest.class,
    LaporanHarianTest.class,
    UserTest.class
})
public class AllTestsSuite {
    // Test suite untuk menjalankan semua test sekaligus
    // Run this class untuk execute semua 90 test cases
}
