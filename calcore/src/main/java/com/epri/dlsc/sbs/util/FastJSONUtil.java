package com.epri.dlsc.sbs.util;

import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * JSON序列化工具
 * 
 * @author Zhong.Weijian
 *
 */
public final class FastJSONUtil {
	
	/**
	 * 将对象序列化成json字符串
	 * @param object 对象
	 * @return
	 */
	public static final String toJSONString(Object object){
		return JSON.toJSONString(object);
	}
	
	/**
	 * 将json字符串反序列化成对象
	 * @param text json字符串
	 * @param clazz 类
	 * @return
	 */
	public static final <T> T parseObject(String text,Class<T> clazz){
		return JSON.parseObject(text,clazz);
	}
	
	/**
	 * 将json字符串反序列化为List
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static final <T> List<T>  parseArray(String text,Class<T> clazz){
		return JSON.parseArray(text, clazz);
	}
	
}
