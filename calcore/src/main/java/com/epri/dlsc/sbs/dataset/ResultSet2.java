package com.epri.dlsc.sbs.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.epri.dlsc.sbs.inter.datasource.DataSource;

import java.util.Set;
/**
 * 结果集
 * @author zhoutx
 */
public class ResultSet2 implements Serializable{
	private DataSource dataSource;
	private Map<String, Map<String, String>> datas;
	private DatasetIndex dataIndex;
	public ResultSet2(){}
	public ResultSet2(DataSource dataSource){
		this.dataSource = dataSource;
	}
	//获取数据条数
	public int size(){
		if(datas == null){
			return 0;
		}
		return datas.size();
	}
	public void addData(String id, Map<String, String> data){
		if(datas == null){
			datas = new HashMap<String, Map<String, String>>();
		}
		this.datas.put(id, data);
		if(data != null && data.size() > 0){
			if(dataIndex == null){
				dataIndex = new DatasetIndex();
			}
			for(String constraintField : dataSource.getUniqueConstraint().getUniqueFieldIDs()){
				String value = data.get(constraintField);
				dataIndex.add(constraintField + "=" + value, id);
			}
		}
	}
	
	public void removeData(String id){
		if(datas != null){
			datas.remove(id);
		}
		if(dataIndex != null){
			dataIndex.removeIndex(id);
		}
	}
	
	//获取数据集所有数据
	public List<Row> getDatas(){
		if(datas == null || datas.size() < 1){
			return null;
		}
		List<Row> rows = new ArrayList<Row>(datas.size());
		for(Entry<String,Map<String, String>> entry : datas.entrySet()){
			rows.add(new Row(entry.getKey(), entry.getValue()));
		}
		return rows;
	}
	
	public List<Row> getDatas(String[] ids){
		if(datas == null || ids == null || ids.length < 1){
			return null;
		}
		List<Row> result = new ArrayList<Row>(ids.length);
		for (int i = 0; i < ids.length; i++) {
			Map<String, String> value = datas.get(ids[i]);
			if(value != null){
				Row row = new Row(ids[i], datas.get(ids[i]));
				result.add(row);
			}else{//附带清除可能冗余的索引信息
				if(dataIndex != null){
					dataIndex.removeIndex(ids[i]);
				}
			}
		}
		return result;
	}
	
	public List<Row> getDatas(Map<String, String> filter){
		if(dataIndex == null || filter == null){
			return null;
		}
		String[] keys = new String[filter.size()];
		int i = 0;
		for(Entry<String, String> entry : filter.entrySet()){
			keys[i++] = entry.getKey() + "=" + entry.getValue();
		}
		String[] ids = dataIndex.get(keys);
		return getDatas(ids);
	}
	
	//按指定数据ID获取数据
	public Row getData(String id){
		if(datas == null || datas.size() < 1){
			return null;
		}
		Map data = datas.get(id);
		if(data == null){
			return null;
		}
		return new Row(id, data);
	}
	
	public Map<String, Map<String, String>> resultSetTransformMap(){
		return datas;
	}
	/**每行数据**/
	public class Row{
		private Map<String, String> data;
		private boolean isValueChanged = false;
		private String id;
		public Row(String id, Map<String, String> data){
			this.id = id;
			this.data = data;
		}
		public String getId(){
			return id;
		}
		public String getFieldValue(String fieldID){
			return data.get(fieldID);
		}
		public String[] getFieldIDs(){
			Set<String> keys = data.keySet();
			if(keys == null || keys.size() < 0){
				return null;
			}
			return keys.toArray(new String[keys.size()]);
		}
		public void setFieldValue(String fieldID, String value){
			if(data.containsKey(fieldID)){
				data.put(fieldID, value);
				isValueChanged = true;
			}
		}
		public boolean isChanged(){
			return isValueChanged;
		}
		public void clearChangedFlag(){
			isValueChanged = false;
		}
		public Map<String, String> RowTransformMap(){
			return data;
		}
	}
	private class DatasetIndex implements Serializable{
		// 数据索引,{'自定义字段1=值1':[数据标识id1,数据标识id2,...]}
		private Map<String, Set<String>> datasetIndex;
		private Map<String, Set<String>> datasetValueIndex;
		
		/**
		 * 添加索引
		 * 
		 * @param key
		 *            键
		 * @param ID
		 *            值
		 */
		public void add(String key, String ID) {
			if (datasetIndex == null){
				datasetIndex = new HashMap<String, Set<String>>();
			}
			if (!datasetIndex.containsKey(key)){
				datasetIndex.put(key, new HashSet<String>());
			}
			datasetIndex.get(key).add(ID);
			
			if(datasetValueIndex == null){
				datasetValueIndex = new HashMap<String, Set<String>>();
			}
			if(!datasetValueIndex.containsKey(ID)){
				datasetValueIndex.put(ID, new HashSet<String>());
			}
			datasetValueIndex.get(ID).add(key);
		}
		//删除索引
		public void removeIndex(String id){
			Set<String> keys = datasetValueIndex.get(id);
			if(keys != null && keys.size() > 0){
				for(String key : keys){
					Set<String> ins = datasetIndex.get(key);
					if(ins != null){
						ins.remove(id);
					}
					if(ins.size() == 0){
						datasetIndex.remove(key);
					}
				}
			}
			datasetValueIndex.remove(id);
		}
		
		/**
		 * 获取索引
		 * 
		 * @param key
		 *            键
		 * @return
		 */
		public Set<String> get(String key) {
			Set<String> ids = datasetIndex.get(key);
			if (ids != null) {
				Set<String> result = new HashSet<String>();
				result.addAll(ids);
				return result;
			}
			return null;
		}

		/**
		 * 获取索引
		 * 
		 * @param keys
		 *            多个键
		 * @return
		 */
		public String[] get(String... keys) {
			Set<String> result = null;
			List<Set<String>> orderList = new ArrayList<Set<String>>();
			//找出最小的分组，最小分组Set使用retainAll方法效率比较高
			int minIndex = 0;
			int minSize = 0;
			for (int i = 0; i < keys.length; i++) {
				Set<String> set = datasetIndex.get(keys[i]);
				if (set == null || set.size() == 0) {
					return null;
				} else {
					if(minIndex == 0 && minSize == 0){
						minSize = set.size();
						minIndex = i;
					}else{
						if(minSize > set.size()){
							minSize = set.size();
							minIndex = i;
						}
					}
					orderList.add(set);
				}
			}
			for(int i = 0; i < orderList.size(); i++){
				if(result == null){
					result = new HashSet<String>();
					result.addAll(orderList.get(minIndex));
				}if(i != minIndex){
					result.retainAll(orderList.get(i));
				}
			}
			return result.toArray(new String[result.size()]);
		}
	}

}
