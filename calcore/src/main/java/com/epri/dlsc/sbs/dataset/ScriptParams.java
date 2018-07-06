package com.epri.dlsc.sbs.dataset;

public class ScriptParams {
	private String paramName;
	private String paramExpression;

	public ScriptParams(String paramName, String paramExpression){
		this.paramName = paramName;
		this.paramExpression = paramExpression;
	}
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}


	public String getParamExpression() {
		return paramExpression;
	}
	public void setParamExpression(String paramExpression) {
		this.paramExpression = paramExpression;
	}
}
