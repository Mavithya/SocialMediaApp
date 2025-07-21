package com.example.social_media_app.util;

public class NumberFormatUtil {
    
    public static String formatCount(long count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 1000000) {
            double k = count / 1000.0;
            if (k == (long) k) {
                return String.format("%.0fK", k);
            } else {
                return String.format("%.1fK", k);
            }
        } else {
            double m = count / 1000000.0;
            if (m == (long) m) {
                return String.format("%.0fM", m);
            } else {
                return String.format("%.1fM", m);
            }
        }
    }
}
