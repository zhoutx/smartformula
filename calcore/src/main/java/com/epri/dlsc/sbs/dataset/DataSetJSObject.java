package com.epri.dlsc.sbs.dataset;

public class DataSetJSObject {
	private String expression;
	private String dataSetID;
	
	public DataSetJSObject(String expression, String dataSetID){
		this.expression = expression;
		this.dataSetID = dataSetID;
	}

	public String getExpression() {
		return expression;
	}

	public String getDataSetID() {
		return dataSetID;
	}
	
}
