package com.epri.dlsc.sbs.dataset;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.epri.dlsc.sbs.inter.datasource.BaseDataSource;
import com.epri.dlsc.sbs.inter.datasource.DataSource;

public class DataSet implements Serializable{
	private Map<String, DataSource> dataSourceMap;
	public DataSource getDataSource(String dataSetId){
		if(dataSourceMap == null){
			return null;
		}
		return dataSourceMap.get(dataSetId);
	}
	
	public Collection<DataSource> getInputDatas(){
		if(dataSourceMap == null){
			return null;
		}
		return dataSourceMap.values();
	}

	public void addDataSource(DataSource dataSource){
		if(dataSourceMap == null){
			dataSourceMap = new HashMap<String, DataSource>();
		}
		dataSourceMap.put(dataSource.getDataSetId(), dataSource);
	}
	
	public void removeDataSource(String dataSetId){
		if(dataSourceMap != null){
			dataSourceMap.remove(dataSetId);
		}
	}
	
	public void loadOrUpdate(){
		if(dataSourceMap != null && dataSourceMap.size() > 0){
			for(DataSource dataSource : dataSourceMap.values()){
				((BaseDataSource) dataSource).load();
			}
		}
	}

	public static class DataRecord{

	}
}
