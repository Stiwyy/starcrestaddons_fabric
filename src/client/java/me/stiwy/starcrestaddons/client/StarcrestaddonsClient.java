package me.stiwy.starcrestaddons.client;

import me.stiwy.starcrestaddons.client.config.ModConfig;
import me.stiwy.starcrestaddons.client.handler.Base64ChatHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarcrestaddonsClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("starcrestaddons");
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Starcrestaddons Client Initialized");
        
        // Load configuration
        ModConfig.load();
        
        // Register handlers
        Base64ChatHandler.register();
        
        LOGGER.info("Base64 link decoder feature enabled: {}", ModConfig.isBase64Enabled());
    }
}
