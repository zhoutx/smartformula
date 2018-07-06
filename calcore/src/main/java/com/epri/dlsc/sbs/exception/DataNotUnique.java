package com.epri.dlsc.sbs.exception;

/**
 * 数据不唯一异常
 */
public class DataNotUnique extends Exception{

    public DataNotUnique(){
        super();
    }
    public DataNotUnique(String msg){
        super(msg);
    }

}
