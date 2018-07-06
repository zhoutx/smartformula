package com.epri.dlsc.sbs.util;

public class OracleException {

	public static boolean exceptionCheck(Exception e,String key){
		boolean isTrue = javaException4Oraclecheck(e, key);
		if(isTrue){
			return isTrue;
		}else{
			return stackTraceCause(e, key);
		}
	}
	
	private static boolean javaException4Oraclecheck(Exception ex, String key)
    {
        boolean result = false;
        String messageFirst = ex.toString();
        Throwable thr = ex.getCause();
        String caurse = "";
        if(thr != null)
            caurse = thr.toString();
        if(messageFirst.indexOf(key) > -1)
            result = true;
        else
        if(caurse.indexOf(key) > -1)
        {
            result = true;
        } else
        {
            StackTraceElement ase[] = ex.getStackTrace();
            if(ase != null && ase.length > 0)
            {
                int i = 0;
                do
                {
                    if(i >= ase.length)
                        break;
                    StackTraceElement se = ase[i];
                    String message = se.toString();
                    if(message.indexOf(key) > -1)
                    {
                        result = true;
                        break;
                    }
                    i++;
                } while(true);
            }
        }
        return result;
    }
    
	private static boolean stackTraceCause(Throwable throwable,String key){
		Throwable throwa = throwable.getCause();
		boolean returnVlaue = false;
		if(throwa != null){
			if(throwa.toString().indexOf(key)>-1){
				return true;
			}else{
				returnVlaue = stackTraceCause(throwa,key);
			}
		}else{
			return false;
		}
		return returnVlaue;
	}
}
