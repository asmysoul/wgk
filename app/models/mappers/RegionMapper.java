package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import models.Region;
import models.Shop;

public interface RegionMapper {

	@Select("select * from " + Region.TABLE_NAME + " where parent_id = #{id}")
	List<Region> selectByParentId(int id);

	@Insert("insert into " + Region.TABLE_NAME + " values(#{id},#{name},#{parent_id},#{type},#{zip})")
    void insert(Region region);
}
