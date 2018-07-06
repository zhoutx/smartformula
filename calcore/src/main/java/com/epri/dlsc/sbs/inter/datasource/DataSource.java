package com.epri.dlsc.sbs.inter.datasource;

import java.util.List;
import java.util.Map;

import com.epri.dlsc.sbs.exception.DataNotUnique;
import com.epri.dlsc.sbs.dataset.DataSetField;
import com.epri.dlsc.sbs.dataset.DataSetSave;
import com.epri.dlsc.sbs.dataset.ResultSet;
import com.epri.dlsc.sbs.formula.UniqueConstraint;

/**
 * 数据源
 * @author zhoutx
 *
 */
public interface DataSource {
	/**
	 * 获取数据集ID
	 * @return
	 */
	public String getDataSetId();
	
	public String getDataSetName();
	/**
	 * 获取数据源数据
	 * @return ResultSet
	 */
	public abstract ResultSet getResultSet();
	/**
	 * 根据过滤条件获取数据源数据
	 * @param filter 条件
	 * @return List<Row>
	 */
	public List<ResultSet.Row> getDatas(Map<String, String> filter);
	/**
	 * 根据一行匹配数据和数据集获取唯一数据（一对一）
	 * @param ID 数据ID
	 * @return Row
	 */
	public ResultSet.Row getUniqueData(String ID) throws DataNotUnique;
	
	public String getUniqueDataValue(String ID, String fieldName) throws DataNotUnique;
	/**
	 * 数据持久化
	 */
	public void save(List<ResultSet.Row> saveRows);
	/**
	 * 获取数据源字段信息
	 * @return DataSetField
	 */
	public DataSetField getDataSetField(String fieldID);
	
	/**
	 * 获取约束条件
	 */
	public UniqueConstraint getUniqueConstraint();
	/**
	 * 获取数据源持久化配置信息
	 * @return DataSetSave
	 */
	public DataSetSave getDataSetSave();
	
}
