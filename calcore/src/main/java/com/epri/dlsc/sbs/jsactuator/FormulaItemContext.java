package com.epri.dlsc.sbs.jsactuator;

import java.util.HashMap;
import java.util.Map;

import com.epri.dlsc.sbs.util.CommonUtil;

public class FormulaItemContext {
	
	private Map<String, Map<String, String>> context;
	
	void set(String key, Map<String, String> value){
		if(context == null){
			context = new HashMap<>();
		}
		context.put(key, value);
	}
	
	public DataSetJsObject returnDataSetJsObject(String key){
		Map<String, String> jsObject = null;
		if(context != null){
			jsObject = context.get(key);
		}
		return new DataSetJsObject(jsObject);
	}
	
	public class DataSetJsObject {
		private Map<String, String> dataSetObject;
		private DataSetJsObject(){}
		DataSetJsObject(Map dataSetObject){
			this.dataSetObject = dataSetObject;
		}
		public String get(String attribute){
			if(attribute == null || "".equals(attribute.trim())){
				return null;
			}
			if(dataSetObject == null){
				return null;
			}
			attribute = CommonUtil.getMD5(attribute.trim());
			String value = dataSetObject.get(attribute);
			return value;
		}
	}
}
