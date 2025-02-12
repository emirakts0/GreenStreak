package com.emir.gitautocommit.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class StrUtils {

    private static final List<String> MESSAGES = List.of(
            "Updated script", "Fixed minor bug", "Refactored code",
            "Improved performance", "Updated dependencies", "Enhanced documentation",
            "Bug fixes and improvements", "Small tweaks", "Code cleanup", "Test commit",
            "Refactored some logic", "Optimized code"
    );

    public static String getRandomCommitMessage() {
        Random random = new Random();
        String message = MESSAGES.get(random.nextInt(MESSAGES.size()));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return message + " (" + timestamp + ")";
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
