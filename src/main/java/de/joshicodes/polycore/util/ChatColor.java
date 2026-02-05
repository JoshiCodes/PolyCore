package de.joshicodes.polycore.util;

public enum ChatColor {

    // Colors
    BLACK('0', "\u001B[30m", "#000000"),
    DARK_BLUE('1', "\u001B[34m", "#0000AA"),
    DARK_GREEN('2', "\u001B[32m", "#00AA00"),
    DARK_AQUA('3', "\u001B[36m", "#00AAAA"),
    DARK_RED('4', "\u001B[31m", "#AA0000"),
    DARK_PURPLE('5', "\u001B[35m", "#AA00AA"),
    GOLD('6', "\u001B[33m", "#FFAA00"),
    GRAY('7', "\u001B[37m", "#AAAAAA"),
    DARK_GRAY('8', "\u001B[90m", "#555555"),
    BLUE('9', "\u001B[94m", "#5555FF"),
    GREEN('a', "\u001B[92m", "#55FF55"),
    AQUA('b', "\u001B[96m", "#55FFFF"),
    RED('c', "\u001B[91m", "#FF5555"),
    LIGHT_PURPLE('d', "\u001B[95m", "#FF55FF"),
    YELLOW('e', "\u001B[93m", "#FFFF55"),
    WHITE('f', "\u001B[97m", "#FFFFFF"),

    // Formatting
    BOLD('l', "\u001B[1m", null, "font-weight:bold"),
    ITALIC('o', "\u001B[3m", null, "font-style:italic"),
    UNDERLINE('n', "\u001B[4m", null, "text-decoration:underline"),
    STRIKETHROUGH('m', "\u001B[9m", null, "text-decoration:line-through"),
    RESET('r', "\u001B[0m", null, null);

    public static final char COLOR_CHAR = '§';

    private final char code;
    private final String ansi;
    private final String hexColor;
    private final String cssStyle;

    ChatColor(char code, String ansi, String hexColor) {
        this(code, ansi, hexColor, hexColor != null ? "color:" + hexColor : null);
    }

    ChatColor(char code, String ansi, String hexColor, String cssStyle) {
        this.code = code;
        this.ansi = ansi;
        this.hexColor = hexColor;
        this.cssStyle = cssStyle;
    }

    /**
     * Returns the ANSI code for terminal output.
     */
    @Override
    public String toString() {
        return ansi;
    }

    /**
     * Returns the format code (e.g., "§c" for red).
     */
    public String getCode() {
        return String.valueOf(COLOR_CHAR) + code;
    }

    /**
     * Returns the hex color value (e.g., "#FF5555").
     */
    public String getHex() {
        return hexColor;
    }

    /**
     * Returns the CSS style string.
     */
    public String getCss() {
        return cssStyle;
    }

    /**
     * Wraps text with this color and appends RESET.
     */
    public String wrap(String text) {
        return this.ansi + text + RESET.ansi;
    }

    /**
     * Converts a message with format codes (§c, §l, etc.) to ANSI for terminal display.
     */
    public static String toAnsi(String message) {
        if (message == null) return null;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            if (c == COLOR_CHAR && i + 1 < message.length()) {
                char nextChar = Character.toLowerCase(message.charAt(i + 1));
                ChatColor color = getByCode(nextChar);
                if (color != null) {
                    result.append(color.ansi);
                    i++; // Skip the code character
                    continue;
                }
            }

            result.append(c);
        }

        // Always reset at the end
        result.append(RESET.ansi);
        return result.toString();
    }

    /**
     * Converts a message with format codes (§c, §l, etc.) to HTML for web client display.
     */
    public static String toHtml(String message) {
        if (message == null) return null;

        StringBuilder result = new StringBuilder();
        StringBuilder currentStyles = new StringBuilder();
        boolean inSpan = false;

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);

            if (c == COLOR_CHAR && i + 1 < message.length()) {
                char nextChar = Character.toLowerCase(message.charAt(i + 1));
                ChatColor color = getByCode(nextChar);

                if (color != null) {
                    // Close previous span if open
                    if (inSpan) {
                        result.append("</span>");
                        inSpan = false;
                    }

                    if (color == RESET) {
                        currentStyles.setLength(0); // Clear all styles
                    } else if (color.cssStyle != null) {
                        // For formatting codes, append to current styles
                        if (color.hexColor == null) {
                            if (!currentStyles.isEmpty()) {
                                currentStyles.append(";");
                            }
                            currentStyles.append(color.cssStyle);
                        } else {
                            // For color codes, reset and set new color
                            currentStyles.setLength(0);
                            currentStyles.append(color.cssStyle);
                        }
                    }

                    i++; // Skip the code character
                    continue;
                }
            }

            // Open span if we have styles and aren't in one
            if (!currentStyles.isEmpty() && !inSpan) {
                result.append("<span style=\"").append(currentStyles).append("\">");
                inSpan = true;
            }

            // Escape HTML characters
            switch (c) {
                case '<' -> result.append("&lt;");
                case '>' -> result.append("&gt;");
                case '&' -> result.append("&amp;");
                case '"' -> result.append("&quot;");
                default -> result.append(c);
            }
        }

        // Close any open span
        if (inSpan) {
            result.append("</span>");
        }

        return result.toString();
    }

    /**
     * Converts a message with ANSI codes to format codes (§).
     * Useful for standardizing input.
     */
    public static String fromAnsi(String message) {
        if (message == null) return null;

        String result = message;
        for (ChatColor color : values()) {
            result = result.replace(color.ansi, color.getCode());
        }
        return result;
    }

    /**
     * Strips all color and formatting codes from a message.
     */
    public static String strip(String message) {
        if (message == null) return null;

        // Strip format codes (§x)
        String result = message.replaceAll(COLOR_CHAR + "[0-9a-fA-Fk-oK-OrR]", "");
        // Strip ANSI codes
        result = result.replaceAll("\u001B\\[[;\\d]*m", "");
        return result;
    }

    /**
     * Gets a ChatColor by its code character.
     */
    public static ChatColor getByCode(char code) {
        for (ChatColor color : values()) {
            if (color.code == Character.toLowerCase(code)) {
                return color;
            }
        }
        return null;
    }

    /**
     * Translates alternate color codes (like &c) to standard format codes (§c).
     */
    public static String translateAlternateColorCodes(char altChar, String message) {
        if (message == null) return null;

        char[] chars = message.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == altChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
                chars[i] = COLOR_CHAR;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }
        return new String(chars);
    }
}
