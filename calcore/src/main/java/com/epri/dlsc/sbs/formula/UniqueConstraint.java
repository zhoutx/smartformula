package com.epri.dlsc.sbs.formula;

import com.epri.dlsc.sbs.dataset.DataSetField;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * 约束条件
 * @author zhoutx
 *
 */
public class UniqueConstraint implements Serializable{
	
	private Map<String, DataSetField> fieldMapping;
	
	public void addUniqueConstraintField(DataSetField field){
		if(fieldMapping == null){
			fieldMapping = new HashMap<String, DataSetField>();
		}
		fieldMapping.put(field.getID(), field);
	}
	
	public Set<String> getUniqueFieldIDs(){
		if(fieldMapping == null){
			return null;
		}
		return fieldMapping.keySet();
	}
	
	public Collection<DataSetField> getUniqueFields(){
		if(fieldMapping == null){
			return null;
		}
		return fieldMapping.values();
	}
	
}
