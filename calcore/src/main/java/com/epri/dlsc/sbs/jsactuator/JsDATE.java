package com.epri.dlsc.sbs.jsactuator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * 日期处理类
 */
public class JsDATE {
	final static String regex = new StringBuilder()
		.append("^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-")
		.append("(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|")
		.append("((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|")
		.append("((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))")
		.append("\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$").toString();
	//格式：yyyy-MM-dd HH:mm:ss
	final static Pattern pattern = Pattern.compile(regex);
	/**
	 * 增加num个月
	 */
	public String addMonths(String currentDate, int num){
		Calendar calendar = setCalendar(currentDate);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + num);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	/**
	 * 增加nun个日
	 * @param currentDate
	 * @param num
	 * @return
	 */
	public String addDays(String currentDate, int num){
		Calendar calendar = setCalendar(currentDate);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + num);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	/**
	 * 增加num个小时
	 * @param currentDate
	 * @param num
	 * @return
	 */
	public String addHours(String currentDate, int num){
		Calendar calendar = setCalendar(currentDate);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + num);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	/**
	 * 增加num分钟
	 * @param currentDate
	 * @param num
	 * @return
	 */
	public String addMinutes(String currentDate, int num){
		Calendar calendar = setCalendar(currentDate);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + num);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	/**
	 * 增加num秒
	 * @param currentDate
	 * @param num
	 * @return
	 */
	public String addSeconds(String currentDate, int num){
		Calendar calendar = setCalendar(currentDate);
		calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + num);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	
	private Calendar setCalendar(String currentDate){
		//不正确的日期格式
		if(!pattern.matcher(currentDate).matches()){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		
		String yyyy = currentDate.substring(0, 4);
		String MM = currentDate.substring(5, 7);
		String dd = currentDate.substring(8, 10);
		String HH = currentDate.substring(11, 13);
		String mm = currentDate.substring(14, 16);
		String ss = currentDate.substring(17, 19);
		
		if(MM.startsWith("0")){
			MM = MM.substring(1);
		}
		if(dd.startsWith("0")){
			dd = dd.substring(1);
		}
		if(HH.startsWith("0")){
			HH = HH.substring(1);
		}
		if(mm.startsWith("0")){
			mm = mm.substring(1);
		}
		if(ss.startsWith("0")){
			ss = ss.substring(1);
		}
		
		calendar.set(Integer.parseInt(yyyy), 
				Integer.parseInt(MM) - 1, 
				Integer.parseInt(dd), 
				Integer.parseInt(HH), 
				Integer.parseInt(mm), 
				Integer.parseInt(ss));
		return calendar;
	}
}
