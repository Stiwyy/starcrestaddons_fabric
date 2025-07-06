package me.stiwy.starcrestaddons.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarcrestaddonsClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("starcrestaddons");
    @Override
    public void onInitializeClient() {
        LOGGER.info("Starcrestaddons Client Initialized");
    }
}
