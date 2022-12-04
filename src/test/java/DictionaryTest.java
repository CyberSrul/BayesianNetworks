import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyException;

class DictionaryTest {

    static Dictionary dict;

    @BeforeEach
    void setUp() {

        dict = new Dictionary();
        Assertions.assertDoesNotThrow(() -> dict.log("A").log("B").log("C").log("T").log("F").log("????"));
    }

    @Test
    void size() {

        Assertions.assertEquals(6, dict.Size());
        Assertions.assertDoesNotThrow(() -> dict.log("A"));
        Assertions.assertEquals(6, dict.Size());
    }

    @Test
    void translate() throws KeyException {

        Assertions.assertEquals(0, dict.translate("A"));
        Assertions.assertEquals(1, dict.translate("B"));
        Assertions.assertEquals(2, dict.translate("C"));
        Assertions.assertEquals(3, dict.translate("T"));
        Assertions.assertEquals(4, dict.translate("F"));
        Assertions.assertEquals(5, dict.translate("????"));

        Assertions.assertThrows(KeyException.class, () -> dict.translate("not there"));
    }
}