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

	private String expressionText;// 数据集完整表达式

	private String dataSetId;// 数据集id

	private String valueFieldId; // 数据项id

	private Set<String> wheres;// 匹配条件

	private Map<String, String> filters;// 过滤条件
	
	public DataSetExpression(String expressionText, String dataSetId, String valueFieldId,
			Set<String> wheres, Map<String, String> filters) {
		this.expressionText = expressionText;
		this.dataSetId = dataSetId;
		this.valueFieldId = valueFieldId;
		this.wheres = wheres;
		this.filters = filters;
	}

	public String getExpressionText() {
		return expressionText;
	}

	public String getDataSetId() {
		return dataSetId;
	}

	public String getValueFieldId() {
		return valueFieldId;
	}

	public Set<String> getWheres() {
		return wheres;
	}

	public Map<String, String> getFilters() {
		return filters;
	}


}
