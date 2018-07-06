package com.epri.dlsc.sbs.dataset;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 数据集表达式类
 * 
 * @author Zhong.Weijian
 *
 */
public class DataSetExpression implements Serializable{

	private String exps;// 数据集完整表达式

	private String datasetId;// 数据集id

	private String filedId; // 数据项id

	private Set<String> wheres;// 匹配条件

	private Map<String, String> filters;// 过滤条件
	
	public DataSetExpression(String exps, String datasetId, String filedId,
			Set<String> wheres, Map<String, String> filters) {
		this.exps = exps;
		this.datasetId = datasetId;
		this.filedId = filedId;
		this.wheres = wheres;
		this.filters = filters;
	}

	public String getExps() {
		return exps;
	}

	public void setExps(String exps) {
		this.exps = exps;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getFiledId() {
		return filedId;
	}

	public void setFiledId(String filedId) {
		this.filedId = filedId;
	}

	public Set<String> getWheres() {
		return wheres;
	}

	public void setWheres(Set<String> wheres) {
		this.wheres = wheres;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

}
