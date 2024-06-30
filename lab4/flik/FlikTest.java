package flik;
import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void Test128() {
        assertTrue(Flik.isSameNumber(128, 128));
    }
}
