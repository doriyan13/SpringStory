package com.dori.SpringStory.logger;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dori.SpringStory.constants.ServerConstants.LOG_LVL;

public class Logger {
    // Fields -
    private final String className;

    // Executioner (ThreadPool)
    private static final ExecutorService loggerExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService worker = Executors.newVirtualThreadPerTaskExecutor();
    // Constructor -
    public Logger(Class<?> currentClass){
        this.className = currentClass.getName();
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

    public void startLoad(@NotNull String fileType,
                          @NotNull String dataToLoad){
        serverNotice(LogggerColor.ANSI_CYAN + "Start loading the "
                + LogggerColor.ANSI_YELLOW + fileType + "s" + LogggerColor.ANSI_CYAN
                + " for " + LogggerColor.ANSI_YELLOW + dataToLoad + LogggerColor.ANSI_CYAN + "..." + LogggerColor.ANSI_RESET);
    }

    public void finishLoad(int amountOfFiles,
                           @NotNull String fileType,
                           @NotNull String dataToLoad,
                           double amountOfSeconds){
        serverNotice(LogggerColor.ANSI_CYAN + "~ Finished loading "
                + LogggerColor.ANSI_PURPLE + amountOfFiles + LogggerColor.ANSI_RESET
                + " " + LogggerColor.ANSI_RED + dataToLoad + LogggerColor.ANSI_RESET
                + " " + LogggerColor.ANSI_YELLOW + fileType + "s" + LogggerColor.ANSI_CYAN
                + " files! in: " + LogggerColor.ANSI_YELLOW + amountOfSeconds + LogggerColor.ANSI_CYAN + " seconds" + LogggerColor.ANSI_RESET);
    }

    /**
     * getting the matching logger color base on the logger level.
     * @param currLvl - the level we need the color for.
     * @return - return the relevant color.
     */
    private static LogggerColor getColorByLevel(LoggerLevels currLvl){
        return switch (currLvl) {
            case DEBUG -> LogggerColor.ANSI_GREEN;
            case WARNING -> LogggerColor.ANSI_YELLOW;
            case ERROR -> LogggerColor.ANSI_RED;
            case SERVER_NOTICE -> LogggerColor.ANSI_CYAN;
            case NOTICE -> LogggerColor.ANSI_PURPLE;
            case null, default -> LogggerColor.ANSI_RESET;
        };
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
        String threadAndClassString = "<" + Thread.currentThread().getName() + ">" + " " + "[" + shortClassName + "]" + ": ";
        // Thread and class String -
        String ClassString = "[" + shortClassName + "]" + ": ";
        // Check if defined log level allow to print that log -
        if(currLvl.getLvl() >= LOG_LVL) {
            // Invoke an event for the loggerExecutor -
            loggerExecutor.submit(() ->
            worker.execute(() ->{
                // Print the base message -
                System.out.println(getColorByLevel(currLvl) + TimeFormatter.defaultTimeFormat() + threadAndClassString + data + LogggerColor.ANSI_RESET);
                // Print additional data -
                for (Object currDataObj : moreData) {
                    System.out.println(getColorByLevel(currLvl) + currDataObj.toString() + LogggerColor.ANSI_RESET);
                }
            }));
        }
    }
}
