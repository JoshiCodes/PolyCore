package de.joshicodes.polycore.util;

public class VersionUtil {

    public static final String VERSION = "1.0.0";

    public static UpdateInfo checkForUpdates() {
        return null;
    }

    public record UpdateInfo(String version, String downloadUrl) { }

}
