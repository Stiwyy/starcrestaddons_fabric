package me.stiwy.starcrestaddons.client.handlers;

import me.stiwy.starcrestaddons.client.config.Base64Config;
import me.stiwy.starcrestaddons.client.utils.Base64Decoder;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles chat messages for Base64 link decoding
 */
public class Base64ChatHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("starcrestaddons-base64");
    private static final Base64Config config = new Base64Config();
    private static final MinecraftClient client = MinecraftClient.getInstance();

    // Simplified pattern to catch more messages
    private static final Pattern GUILD_PATTERN = Pattern.compile(
            "Guild >.*?\\[Image\\]\\s+([A-Za-z0-9+/=]{20,})"
    );

    // More specific pattern for the exact format
    private static final Pattern GUILD_IMAGE_PATTERN = Pattern.compile(
            "§r§2Guild > (?:.*?\\s+)?(\\w+)(?:\\s+.*?)?:\\s+§r(\\w+)(?:" +
                    Pattern.quote(":") + ")?\\s+\\[Image\\]\\s+([A-Za-z0-9+/=]+)§r?"
    );

    public static void register() {
        LOGGER.info("Registering Base64 Chat Handler...");

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!config.isEnabled() || overlay) {
                return;
            }

            String messageText = message.getString();
            String rawMessage = message.toString(); // Get the raw message with formatting codes

            // Debug: Log all guild messages
            if (messageText.contains("Guild >")) {
                LOGGER.info("Guild message detected: {}", messageText);
                LOGGER.info("Raw message: {}", rawMessage);
            }

            // Check for [Image] in any guild message
            if (messageText.contains("Guild >") && messageText.contains("[Image]")) {
                LOGGER.info("Guild image message found: {}", messageText);

                // Try simplified pattern first
                Matcher simpleMatcher = GUILD_PATTERN.matcher(messageText);
                if (simpleMatcher.find()) {
                    String base64Content = simpleMatcher.group(1);
                    LOGGER.info("Found base64 content (simple): {}", base64Content);

                    processBase64Content("Unknown", base64Content);
                    return;
                }

                // Try detailed pattern
                Matcher matcher = GUILD_IMAGE_PATTERN.matcher(rawMessage);
                if (matcher.find()) {
                    String botName = matcher.group(1);
                    String playerName = matcher.group(2);
                    String base64Content = matcher.group(3);

                    LOGGER.info("Found detailed match - Bot: {}, Player: {}, Base64: {}",
                            botName, playerName, base64Content.substring(0, Math.min(20, base64Content.length())) + "...");

                    // Check if this message is from the configured bot
                    if (!config.getBotName().isEmpty() &&
                            !config.getBotName().equalsIgnoreCase(botName)) {
                        LOGGER.info("Bot name doesn't match config. Expected: {}, Got: {}", config.getBotName(), botName);
                        return;
                    }

                    processBase64Content(playerName, base64Content);
                } else {
                    LOGGER.warn("Guild image message found but pattern didn't match: {}", rawMessage);
                }
            }
        });

        LOGGER.info("Base64 Chat Handler registered successfully!");
    }

    private static void processBase64Content(String playerName, String base64Content) {
        try {
            // Validate base64
            if (!Base64Decoder.isBase64(base64Content)) {
                LOGGER.warn("Invalid base64 content: {}", base64Content.substring(0, Math.min(50, base64Content.length())));
                return;
            }

            // Decode the Base64 content
            String decodedLink = Base64Decoder.decode(base64Content);
            LOGGER.info("Decoded link: {}", decodedLink);

            if (!decodedLink.isEmpty()) {
                // Create the formatted message
                MutableText newMessage = createFormattedMessage(playerName, decodedLink);

                // Send the new message to chat
                if (client.player != null) {
                    client.player.sendMessage(newMessage, false);
                    LOGGER.info("Sent decoded message to chat");
                } else {
                    LOGGER.warn("Player is null, cannot send message");
                }
            } else {
                LOGGER.warn("Decoded link is empty");
            }
        } catch (Exception e) {
            LOGGER.error("Error processing base64 content", e);
        }
    }

    private static MutableText createFormattedMessage(String playerName, String decodedLink) {
        MutableText message = Text.literal("");

        // Add prefix to distinguish our messages
        message.append(Text.literal("[DECODED] ").formatted(Formatting.YELLOW, Formatting.BOLD));

        // Add guild prefix
        message.append(Text.literal("Guild > ").formatted(Formatting.DARK_GREEN));

        // Add bridge text with formatting
        String bridgeText = config.getPrefix();
        MutableText bridgePart = Text.literal(bridgeText + " ");
        if (config.isPrefixBold()) {
            bridgePart = bridgePart.formatted(Formatting.BOLD);
        }
        bridgePart = bridgePart.formatted(Formatting.GREEN);
        message.append(bridgePart);

        // Add player name with formatting
        MutableText playerPart = Text.literal(playerName);
        if (config.isUserBold()) {
            playerPart = playerPart.formatted(Formatting.BOLD);
        }
        playerPart = playerPart.formatted(Formatting.WHITE);
        message.append(playerPart);
        message.append(Text.literal(": "));

        // Add the decoded link
        message.append(Text.literal(decodedLink).formatted(Formatting.AQUA, Formatting.UNDERLINE));

        return message;
    }

    public static Base64Config getConfig() {
        return config;
    }
}