package com.epri.dlsc.sbs.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epri.dlsc.sbs.dataset.Condition;

/**
 * sql表达式解析器
 * 
 * @author Zhong.Weijian
 * 
 */
public final class ConditionParser {

	private final static String REGEX = "\\$\\{\\s*(\\S+)\\s*\\}";

	private static Pattern PATTERN = Pattern.compile(REGEX);

	private static ConditionParser sqlCondParser;

	private ConditionParser() {
	};

	public synchronized static ConditionParser getInstance() {
		if (sqlCondParser == null) {
			sqlCondParser = new ConditionParser();
		}
		return sqlCondParser;
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

	public List<Condition> getDataSetSqlCondition(String exps) {
		List<Condition> list = null;
		if (null == exps){
			return list;
		}
		Matcher matcher = PATTERN.matcher(exps);
		if (matcher != null) {
			while (matcher.find()) {
				if (null == list) {
					list = new ArrayList<Condition>();
				}
				String grp0 = matcher.group();// 完整表达式
				String grp1 = matcher.group(1);// sql条件属性名
				Condition sqlCondExps = new Condition(grp1, grp0);
				list.add(sqlCondExps);
			}
		}
		return list;
	}
}
