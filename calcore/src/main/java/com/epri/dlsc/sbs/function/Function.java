package com.epri.dlsc.sbs.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Function implements Serializable{
	
	private String functionId;
	
	private String functionTextName;
	
	private String functionFullName;
	
	private List<FunctionArg> args;// 函数参数信息
	
	public Function(String functionId, String functionFullName, String functionTextName){
		this.functionId = functionId;
		this.functionFullName = functionFullName;
		this.functionTextName = functionTextName;
	}
	
	public void setFunctionArg(FunctionArg arg){
		if(args == null){
			args = new ArrayList<FunctionArg>();
		}
		args.add(arg);
	}
	
	public String getFunctionId(){
		return functionId;
	}
	
	public String getFunctionFullName() {
		return functionFullName;
	}

	public String getFunctionTextName(){
		return functionTextName;
	}
	
	public FunctionArg[] getArgs(){
		if(args == null || args.size() < 1){
			return null;
		}
		return args.toArray(new FunctionArg[args.size()]);
	}
	/**
	 * 函数参数 
	 */
	public static class FunctionArg implements Serializable{
		//字符串
		final public static int STRING = 1;
		//数字
		final public static int NUMBER = 2;
		//常量
		final public static int CONSTANT = 3;
		//数据集
		final public static int DATASET = 4;
		//变量
		final public static int VARIABLE = 5;
		//参数
		private String field;
		//参数名称
		private String fieldName;
		//参数类型
		private int type;
		
		public FunctionArg(String field, String fieldName, int argType){
			this.field = field;
			this.fieldName = fieldName;
			this.type = argType;
		}
		public String getfield(){
			return field;
		}
		public String getFieldName() {
			return fieldName;
		}
		public int getType(){
			return type;
		}
	}
}
