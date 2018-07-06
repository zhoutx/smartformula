package com.epri.dlsc.sbs.dataset;

import java.util.Map;
/**
 * @author zhoutx
 * 数据集类型
 */
public class DataSetValue {
	//数据集数据
	private Map<String, Map<String, String>> dataSets;
	//数据条数
	private long size;
	//操作的数据集字段名
	//#dataset[上网电量数据集.上网电量],即“上网电量”为这里要操作的数据集字段名
	private String operateKey;
	public Map<String, Map<String, String>> getDataSets() {
		return dataSets;
	}
	public void setDataSets(Map<String, Map<String, String>> dataSets) {
		this.dataSets = dataSets;
	}
	public String getOperateKey() {
		return operateKey;
	}
	public void setOperateKey(String operateKey) {
		this.operateKey = operateKey;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
}
