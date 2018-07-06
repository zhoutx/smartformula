package com.epri.dlsc.sbs.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.epri.dlsc.sbs.dataset.DataSetJSObject;
import com.epri.dlsc.sbs.util.CommonUtil;

public class DataSetJSObjectParser {
	private final static String REGE = "\\$\\(\"(.+?)\"\\)";
	private static Pattern PATTERN = Pattern.compile(REGE);
	private static DataSetJSObjectParser jsObjectParser;
	public synchronized static DataSetJSObjectParser getInstance() {
		if (jsObjectParser == null) {
			jsObjectParser = new DataSetJSObjectParser();
		}
		return jsObjectParser;
	}
	public List<DataSetJSObject> getAllList(String script){
		List<DataSetJSObject> list = null;
		Matcher matcher = PATTERN.matcher(script);
		while(matcher.find()){
			if(list == null){
				list = new ArrayList<DataSetJSObject>();
			}
			String expression = matcher.group();
			String dataSetID = matcher.group(1);
			if(dataSetID != null){
				dataSetID = "'" + CommonUtil.getMD5(dataSetID.trim()) + "'";
			}else{
				dataSetID = "''";
			}
			DataSetJSObject jsObject = new DataSetJSObject(expression, dataSetID);
			list.add(jsObject);
		}
		return list;
	}
}
