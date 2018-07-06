package com.epri.dlsc.sbs.parser;


/**
 * 解析器基类
 * 
 * @author Zhong.Weijian
 *
 */
public class BaseParser {
	/**
	 * 常量解析器
	 */
	public final static ConstantParser constant = ConstantParser.getInstance();
	/**
	 * 数据集解析器
	 */
	public final static DataSetParser dataset = DataSetParser.getInstance();
	/**
	 * 公式解析器
	 */
	public final static FormulaParser formula = FormulaParser.getInstance();
	/**
	 * 函数解析器
	 */
	public final static FunctionParser function = FunctionParser.getInstance();
	/**
	 * 条件解析器
	 */
	public final static ConditionParser condition = ConditionParser.getInstance();
	/**
	 * 数据集上下文解析器
	 */
	public final static  DataSetJSObjectParser dataSetJSObject =  DataSetJSObjectParser.getInstance();
}
