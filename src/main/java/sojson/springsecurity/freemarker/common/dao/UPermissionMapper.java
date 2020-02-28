package sojson.springsecurity.freemarker.common.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;

import sojson.springsecurity.freemarker.common.model.UPermission;
import sojson.springsecurity.freemarker.permission.bo.UPermissionBo;

@Mapper
public interface UPermissionMapper {
	int deleteByPrimaryKey(Long id);

	int insert(UPermission record);

	int insertSelective(UPermission record);

	UPermission selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(UPermission record);

	int updateByPrimaryKey(UPermission record);

	List<UPermissionBo> selectPermissionById(Long id);

	//根据用户ID获取权限的Set集合
	Set<String> findPermissionByUserId(Long id);
}