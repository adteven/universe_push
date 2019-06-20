package com.comsince.github.logger;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-21 下午2:27
 **/
public class LoggerFactory {
    public static Class myLogClass;
    public static void initLoggerClass(Class logclass){
        myLogClass = logclass;
    }
    public static Log getLogger(Class loggerClass){
       Log log =  new JavaLogger();
       log.setTag(loggerClass);
       if(myLogClass != null){
           try {
               log = (Log) myLogClass.newInstance();
               log.setTag(loggerClass);
           } catch (InstantiationException e) {
               e.printStackTrace();
           } catch (IllegalAccessException e) {
               e.printStackTrace();
           }
       }
        return log;
    }
}
