package de.joshicodes.polycore.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class VersionUtil {

    private static final String GITHUB_OWNER = "JoshiCodes";
    private static final String GITHUB_REPO = "PolyCore";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";

    private static String version = null;

    public static String getVersion() {
        if(version == null) {
            version = loadVersion();
        }
        return version;
    }

    private static String loadVersion() {
        try (InputStream input = VersionUtil.class.getClassLoader().getResourceAsStream("version.properties")) {
            if(input == null) {
                return "unknown";
            }
            final Properties props = new Properties();
            props.load(input);
            final String ver = props.getProperty("version", "unknown");
            if(ver.contains("${") || ver.isBlank()) {
                return "dev";
            }
            return ver;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UpdateInfo checkForUpdates() {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()){
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GITHUB_API_URL))
                    .header("Accept", "application/json")
                    .header("User-Agent", "PolyCore-UpdateChecker")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            final String currentVersion = getVersion();

            if(response.statusCode() == 404) {
                return new UpdateInfo(currentVersion, null); // No releases found
            }

            if(response.statusCode() != 200) {
                System.err.println("Failed to check for updates: HTTP " + response.statusCode());
                return new UpdateInfo(null, null);
            }

            final Gson gson = new Gson();
            final JsonObject json = gson.fromJson(response.body(), JsonObject.class);

            String latestVersion = json.get("tag_name").getAsString();
            if(latestVersion.startsWith("v"))
                latestVersion = latestVersion.substring(1);

            // Skip update check for dev/unknown versions
            if (currentVersion.equals("dev") || currentVersion.equals("unknown")) {
                return new UpdateInfo(currentVersion, null);
            }

            if (isNewerVersion(latestVersion, currentVersion)) {
                String downloadUrl = json.get("html_url").getAsString();

                // Try to get the JAR download URL from assets
                if (json.has("assets") && json.get("assets").isJsonArray()) {
                    JsonArray assets = json.getAsJsonArray("assets");
                    for (int i = 0; i < assets.size(); i++) {
                        JsonObject asset = assets.get(i).getAsJsonObject();
                        String name = asset.get("name").getAsString();
                        if (name.endsWith(".jar")) {
                            downloadUrl = asset.get("browser_download_url").getAsString();
                            break;
                        }
                    }
                }

                return new UpdateInfo(latestVersion, downloadUrl);
            }
            return new UpdateInfo(currentVersion, null);

        } catch (IOException | InterruptedException e) {
            return new UpdateInfo(null, e.getMessage());
        }

    }

    private static boolean isNewerVersion(String newVersion, String currentVersion) {

        try {
            // Remove any non-numeric suffixes for comparison (e.g., "-SNAPSHOT", "-beta")
            String[] newParts = newVersion.split("[^0-9]+");
            String[] currentParts = currentVersion.split("[^0-9]+");

            int maxLength = Math.max(newParts.length, currentParts.length);

            for (int i = 0; i < maxLength; i++) {
                int newPart = i < newParts.length && !newParts[i].isEmpty()
                        ? Integer.parseInt(newParts[i]) : 0;
                int currentPart = i < currentParts.length && !currentParts[i].isEmpty()
                        ? Integer.parseInt(currentParts[i]) : 0;

                if (newPart > currentPart) {
                    return true;
                } else if (newPart < currentPart) {
                    return false;
                }
            }

            // Versions are equal
            // But if current is SNAPSHOT and new is not, new is newer
            return currentVersion.contains("SNAPSHOT") && !newVersion.contains("SNAPSHOT");
        } catch (NumberFormatException e) {
            // Fallback to string comparison
            return newVersion.compareTo(currentVersion) > 0;
        }

    }

    public record UpdateInfo(String version, String downloadUrl) { }

}
