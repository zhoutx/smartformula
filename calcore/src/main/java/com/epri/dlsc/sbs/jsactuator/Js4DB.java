package com.epri.dlsc.sbs.jsactuator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.epri.dlsc.sbs.inter.db.DBManager;

/**
 * 版本要求 JDK1.8
 */
public class Js4DB {
	
	private static Map<String, SavingTable> savingTables;
	//private static String STRING = "STRING";
	private static String DATE = "DATE";
	//private static String NUMBER = "NUMBER";
	/**
	 * JS中DB.save()方法
	 * @param tableName
	 * @param uk
	 * @param dataType
	 * @param row
	 */
	public void save(String tableName, String[] uk, Map dataType, Map row){
		
		tableName = tableName.toUpperCase();
		
		if(savingTables == null ){
			savingTables = new HashMap<>();
		}
		if(!savingTables.containsKey(tableName)){
			SavingTable savingTable = new SavingTable(tableName, uk, dataType);
			savingTables.put(tableName, savingTable);
		}
		
		savingTables.get(tableName).setData(row);
	}
	/**
	 * 数据存储至数据库
	 */
	public synchronized static void savingTablesToDB(){
		if(savingTables != null && savingTables.size() > 0){
			for(SavingTable table : savingTables.values()){
				table.save();
			}
			savingTables.clear();
			savingTables = null;
		}
	}
	/**
	 * 要存储的表信息
	 */
	private class SavingTable{
		private String tableName;
		private String[] uk;
		private Set<String> ukSet;
		private Map<String, String> dataTypes;
		private String delSql;
		private String insSql;
		private List<Object[]> saveDatas = new ArrayList<>();
		private List<Object[]> deleDatas = new ArrayList<>();
		
		public SavingTable(String tableName, String[] uk, Map<String, String> dataTypes) {
			/**
			 * 为了保证数据安全，需要校验uk是否能够唯一
			 */
			
			this.tableName = tableName.toUpperCase();
			this.uk = uk;
			this.dataTypes = dataTypes;
			
			if(uk != null){
				ukSet = new HashSet<>();
				for(String ukCol : uk){
					ukSet.add(ukCol);
				}
			}
			
			StringBuilder _columns = new StringBuilder();
			StringBuilder _saveValuesFormat = new StringBuilder();
			StringBuilder _deleValuesFormat = new StringBuilder();
			if(dataTypes != null && dataTypes.size() > 0){
				for(String col : this.dataTypes.keySet()){
					_columns.append(col.toUpperCase()).append(",");
					
					String colDataType = dataTypes.get(col);
					
					if(colDataType != null && DATE.equals(colDataType.toUpperCase())){
						_saveValuesFormat.append("TO_DATE(?,'YYYY-MM-DD HH24:MI:SS'),");
						
					}else{
						_saveValuesFormat.append("?,");
					}
					
					//删除
					if(ukSet.contains(col)){
						if(_deleValuesFormat.length() > 0){
							_deleValuesFormat.append(" AND ");
						}
						_deleValuesFormat.append(col.toUpperCase())
						.append("=");
						if(colDataType != null && DATE.equals(colDataType.toUpperCase())){
							_deleValuesFormat.append("TO_DATE(?,'YYYY-MM-DD HH24:MI:SS')");
							
						}else{
							_deleValuesFormat.append("?");
						}
					}
				}
			}
			insSql = new StringBuilder()
					 .append("INSERT INTO ")
					 .append(this.tableName)
					 .append(" (")
					 .append(_columns.substring(0, _columns.length() - 1))
					 .append(") VALUES (")
					 .append(_saveValuesFormat.substring(0, _saveValuesFormat.length() - 1))
					 .append(")").toString();
			delSql = new StringBuilder()
					 .append("DELETE FROM ")
					 .append(this.tableName)
					 .append(" WHERE ")
					 .append(_deleValuesFormat).toString();
					 
		}
		/**
		 *数据表的存储数据 
		 */
		private void setData(Map row){
			if(dataTypes != null && dataTypes.size() > 0){
				Object[] saveData = new Object[dataTypes.size()];
				Object[] deleData = new Object[uk.length];
				int saveIndex = 0;
				int deleIndex = 0;
				for(String key : this.dataTypes.keySet()){
					saveData[saveIndex] = row.get(key);
					if(ukSet.contains(key)){
						deleData[deleIndex] = saveData[saveIndex];
						++deleIndex;
					}
					++saveIndex;
				}
				saveDatas.add(saveData);
				deleDatas.add(deleData);
			}
		}
		/**
		 * 数据存储执行方法
		 */
		private void save(){
			try{
				if(deleDatas != null && deleDatas.size() > 0){
					DBManager.getDBCollection().batchExecuteSql(delSql, deleDatas);
				}
				if(saveDatas != null && saveDatas.size() > 0){
					DBManager.getDBCollection().batchExecuteSql(insSql, saveDatas);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(deleDatas != null){
					deleDatas.clear();
					deleDatas = null;
				}
				if(saveDatas != null){
					saveDatas.clear();
					saveDatas = null;
				}
			}
		}
	}
}
