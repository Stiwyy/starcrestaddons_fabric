package me.stiwy.starcrestaddons.client.config;

public class Base64Config {

    private boolean enabled = true;
    private String botName = "";
    private String separator = ":";
    private String prefix = "Discord";
    private boolean prefixBold = false;
    private boolean userBold = false;
    private int bridgeColor = 10;
    private int userColor = 15;

    private static final String[] COLOR_ARRAY = {
            "§4", "§c", "§6", "§e",
            "§2", "§a", "§b", "§3",
            "§1", "§9", "§d", "§5",
            "§f", "§7", "§8", "§0"
    };

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isPrefixBold() {
        return prefixBold;
    }

    public void setPrefixBold(boolean prefixBold) {
        this.prefixBold = prefixBold;
    }

    public boolean isUserBold() {
        return userBold;
    }

    public void setUserBold(boolean userBold) {
        this.userBold = userBold;
    }

    public int getBridgeColor() {
        return bridgeColor;
    }

    public void setBridgeColor(int bridgeColor) {
        this.bridgeColor = Math.max(0, Math.min(15, bridgeColor));
    }

    public int getUserColor() {
        return userColor;
    }

    public void setUserColor(int userColor) {
        this.userColor = Math.max(0, Math.min(15, userColor));
    }

    public String getBridgeColorCode() {
        return COLOR_ARRAY[bridgeColor];
    }

    public String getUserColorCode() {
        return COLOR_ARRAY[userColor];
    }
}