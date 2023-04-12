package com.dori.Dori90v.logger;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dori.Dori90v.constants.ServerConstants.LOG_LVL;

public class Logger {
    // Fields -
    private final String className;
    private final String threadName;

    // Executioner (ThreadPool)
    private static final ExecutorService loggerExecutor = Executors.newCachedThreadPool();
    // Constructor -
    public Logger(Class<?> currentClass){
        this.className = currentClass.getName();
        this.threadName = Thread.currentThread().getName();
    }

    /**
     * These functions handle info log.
     * @param data - the base data to print.
     * @param moreData - in-case of additional data.
     */
    public void info(String data, Object... moreData){
        printMessage(data, LoggerLevels.INFO, moreData);
    }

    /**
     * These functions handle debug log.
     * @param data - the base data to print.
     * @param moreData - in-case of additional data.
     */
    public void debug(String data, Object... moreData){
        printMessage(data, LoggerLevels.DEBUG, moreData);
    }

    /**
     * These functions handle warning log.
     * @param data - the base data to print.
     * @param moreData - in-case of additional data.
     */
    public void warning(String data, Object... moreData){
        printMessage(data, LoggerLevels.WARNING, moreData);
    }

    /**
     * These functions handle error log.
     * @param data - the base data to print.
     * @param moreData - in-case of additional data.
     */
    public void error(String data, Object... moreData){
        printMessage(data, LoggerLevels.ERROR, moreData);
    }

    /**
     * These functions handle server notice log.
     * @param data - the base data to print.
     * @param moreData - in-case of additional data.
     */
    public void serverNotice(String data, Object... moreData){
        printMessage(data, LoggerLevels.SERVER_NOTICE, moreData);
    }

    /**
     * These functions handle notice log.
     * @param data - the base data to print.
     * @param moreData - in-case of additional data.
     */
    public void notice(String data, Object... moreData){
        printMessage(data, LoggerLevels.NOTICE, moreData);
    }

    /**
     * These functions handle receive packet log.
     */
    public void receive(String opCode, String hexOpCode, String opCodeName, String arrBytes){
        System.out.print(LogggerColor.ANSI_BLUE + "[RECEIVED] " + opCodeName + LogggerColor.ANSI_RESET + " <" + LogggerColor.ANSI_PURPLE +  opCode + "/" + hexOpCode + LogggerColor.ANSI_RESET + ">" + ": ");
        System.out.println(LogggerColor.ANSI_GREEN + arrBytes + LogggerColor.ANSI_RESET);
    }

    /**
     * These functions handle sent packet log.
     */
    public void sent(String opCode, String hexOpCode, String opCodeName, String arrBytes){
        System.out.print(LogggerColor.ANSI_YELLOW + "[SENT] " + opCodeName + LogggerColor.ANSI_RESET + " <" + LogggerColor.ANSI_PURPLE +  opCode + "/" + hexOpCode + LogggerColor.ANSI_RESET + ">" + ": ");
        System.out.println(LogggerColor.ANSI_GREEN + arrBytes + LogggerColor.ANSI_RESET);
    }

    /**
     * getting the matching logger color base on the logger level.
     * @param currLvl - the level we need the color for.
     * @return - return the relevant color.
     */
    private static LogggerColor getColorByLevel(LoggerLevels currLvl){
        LogggerColor retVal = LogggerColor.ANSI_RESET;
        switch (currLvl) {
            case INFO -> retVal = LogggerColor.ANSI_RESET;
            case DEBUG -> retVal =  LogggerColor.ANSI_GREEN;
            case WARNING -> retVal =  LogggerColor.ANSI_YELLOW;
            case ERROR -> retVal =  LogggerColor.ANSI_RED;
            case SERVER_NOTICE -> retVal =  LogggerColor.ANSI_CYAN;
            case NOTICE -> retVal =  LogggerColor.ANSI_PURPLE;
        }
        return retVal;
    }

    /**
     * This function will print in the console all the needing functions according to the decided format.
     * @param data - data to print.
     * @param currLvl - the logger level that need to print the message.
     * @param moreData - additional data to print.
     */
    private void printMessage(String data, LoggerLevels currLvl, Object... moreData){
        // Handle the className to get the correct one -
        String[] classNameList = this.className.split("\\.");
        String shortClassName = classNameList[classNameList.length - 1];
        // Thread and class String (short version) -
        String threadAndClassString = "<" + this.threadName + ">" + " " + "[" + shortClassName + "]" + ": ";
        // Thread and class String -
        String ClassString = "[" + shortClassName + "]" + ": ";
        // Check if defined log level allow to print that log -
        if(currLvl.getLvl() >= LOG_LVL) {
            // Invoke an event for the loggerExecutor -
            loggerExecutor.submit(() -> {
                // Print the base message -
                System.out.println(getColorByLevel(currLvl) + TimeFormatter.defaultTimeFormat() + threadAndClassString + data + LogggerColor.ANSI_RESET);
                // Print additional data -
                if (moreData.length != 0) {
                    for (Object currDataObj : moreData) {
                        System.out.println(getColorByLevel(currLvl) + currDataObj.toString() + LogggerColor.ANSI_RESET);
                    }
                }
            });
        }
    }
}
