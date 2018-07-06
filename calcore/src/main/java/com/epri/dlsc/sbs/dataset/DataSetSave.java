package com.epri.dlsc.sbs.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集保存信息
 * 
 * @author Zhong.Weijian
 * 
 */
public class DataSetSave implements Serializable{

	private String saveTable;// 数据源入库表名称

	private List<DataSetField> requiredSaveFields;//需要保存的字段
	
	private Map<String, String> fieldSaveMapping;// 入库字段映射，{自定义字段名称:入库表字段名称（入库表table字段名称）}
	
	public DataSetSave(String saveTable){
		this.saveTable = saveTable;
	}
	
	public String getSaveTargetFieldName(DataSetField field){
		if(fieldSaveMapping == null){
			return null;
		}
		return fieldSaveMapping.get(field.getID());
	}
	
	public String getSaveTable() {
		return saveTable;
	}

	public List<DataSetField> getRequiredSaveFields(){
		return requiredSaveFields;
	}
	
	public void addSaveField(DataSetField field, String targetFieldName){
		if(fieldSaveMapping == null){
			fieldSaveMapping = new HashMap<String, String>();
		}
		if(!fieldSaveMapping.containsKey(field.getID())){
			if(requiredSaveFields == null){
				requiredSaveFields = new ArrayList<DataSetField>();
			}
			requiredSaveFields.add(field);
		}
		fieldSaveMapping.put(field.getID(), targetFieldName);
		
	}
	public void setSaveTable(String saveTable) {
		this.saveTable = saveTable;
	}
}
