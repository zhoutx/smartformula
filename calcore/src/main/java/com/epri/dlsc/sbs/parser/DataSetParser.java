package com.epri.dlsc.sbs.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epri.dlsc.sbs.dataset.DataSetExpression;

/**
 * 数据集表达式解析器
 * 
 * @author Zhong.Weijian
 *
 */
public final class DataSetParser {

	private final static String REGEX = "#dataset\\s*\\[\\s*" +
			"value\\s*\\(\\s*(\\w+)\\.(\\w+)\\s*\\)(?:\\s*,\\s*" +
			"where\\s*\\(((?:\\s*\\w+\\s*,)*\\s*\\w+\\s*)\\s*\\)\\s*){0,1}(?:\\s*,\\s*" +
			"filter\\s*\\(((?:\\s*\\w+=[^,]+\\s*,)*\\s*\\w+=[^,]+\\s*)\\)\\s*){0,1}\\]";

	private final static Pattern PATTERN = Pattern.compile(REGEX);

	private static DataSetParser datasetParser;

	private DataSetParser() {
	}

	public synchronized static DataSetParser getInstance() {
		if (datasetParser == null) {
			datasetParser = new DataSetParser();
		}
		return datasetParser;
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

	public List<DataSetExpression> getAllList(String exps) {
		List<DataSetExpression> list = null;

		if (null == exps)
			return list;

		Matcher matcher = PATTERN.matcher(exps);

		if (matcher != null) {
			while (matcher.find()) {
				if (null == list) {
					list = new ArrayList<DataSetExpression>();
				}
				String grp0 = matcher.group();// 完整表达式
				String grp1 = matcher.group(1);// 数据集id
				String grp2 = matcher.group(2);// 数据项id
				String grp3 = matcher.group(3);// wheres
				String grp4 = matcher.group(4);// filters

				Set<String> wheres = null;
				Map<String, String> filters = null;

				if (grp3 != null
						&& !(grp3 = grp3.replaceAll("\\s", "")).equals("")) {
					wheres = new HashSet<String>(Arrays.asList(grp3.split(",")));
				}

				if (grp4 != null
						&& !(grp4 = grp4.replaceAll("\\s", "")).equals("")) {

					String[] arr = grp4.split(",");

					for (int i = 0, count = arr.length; i < count; i++) {

						if (filters == null)
							filters = new HashMap<String, String>();
						filters.put(arr[i].split("=")[0], arr[i].split("=")[1]);
					}
				}
				DataSetExpression datasetExps = new DataSetExpression(grp0, grp1, grp2,
						wheres, filters);
				list.add(datasetExps);
			}
		}
		return list;
	}

	public Set<DataSetExpression> getAllSet(String exps) {
		Set<DataSetExpression> set = null;

		if (null == exps)
			return set;

		Matcher matcher = PATTERN.matcher(exps);

		if (matcher != null) {
			while (matcher.find()) {
				if (null == set) {
					set = new HashSet<DataSetExpression>();
				}
				String grp0 = matcher.group();// 完整表达式
				String grp1 = matcher.group(1);// 数据集id
				String grp2 = matcher.group(2);// 数据项id
				String grp3 = matcher.group(3);// wheres
				String grp4 = matcher.group(4);// filters

				Set<String> wheres = null;
				Map<String, String> filters = null;

				if (grp3 != null
						&& !(grp3 = grp3.replaceAll("\\s", "")).equals("")) {
					wheres = new HashSet<String>(Arrays.asList(grp3.split(",")));
				}

				if (grp4 != null
						&& !(grp4 = grp4.replaceAll("\\s", "")).equals("")) {

					String[] arr = grp4.split(",");

					for (int i = 0, count = arr.length; i < count; i++) {

						if (filters == null)
							filters = new HashMap<String, String>();
						filters.put(arr[i].split("=")[0], arr[i].split("=")[1]);
					}
				}
				DataSetExpression datasetExps = new DataSetExpression(grp0, grp1, grp2, wheres, filters);
				set.add(datasetExps);
			}
		}
		return set;
	}

	public Set<String> getAllDatasetId(String exps) {
		Set<String> set = null;

		if (null == exps)
			return set;

		Matcher matcher = PATTERN.matcher(exps);

		if (matcher != null) {
			while (matcher.find()) {
				if (null == set) {
					set = new HashSet<String>();
				}
				String datasetId = matcher.group(1);// 数据集id
				set.add(datasetId);
			}
		}
		return set;
	}
}
