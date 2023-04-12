package com.dori.Dori90v.logger;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    public static String defaultTimeFormat(){
        //Create formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm:ss ");
        //Local date instance
        ZonedDateTime currentTime = ZonedDateTime.now();
        //Get formatted String
        return formatter.format(currentTime);
    }
}
