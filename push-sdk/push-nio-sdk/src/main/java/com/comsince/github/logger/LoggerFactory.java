package com.comsince.github.logger;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-21 下午2:27
 **/
public class LoggerFactory {
    public static Log log;
    public static Log getLogger(Class loggerClass){
        if(log != null){
            return log;
        }
        return new JavaLogger(loggerClass);
    }

    public static void setLoggger(Log loggger){
        log = loggger;
    }
}
