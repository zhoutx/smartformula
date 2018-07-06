package com.epri.dlsc.sbs.function;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class FunctionDefine implements Serializable{

	private Map<String, Function> functionMap;
	
	public boolean hasFunction(String functionID){
		if(functionMap == null){
			return false;
		}
		return functionMap.containsKey(functionID);
	}
	
	public void addFunction(Function function){
		if(functionMap == null){
			functionMap = new HashMap<String, Function>();
		}
		functionMap.put(function.getFunctionId(), function);
	}
	
	public Function getFunction(String functionID){
		if(functionMap == null){
			return null;
		}
		return functionMap.get(functionID);
	}

	public Collection<Function> getFunctions(){
	    if(functionMap == null){
	        return null;
        }
        return functionMap.values();
    }
}
