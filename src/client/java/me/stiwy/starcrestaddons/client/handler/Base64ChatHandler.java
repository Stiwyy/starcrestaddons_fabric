package me.stiwy.starcrestaddons.client.handler;

import me.stiwy.starcrestaddons.client.config.ModConfig;
import me.stiwy.starcrestaddons.client.util.Base64Decoder;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles base64 decoding in chat messages.
 * Listens for guild messages containing base64 encoded links and replaces them with decoded versions.
 */
public class Base64ChatHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("starcrestaddons.base64");
    
    // Pattern to match guild messages with base64 encoded images
    // Original pattern from ChatTriggers: "&r&2Guild > ${*} ${bot} ${*}: &r${player}${separator} [Image] ${msg}&r"
    // This matches messages like: "§r§2Guild > [VIP] MAChatbridge [EQUITE]: §rPlayerName » [Image] base64content§r"
    private static final Pattern GUILD_BASE64_PATTERN = Pattern.compile(
        "§r§2Guild > .*?(\\w+).*?: §r(\\w+)([^\\[]*) \\[Image\\] (.+?)§r"
    );
    
    // Alternative pattern for different message formats
    private static final Pattern GUILD_BASE64_PATTERN_ALT = Pattern.compile(
        "§r§2Guild > (\\w+).*?: §r(\\w+)([^\\[]*) \\[Image\\] (.+?)§r"
    );
    
    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (!ModConfig.isBase64Enabled() || overlay) {
                return true; // Allow the message through
            }
            
            return !handleChatMessage(message); // Return false to cancel, true to allow
        });
        
        LOGGER.info("Base64 chat handler registered");
    }
    
    private static boolean handleChatMessage(Text message) {
        String messageText = message.getString();
        
        // Convert Minecraft formatting codes to section symbols for pattern matching
        String formattedText = messageText.replace("§", "§");
        
        LOGGER.debug("Checking message: {}", formattedText);
        
        Matcher matcher = GUILD_BASE64_PATTERN.matcher(formattedText);
        if (!matcher.find()) {
            // Try alternative pattern
            matcher = GUILD_BASE64_PATTERN_ALT.matcher(formattedText);
        }
        
        if (matcher.find()) {
            String bot = matcher.group(1);
            String player = matcher.group(2);
            String separator = matcher.group(3);
            String base64Message = matcher.group(4);
            
            LOGGER.debug("Found potential base64 message - Bot: {}, Player: {}, Message: {}", bot, player, base64Message);
            
            // Check if the bot name matches our configured bot
            if (!ModConfig.getBotName().toLowerCase().equals(bot.toLowerCase())) {
                LOGGER.debug("Bot name '{}' doesn't match configured '{}'", bot, ModConfig.getBotName());
                return false; // Don't cancel message, not our bot
            }
            
            LOGGER.debug("Processing base64 message from {}: {}", player, base64Message);
            
            // Decode the base64 content
            String decodedLink = Base64Decoder.decode(base64Message.trim());
            
            if (!decodedLink.isEmpty() && Base64Decoder.isBase64(base64Message.trim())) {
                // Create the replacement message with proper formatting
                MutableText replacementMessage = Text.literal("Guild > ")
                    .formatted(Formatting.DARK_GREEN)
                    .append(Text.literal("[VIP] MAChatbridge ")
                        .formatted(Formatting.GREEN))
                    .append(Text.literal("[EQUITE] ")
                        .formatted(Formatting.GOLD))
                    .append(Text.literal(player + " » " + decodedLink)
                        .formatted(Formatting.WHITE));
                
                // Send the new message to chat
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(replacementMessage, false);
                }
                
                LOGGER.info("Decoded base64 message for {}: {}", player, decodedLink);
                return true; // Cancel the original message
            } else {
                LOGGER.debug("Failed to decode or not valid base64: {}", base64Message);
            }
        }
        
        return false; // Don't cancel message
    }
}