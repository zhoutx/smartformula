package com.epri.dlsc.sbs.function;

import java.io.Serializable;

/**
 * 函数表达式类
 * 
 * @author Zhong.Weijian
 *
 */
public class FunctionExpression {

	private String expression;// 函数完整表达式

	private String functionId;// 函数id

	private ArgumentValue[] argumentValues;// 函数参数

	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String exps) {
		this.expression = exps;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public ArgumentValue[] getFunctionArgs() {
		return argumentValues;
	}

	public void setFunctionArgs(ArgumentValue[] argumentValues) {
		this.argumentValues = argumentValues;
	}
	/**
	 * 函数表达式实参列表
	 */
	public static class ArgumentValue{
		public int dataType;
		public Serializable value;
		public ArgumentValue(int dataType, Serializable value){
			this.dataType = dataType;
			this.value = value;
		}
	}
}



