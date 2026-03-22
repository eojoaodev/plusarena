package com.plusarena.utils;

public class ChatUtil {

    private ChatUtil() {}

    public static String color(String message) {
        if (message == null) return "";
        return message.replace("&", "§");
    }
}
