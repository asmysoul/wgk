package enums;

import static org.junit.Assert.*;

import org.junit.Test;


public class BuyerTaskStepTypeTest {

    @Test
    public void testGetNext() {
        BuyerTaskStepType second = BuyerTaskStepType.CHOOSE_ITEM.getNext();
        assertEquals(BuyerTaskStepType.VIEW_AND_INQUIRY, second);
    }

}
