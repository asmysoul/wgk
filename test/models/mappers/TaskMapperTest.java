package models.mappers;

import static org.junit.Assert.*;

import java.util.List;

import models.Task;

import org.junit.Test;

import vos.TaskSearchVo;

import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.test.UnitTest;

import enums.Device;
import enums.Platform;


/**
 * 
 * TODO Comment.
 * 
 * @author youblade
 * @since  v0.2
 * @created 2014年10月22日 下午7:52:05
 */
public class TaskMapperTest extends UnitTest {

    TaskMapper mapper = SessionFactory.getSqlSession().getMapper(TaskMapper.class);
    
    /**
     * Test method for {@link models.mappers.TaskMapper#countSimple(vos.TaskSearchVo)}.
     */
    @Test
    public void testCountSimple() {
        DBHelper.truncate(Task.TABLE_NAME);
        
        Task.instanceForTest(1999).setSellerId(1).platform(Platform.TMALL).orderNum(Device.PC, 10).create();
        
        TaskSearchVo vo = TaskSearchVo.newInstance().pageNo(1).pageSize(1);
        assertEquals(1, mapper.count(vo));
    }

    /**
     * Test method for {@link models.mappers.TaskMapper#selectSimple(vos.TaskSearchVo)}.
     */
    @Test
    public void testSelectSimple() {
        TaskSearchVo vo = TaskSearchVo.newInstance("id").pageNo(1).pageSize(100);
        List<Task> list = mapper.selectSimple(vo);
        assertEquals(mapper.count(vo), list.size());
    }

    /**
     * Test method for {@link models.mappers.TaskMapper#mobilePublishAndNotTokenOver()}.
     */
    @Test
    public void testMobilePublishAndNotTokenOver() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link models.mappers.TaskMapper#pcPublishAndNotTokenOver()}.
     */
    @Test
    public void testPcPublishAndNotTokenOver() {
        fail("Not yet implemented");
    }

}
