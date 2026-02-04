package de.joshicodes.polycore.util;

public class ChatColor {

    public static final ChatColor RESET = new ChatColor("\u001B[0m", "\\u001B[0m");

    public static final ChatColor BLACK = new ChatColor("\u001B[30m", "\u001B[40m");
    public static final ChatColor DARK_BLUE = new ChatColor("\u001B[34m", "\u001B[44m");
    public static final ChatColor DARK_GREEN = new ChatColor("\u001B[32m", "\u001B[42m");
    public static final ChatColor DARK_AQUA = new ChatColor("\u001B[36m", "\u001B[46m");
    public static final ChatColor DARK_RED = new ChatColor("\u001B[31m", "\u001B[41m");
    public static final ChatColor DARK_PURPLE = new ChatColor("\u001B[35m", "\u001B[45m");
    public static final ChatColor GOLD = new ChatColor("\u001B[33m", "\u001B[43m");
    public static final ChatColor GRAY = new ChatColor("\u001B[37m", "\u001B[47m");
    public static final ChatColor DARK_GRAY = new ChatColor("\u001B[90m", "\u001B[100m");
    public static final ChatColor BLUE = new ChatColor("\u001B[94m", "\u001B[104m");
    public static final ChatColor GREEN = new ChatColor("\u001B[92m", "\u001B[102m");
    public static final ChatColor AQUA = new ChatColor("\u001B[96m", "\u001B[106m");
    public static final ChatColor RED = new ChatColor("\u001B[91m", "\u001B[101m");
    public static final ChatColor LIGHT_PURPLE = new ChatColor("\u001B[95m", "\u001B[105m");
    public static final ChatColor YELLOW = new ChatColor("\u001B[93m", "\u001B[103m");
    public static final ChatColor WHITE = new ChatColor("\u001B[97m", "\u001B[107m");
    public static final ChatColor PURPLE = new ChatColor("\u001B[35m", "\u001B[45m");
    public static final ChatColor BOLD = new ChatColor("\u001B[1m", "\u001B[22m");
    public static final ChatColor STRIKETHROUGH = new ChatColor("\u001B[9m", "\u001B[29m");
    public static final ChatColor UNDERLINE = new ChatColor("\u001B[4m", "\u001B[24m");
    public static final ChatColor ITALIC = new ChatColor("\u001B[3m", "\u001B[23m");

    private final String ansiColor;
    private final String backgroundAnsiColor;

    public ChatColor(final String ansiColor, final String backgroundAnsiColor) {
        this.ansiColor = ansiColor;
        this.backgroundAnsiColor = backgroundAnsiColor;
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public String asBackground() {
        return backgroundAnsiColor;
    }

    @Override
    public String toString() {
        return ansiColor;
    }

    public ChatColor bold() {
        return new ChatColor(ansiColor + BOLD.ansiColor, backgroundAnsiColor + BOLD.backgroundAnsiColor);
    }

}
