package com.epri.dlsc.sbs.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epri.dlsc.sbs.dataset.DataSetExpression;
import com.epri.dlsc.sbs.function.Function;
import com.epri.dlsc.sbs.function.FunctionExpression;
import com.epri.dlsc.sbs.util.CommonUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 函数表达式解析器
 * 
 * @author Zhong.Weijian
 *
 */
final public class FunctionParser {
	//  (?:[#\\w]*?\\[[^\\+\\-\\*\\/\\;\\!]+?\\]\\.*?)
	//  [^\\+\\-\\*\\/\\;\\!\\[\\]]+?
	//  #function\\s*\\[\\s*(\\w+)\\s*\\(\\s*((?:(?:[#\\w]*?\\[[^\\+\\-\\*\\/\\;\\!]+?\\]\\.*?)|[^\\+\\-\\*\\/\\;\\!\\[\\]]+?)+)\\s*\\)\\s*\\]
	
	private static String REGEX = "#function\\s*\\[\\s*(\\w+)\\s*\\(\\s*((?:(?:[#\\w]*?\\[[^\\+\\-\\*\\/\\;\\!]+?\\]\\.*?)|[^\\+\\-\\*\\/\\;\\!\\[\\]]+?)+)\\s*\\)\\s*\\]";

	private static Pattern PATTERN = Pattern.compile(REGEX);

	private static FunctionParser functionParser;

	private FunctionParser() {
	};

	public synchronized static FunctionParser getInstance() {
		if (functionParser == null) {
			functionParser = new FunctionParser();
		}
		return functionParser;
	}

	public boolean matches(String exps) {
		if (exps == null)
			return false;
		
		return exps.matches(REGEX);
	}

	public boolean contains(String exps) {
		if (exps == null)
			return false;
		
		Matcher matcher = PATTERN.matcher(exps);
		if (matcher != null) {
			if (matcher.find())
				return true;
			else
				return false;
		}
		return false;
	}

	public List<FunctionExpression> getAllList(String exps) {
		List<FunctionExpression> list = null;

		if (null == exps)
			return list;

		Matcher matcher = PATTERN.matcher(exps);

		if (matcher != null) {
			while (matcher.find()) {
				if (null == list) {
					list = new ArrayList<FunctionExpression>();
				}
				String expression = matcher.group();// 完整表达式
				String functionId = matcher.group(1);// 函数id
				String functionArgsList = matcher.group(2);// 函数参数
				
				FunctionExpression functionExpression = new FunctionExpression();
				functionExpression.setExpression(expression);
				functionExpression.setFunctionId(functionId);

				Map<String, DataSetExpression> datasetExpsMap = new HashMap<String, DataSetExpression>();
				if (functionArgsList != null && !functionArgsList.equals("")) {
					functionArgsList = functionArgsList.replaceAll("\\s", "");
					if (BaseParser.dataset.contains(functionArgsList)) {
						List<DataSetExpression> datasetExpsList = BaseParser.dataset.getAllList(functionArgsList);
						for (int i = 0; i < datasetExpsList.size(); i++) {
							DataSetExpression datasetExps = datasetExpsList.get(i);
							String var = CommonUtil.toDatasetVar(i + 1);
							datasetExpsMap.put(var, datasetExps);
							functionArgsList = functionArgsList.replace(datasetExps.getExps(), var);
						}
					}
					
					String[] argArr = functionArgsList.split(",");
					if(argArr != null || argArr.length > 0){
						FunctionExpression.ArgumentValue[] argumentValues = new FunctionExpression.ArgumentValue[argArr.length];
						for (int i = 0; i < argArr.length; i++) {
							String arg = argArr[i];
							if (datasetExpsMap.containsKey(arg)){//数据集类型
									FunctionExpression.ArgumentValue argValue = new FunctionExpression.ArgumentValue(Function.FunctionArg.DATASET, datasetExpsMap.get(arg));
									argumentValues[i] = argValue;
							} else {
								if (StringUtils.isNumeric(arg)) {// 数字类型
									FunctionExpression.ArgumentValue argValue = new FunctionExpression.ArgumentValue(Function.FunctionArg.NUMBER, Double.valueOf(arg));
									argumentValues[i] = argValue;
								} else if (arg.startsWith("\"") && arg.endsWith("\"")) {// 字符串类型
									FunctionExpression.ArgumentValue argValue = new FunctionExpression.ArgumentValue(Function.FunctionArg.STRING, arg.replace("\"", ""));
									argumentValues[i] = argValue;
								} else {// 变量
									FunctionExpression.ArgumentValue argValue = new FunctionExpression.ArgumentValue(Function.FunctionArg.VARIABLE, arg);
									argumentValues[i] = argValue;
								}
							}
						}
						functionExpression.setFunctionArgs(argumentValues);
					}
				}
				list.add(functionExpression);
			}
		}
		return list;
	}
}
