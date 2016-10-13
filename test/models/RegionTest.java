package models;

import java.io.File;
import java.util.List;

import models.mappers.RegionMapper;
import net.sf.oval.constraint.AssertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import play.test.UnitTest;

import com.aton.config.Config;
import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.util.FileUtils;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;

public class RegionTest extends UnitTest {

	@Test
	public void test_findByParentId() {
		List<Region> list = Region.findByParentId(0);
		assertTrue(list.size() > 0);
	}
	
	@Test
    public void test() {
	    DBHelper.truncate(Region.TABLE_NAME);
	    
        String json = FileUtils.readFileToString(new File(Config.testDataDir, "tbregion.json"));
        assertTrue(MixHelper.isNotEmpty(json));
        
        List<Region> regions = JsonUtil.toList(json, Region.class);
        assertEquals(4440, regions.size());
        
        for (Region region : regions) {
            assertNotNull(region.id);
            assertNotNull(region.name);
            assertNotNull(region.parent_id);
            
            MixHelper.print("{}-{}-{}",region.id,region.name,region.parent_id);
        }
        
        // 批量写入
        Region.batchSave(regions);
        List<Region> provinces = Region.findRoot();
        for (Region p : provinces) {
            MixHelper.print("{}-{}", p.id, p.name);
        }
    }

}
