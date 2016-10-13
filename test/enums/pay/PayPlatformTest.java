package enums.pay;

import static org.junit.Assert.*;

import org.junit.Test;


public class PayPlatformTest {

    @Test
    public void testBuildFromKQpayPlatform() {
        String bankId = "PSBC";
        PayPlatform bank = PayPlatform.buildFrom(KQpayPlatform.buildFromCode(bankId));
        assertEquals(PayPlatform.POST, bank);
    }

}
