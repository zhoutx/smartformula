package com.epri.dlsc.sbs.parser;

/**
 * 常量表达式解析器
 * 
 * @author Zhong.Weijian
 *
 */
public final class ConstantParser {
	
	private final String[] BlackList = { "#dataset", "#formula", "#function" };
	
	private static ConstantParser constantParser;
	
	private ConstantParser() {
	};
	
	public synchronized static ConstantParser getInstance() {
		if (constantParser == null) {
			constantParser = new ConstantParser();
		}
		return constantParser;
	}
	
	public boolean matches(String exps) {
		if (exps == null)
			return false;

		for (int i = 0; i < BlackList.length; i++) {
			if (exps.contains(BlackList[i]))
				return false;
		}
		return true;
	}
 
}
