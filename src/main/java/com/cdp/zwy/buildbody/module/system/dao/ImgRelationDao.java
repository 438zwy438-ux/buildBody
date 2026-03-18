package com.cdp.zwy.buildbody.module.system.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;

/**
 * 图片关系表(ImgRelation)表数据库访问层
 *
 * @author makejava
 * @since 2026-03-18 11:18:52
 */
public interface ImgRelationDao extends BaseMapper<ImgRelation> {

/**
* 批量新增数据（MyBatis原生foreach方法）
*
* @param entities List<ImgRelation> 实例对象列表
* @return 影响行数
*/
int insertBatch(@Param("entities") List<ImgRelation> entities);

/**
* 批量新增或按主键更新数据（MyBatis原生foreach方法）
*
* @param entities List<ImgRelation> 实例对象列表
* @return 影响行数
* @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
*/
int insertOrUpdateBatch(@Param("entities") List<ImgRelation> entities);

}

