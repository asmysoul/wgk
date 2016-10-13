package models;

import java.io.File;
import java.util.List;

import models.mappers.RegionMapper;

import org.apache.ibatis.session.SqlSession;

import com.aton.config.Config;
import com.aton.db.SessionFactory;
import com.aton.test.DBHelper;
import com.aton.util.CollectionUtils;
import com.aton.util.FileUtils;
import com.aton.util.JsonUtil;

/**
 * 
 * 行政区域（省、市、区等）.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年7月16日 下午1:57:28
 */
public class Region {

	public static final String TABLE_NAME = "region";
    public int id;
    public String name;
    public int parent_id;
    
    /**
     * 区域类型
     * .area区域
     *  1:country/国家;
     *  2:province/省/自治区/直辖市;
     *  3:city/地区(省下面的地级市);
     *  4:district/县/市(县级市)/区;
     *  abroad:海外. 
     *  比如北京市的area_type = 2,朝阳区是北京市的一个区,所以朝阳区的area_type = 4.
     */
    public int type;
    public String zip;
    
    
	/**
	 * 
	 * 根据父id取得区域数据
	 * 
	 * @param id
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-7-28 下午5:37:31
	 */
	public static List<Region> findByParentId(int id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			RegionMapper rmapper = ss.getMapper(RegionMapper.class);
			return rmapper.selectByParentId(id);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 取中国区域的省、直辖市.
	 *
	 * @return
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月18日 下午9:34:48
	 */
	public static List<Region> findRoot() {
	    SqlSession ss = SessionFactory.getSqlSession();
	    try {
	        RegionMapper rmapper = ss.getMapper(RegionMapper.class);
	        return rmapper.selectByParentId(1);
	    } finally {
	        ss.close();
	    }
	}
	
	public static void batchSave(List<Region> list) {
	    SqlSession ss = SessionFactory.getSqlSessionForBatch();
	    try {
	        RegionMapper mapper = ss.getMapper(RegionMapper.class);
	        for (Region region : list) {
	            mapper.insert(region);
            }
	        ss.commit();
	    } finally {
	        ss.close();
	    }
	}
	
	/**
	 * 
	 * 初始化导入地区数据.
	 *
	 * @since  0.1
	 * @author youblade
	 * @created 2014年9月18日 下午9:46:39
	 */
    public static void init() {
        // 取省（直辖市）数据判断数据是否已导入过
        List<Region> list = Region.findRoot();
        if (CollectionUtils.isEmpty(list) || list.size() < 10) {
            // 清除残留数据
            DBHelper.truncate(Region.TABLE_NAME);
            
            String json = FileUtils.readFileToString(new File(Config.testDataDir, "tbregion.json"));
            List<Region> regions = JsonUtil.toList(json, Region.class);
            // 批量写入
            Region.batchSave(regions);
        }
    }
}
