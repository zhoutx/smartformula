package com.epri.dlsc.sbs.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 公共工具类
 * 
 * @author Zhong.Weijian
 *
 */
public class CommonUtil {

	public static String toCharVar(int i) {
		String s = "";
		while (i > 0) {
			int j = i % 26;
			if (j == 0)
				j = 26;
			s = (char) (j + 96) + s;
			i = (i - j) / 26;
		}
		return "$_pu_" + s;
	}

	public static String toFormulaVar(int i) {
		String s = "";
		while (i > 0) {
			int j = i % 26;
			if (j == 0)
				j = 26;
			s = (char) (j + 96) + s;
			i = (i - j) / 26;
		}
		return "$_fa_" + s;
	}

	public static String toDatasetVar(int i) {
		String s = "";
		while (i > 0) {
			int j = i % 26;
			if (j == 0)
				j = 26;
			s = (char) (j + 96) + s;
			i = (i - j) / 26;
		}
		return "$_da_" + s;
	}

	public static String toFunctionVar(int i) {
		String s = "";
		while (i > 0) {
			int j = i % 26;
			if (j == 0)
				j = 26;
			s = (char) (j + 96) + s;
			i = (i - j) / 26;
		}
		return "$_fn_" + s;
	}

	public static String toString(Set<String> set) {
		if (set == null || set.size() == 0)
			return "";
		String result = "";
		for (String str : set) {
			result += "," + str;
		}
		return result.substring(1);
	}

	public static String getItemsCondition50pg(String field, String[] arr) {
		StringBuilder condition = new StringBuilder();
		List<String> list = getItemsCondition50pg(arr);
		if (list.size() == 0)
			return "(1=2)";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0)
				condition.append("(");
			if (i > 0)
				condition.append(" OR ");
			condition.append(field).append(" IN (").append(list.get(i))
					.append(")");
			if (i == list.size() - 1)
				condition.append(")");
		}
		return condition.toString();
	}

	private static List<String> getItemsCondition50pg(String[] arr) {
		List<String> list = new ArrayList<String>();
		if (arr == null || arr.length == 0)
			return list;
		for (int i = 0; i < java.lang.Math.round(arr.length / 50 + 0.5); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = i * 50; j < (i + 1) * 50 && j < arr.length; j++) {
				sb.append("'" + arr[j] + "',");
			}
			String sbStr = sb.toString();
			if (sbStr.length() > 0) {
				sbStr = sbStr.substring(0, sbStr.length() - 1);
				list.add(sbStr);
			}
		}
		return list;
	}
	
	public static String getItemsCondition50pg(String field,Object[] arr){
		StringBuilder condition = new StringBuilder();
		List<String> list = getItemsCondition50pg(arr);
		if(list.size()==0) return "(1=2)";//没有符合的条件
		for(int i=0;i<list.size();i++){
			if(i==0) condition.append("(");
			if(i>0) condition.append(" OR ");
			condition.append(field).append(" IN (").append(list.get(i)).append(")");
			if(i==list.size()-1) condition.append(")");
		}
		return condition.toString();
	}
	
	public static List<String> getItemsCondition50pg(Object[] arr){
		List list = new ArrayList();
		if(arr==null ||arr.length==0) return list;
		for(int k=0;k<java.lang.Math.round(arr.length/50+0.5);k++){//or语句列表字符限制原因，每50条一个or语句//
			StringBuilder sb = new StringBuilder();
			for(int n=k*50;n<(k+1)*50&&n<arr.length;n++){
				sb.append("'"+arr[n]+"',");
			}
			String sbStr = sb.toString();
			if(sbStr.length()>0){
				sbStr = sbStr.substring(0,sbStr.length()-1);
				list.add(sbStr);
			}
		}
		return list;
	}
	//求两Set的交集
	public static <T> Set<T> intersect(Collection<T> collection1, Collection<T> collection2){
			Set<T> keySet1 = new HashSet<T>();
			T[] dest = (T[]) new Object[collection1.size()];
			System.arraycopy(collection1.toArray(), 0, dest, 0, collection1.size());
			Collections.addAll(keySet1, dest);
			keySet1.retainAll(collection2);
			return keySet1;
	}
	//求两Set的差集
	public static <T> Set<T> minus(Collection<T> collection1, Collection<T> collection2){
		Set<T> keySet1 = new HashSet<T>();
		T[] dest = (T[]) new Object[collection1.size()];
		System.arraycopy(collection1.toArray(), 0, dest, 0, collection1.size());
		Collections.addAll(keySet1, dest);
		keySet1.removeAll(collection2);
		return keySet1;
	}
	//MD5算法
	public static String getMD5(String str) {
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        md.update(str.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
	}
}
