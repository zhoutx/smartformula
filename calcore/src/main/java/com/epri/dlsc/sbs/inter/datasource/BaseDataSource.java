package com.epri.dlsc.sbs.inter.datasource;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.epri.dlsc.sbs.exception.CalEngineException;
import com.epri.dlsc.sbs.exception.DataNotUnique;
import com.epri.dlsc.sbs.dataset.DataSetExpression;
import com.epri.dlsc.sbs.dataset.DataSetField;
import com.epri.dlsc.sbs.dataset.ResultSet;

public abstract class BaseDataSource implements DataSource, Serializable {
	//加载数据
	public abstract void load();
	
	public abstract Collection<DataSetField> getDataSetFields();
	
	public abstract ResultSet returnResultSet();
	
	@Override
	public List<ResultSet.Row> getDatas(Map<String, String> filter) {
		return getResultSet().getDatas(filter);
	}

	@Override
	public ResultSet.Row getUniqueData(String ID) throws DataNotUnique {
		ResultSet.Row data = getResultSet().getData(ID);
		if(data == null){
			if(ID != null){
				String[] conds = ID.split("@");
				if(conds != null){
					Map params = new HashMap();
					for(String cond : conds){
						String[] a = cond.split("=");
						params.put(a[0], a[1]);
					}
					List<ResultSet.Row> datas = getDatas(params);
					if(datas != null && datas.size() > 1){
						throw new DataNotUnique("获取数据不唯一");
					}else if(datas != null && datas.size() == 1){
						data = datas.get(0);
					}
				}
			}
		}
		return data;
	}

	@Override
	public String getUniqueDataValue(String ID, String fieldName) throws DataNotUnique {
		ResultSet.Row row = getUniqueData(ID);
		if(row == null){
			return null;
		}
		return row.getFieldValue(fieldName);
	}
	@Override
	public ResultSet getResultSet() {
		return returnResultSet() == null ? new ResultSet() : returnResultSet();
	}
	
	public Map<String, String> getUniqueConstraintFilters(ResultSet.Row calcuateRow, DataSetExpression expression){
		Map filtersMap = null;
		if (expression != null) {
			Set<String> wheres = expression.getWheres();
			Map<String, String> filters = expression.getFilters();
			filtersMap = new HashMap<String, String>();
			if (wheres != null && wheres.size() > 0) {
				for (String where : wheres) {
					filtersMap.put(where, calcuateRow.getFieldValue(where));
				}
			} else {
				String msg = "数据集[" + expression.getDatasetId() + "]未指定where条件";
				throw new NullPointerException(msg);
			}
			if (filters != null && filters.size() > 0) {
				for (Entry<String, String> entry : filters.entrySet()) {
					filtersMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return filtersMap;
	}
	public String getUniqueConstraintId(ResultSet.Row calcuateRow, DataSetExpression expression){
		String key = "";
		if (getUniqueConstraint() == null) {
			throw new CalEngineException("数据集[" + getDataSetId() + "]唯一条件不可为空");
		}
		Set<String> uniqueFieldsCopy = new HashSet<String>();
		if (expression.getWheres() != null && expression.getWheres().size() > 0) {
			uniqueFieldsCopy.addAll(expression.getWheres());
		}
		if (expression.getFilters() != null && expression.getFilters().size() > 0) {
			uniqueFieldsCopy.addAll(expression.getFilters().keySet());
		}
		if (!getUniqueConstraint().getUniqueFieldIDs().containsAll(uniqueFieldsCopy)) {
			throw new CalEngineException("数据集[" + getDataSetId() + "]过滤条件不合法");
		}
		for(String fieldID : getUniqueConstraint().getUniqueFieldIDs()){
			if (expression.getWheres() != null && expression.getWheres().size() > 0) {
				if (expression.getWheres().contains(fieldID)) {
					key += "@" + fieldID + "=" + calcuateRow.getFieldValue(fieldID);
				}
			}
			if (expression.getFilters() != null) {
				if (expression.getFilters().keySet().contains(fieldID)) {
					key += "@" + fieldID + "=" + expression.getFilters().get(fieldID);
				}
			}
		}
		if (key != null && !key.equals("") && key.startsWith("@")) {
			key = key.substring(1);
		} else {
			throw new CalEngineException("数据集[" + getDataSetId() + "]未指定wheres和filters条件");
		}
		return key;
	}
}
