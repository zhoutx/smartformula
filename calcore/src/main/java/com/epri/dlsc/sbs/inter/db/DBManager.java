package com.epri.dlsc.sbs.inter.db;

final public class DBManager {
	
	private DBManager(){}
	
	private static DBCollection collection;
	//设置数据库操作连接对象
	public static void registerDBCollection(DBCollection dbColl){
		collection = dbColl; 
	}
	public static DBCollection getDBCollection(){
		if(collection == null){
			throw new RuntimeException("数据库操作接口未实现");
		}
		return collection;
	}
}
