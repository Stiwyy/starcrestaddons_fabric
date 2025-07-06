package me.stiwy.starcrestaddons.client.config;

/**
 * Configuration class for StarcrestAddons features.
 * Manages settings for base64 decoding and other features.
 */
public class ModConfig {
    
    // Base64 feature settings
    private static boolean base64Enabled = true;
    private static String botName = "MAChatbridge";
    private static String separator = " »";
    
    // Getters
    public static boolean isBase64Enabled() {
        return base64Enabled;
    }
    
    public static String getBotName() {
        return botName;
    }
    
    public static String getSeparator() {
        return separator;
    }
    
    // Setters
    public static void setBase64Enabled(boolean enabled) {
        base64Enabled = enabled;
    }
    
    public static void setBotName(String name) {
        botName = name;
    }
    
    public static void setSeparator(String sep) {
        separator = sep;
    }
    
    /**
     * Load configuration from file (placeholder for future implementation).
     */
    public static void load() {
        // TODO: Implement config file loading
    }
    
    /**
     * Save configuration to file (placeholder for future implementation).
     */
    public static void save() {
        // TODO: Implement config file saving
    }
}