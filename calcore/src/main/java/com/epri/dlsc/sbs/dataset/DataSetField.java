package com.epri.dlsc.sbs.dataset;

import java.io.Serializable;

public class DataSetField implements Serializable{
	//字符串
	final public static int STRING = 1;
	//数字
	final public static int NUMBER = 2;
	//日期
	final public static int DATE = 3;
	
	//字段ID
	private String ID;
	//字段名称
	private String fieldName;
	//字段数据类型
	private int dataType;
	//是否为约束条件
	private boolean isConstraint;
	/**
	 * 数据集配置字段信息
	 *  @param ID
	 *  @param fieldName
	 *	@param dataType
	 *	@param isConstraint
	 */
	public DataSetField(String ID, String fieldName, int dataType, boolean isConstraint){
		this.ID = ID;
		this.fieldName = fieldName;
		this.dataType = dataType;
		this.isConstraint = isConstraint;
	}
	public String getID() {
		return ID;
	}

	public String getFieldName() {
		return fieldName;
	}

	public int getDataType() {
		return dataType;
	}
	public boolean isConstraint() {
		return isConstraint;
	}
}
