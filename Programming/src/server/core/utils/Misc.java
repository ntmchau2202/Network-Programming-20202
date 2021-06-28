package server.core.utils;

import java.util.UUID;

public class Misc {
    public static String genShortUUID() {
        return String.valueOf(System.currentTimeMillis()).substring(8, 13) + UUID.randomUUID().toString().substring(1, 10);
    }

    public static String genOriginalUUID() {
        return UUID.randomUUID().toString();
    }

}
