package me.stiwy.starcrestaddons.client;

import me.stiwy.starcrestaddons.client.commands.TestBase64Command;
import me.stiwy.starcrestaddons.client.handlers.Base64ChatHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarcrestaddonsClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("starcrestaddons");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starcrestaddons Client Initialized");

        // Register Base64 chat handler
        Base64ChatHandler.register();
        LOGGER.info("Base64 Chat Handler registered");

        // Register test commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            TestBase64Command.register(dispatcher);
        });
        LOGGER.info("Test commands registered");
    }
}