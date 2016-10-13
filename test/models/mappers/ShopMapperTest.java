package models.mappers;

import java.io.File;

import models.Shop;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.db.DB;
import play.test.UnitTest;

import com.aton.db.SessionFactory;
import com.aton.util.FileUtils;


public class ShopMapperTest extends UnitTest {

    static SqlSession ss;
    static ShopMapper mapper;
    
    @BeforeClass
    public static void setUp(){
        ss  = SessionFactory.getSqlSession();
        mapper = ss.getMapper(ShopMapper.class);
    }
    @AfterClass
    public static void tearDown(){
        ss.close();
    }
    
    @Test
    public void test() {
        Shop shop = new Shop();
        shop.name = "test";
        assertNull( mapper.selectExists(shop));
//        assertFalse(isExists);
        
        mapper.insert(shop);
        assertNotNull(mapper.selectExists(shop));
    }

}
