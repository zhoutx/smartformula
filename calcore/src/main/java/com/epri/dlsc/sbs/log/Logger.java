package com.epri.dlsc.sbs.log;

import org.apache.log4j.PropertyConfigurator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {
    private org.apache.log4j.Logger logger;
    private static Map<String, Logger> loggers;
    static {
        PropertyConfigurator.configure(Logger.class.getClassLoader().getResourceAsStream("log4j.properties"));
    }

    private Logger(Class clazz){
        this.logger = org.apache.log4j.Logger.getLogger(clazz);
    }

    public static Logger getLogger(Class clazz){
        if(loggers == null){
            loggers = new ConcurrentHashMap();
        }
        if(!loggers.containsKey(clazz.getName())){
            loggers.put(clazz.getName(), new Logger(clazz));
        }
        return loggers.get(clazz.getName());
    }

    public void debug(String message){
        this.logger.debug(message);
    }
    public void info(String message){
        this.logger.info(message);
    }
    public void warn(String message){
        this.logger.warn(message);
    }
    public void error(String message){
        this.logger.error(message);
    }
}
