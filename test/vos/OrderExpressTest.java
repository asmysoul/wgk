package vos;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.aton.config.Config;
import com.aton.test.UnitTest;
import com.aton.util.ExcelUtil;
import com.aton.util.MixHelper;

public class OrderExpressTest extends UnitTest {
        
    @Test
    public void test_read() throws Exception{
        
        File dataXLS = new File(Config.testDataDir, "orderExpress.xls");
        assertNotNull(dataXLS);
        File xmlConfig = new File(Config.confDir, "excel/orderExpressConfig.xml");
        assertNotNull(xmlConfig);

        List<OrderExpress> list = ExcelUtil.parseExcelFileToBeans(dataXLS, xmlConfig);
        assertTrue(MixHelper.isNotEmpty(list));
        for (OrderExpress e : list) {
            MixHelper.print(e.number);
            MixHelper.print(e.fullAddress);
            MixHelper.print(e.expressNo);
        }
        MixHelper.print(list.size());
    }

}
