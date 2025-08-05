package com.example.social_media_app.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
    
    /**
     * Formats time in Facebook-style relative format
     * @param dateTime the LocalDateTime to format
     * @return formatted time string (e.g., "3m", "5h", "2d", or full date for older posts)
     */
    public static String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        
        if (minutes < 1) {
            return "now";
        } else if (minutes < 60) {
            return minutes + "m";
        } else if (hours < 24) {
            return hours + "h";
        } else if (days < 7) {
            return days + "d";
        } else {
            // For posts older than a week, show the full date
            return dateTime.format(DISPLAY_FORMATTER);
        }
    }
    
    /**
     * Gets verbose time ago format for detailed displays
     * @param dateTime the LocalDateTime to format
     * @return formatted time string (e.g., "3 minutes ago", "5 hours ago", etc.)
     */
    public static String getVerboseTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (days < 7) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else {
            // For posts older than a week, show the full date
            return dateTime.format(DISPLAY_FORMATTER);
        }
    }
}
