package com.epri.dlsc.sbs.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epri.dlsc.sbs.formula.FormulaExps;

/**
 * 公式表达式解析器
 * 
 * @author Zhong.Weijian
 *
 */
public final class FormulaParser {

	private final static String REGEX = "#formula\\[(\\w+)\\]";

	private static Pattern PATTERN = Pattern.compile(REGEX);

	private static FormulaParser formulaParser;

	private FormulaParser() {
	};

	public synchronized static FormulaParser getInstance() {
		if (formulaParser == null) {
			formulaParser = new FormulaParser();
		}
		return formulaParser;
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

	public List<FormulaExps> getAllList(String exps) {
		List<FormulaExps> list = null;

		if (null == exps)
			return list;
		
		Matcher matcher = PATTERN.matcher(exps);

		if (matcher != null) {
			while (matcher.find()) {
				if (null == list) {
					list = new ArrayList<FormulaExps>();
				}
				String grp0 = matcher.group();// 完整表达式
				String grp1 = matcher.group(1);// 公式id

				FormulaExps formulaExps = new FormulaExps(grp0, grp1);
				list.add(formulaExps);
			}
		}
		return list;
	}

	public Set<FormulaExps> getAllSet(String exps) {
		Set<FormulaExps> set = null;

		if (null == exps)
			return set;
		
		Matcher matcher = PATTERN.matcher(exps);

		Set<String> formulaIds = new HashSet<String>();

		if (matcher != null) {
			while (matcher.find()) {
				if (null == set) {
					set = new HashSet<FormulaExps>();
				}

				String grp0 = matcher.group();// 完整表达式
				String grp1 = matcher.group(1);// 公式id

				if (formulaIds.contains(grp1)) {
					continue;
				} else {
					formulaIds.add(grp1);
				}

				FormulaExps formulaExps = new FormulaExps(grp0, grp1);
				set.add(formulaExps);
			}
		}
		return set;
	}

	public Set<String> getAllFormulaId(String exps) {
		Set<String> set = null;

		if (null == exps)
			return set;
		
		Matcher matcher = PATTERN.matcher(exps);

		if (matcher != null) {
			while (matcher.find()) {
				if (null == set) {
					set = new HashSet<String>();
				}

				String formulaId = matcher.group(1);// 公式id

				set.add(formulaId);
			}
		}
		return set;
	}

}
