package com.project.twitter.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneralDateTimeUtil {
    private final static String TIME_FORMAT = "HH:mm:ss";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static String formatTime(LocalDateTime time) {
        return time.format(formatter);
    }
}