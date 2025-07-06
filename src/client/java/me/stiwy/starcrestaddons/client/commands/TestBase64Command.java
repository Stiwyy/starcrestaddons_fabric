package me.stiwy.starcrestaddons.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.stiwy.starcrestaddons.client.utils.Base64Decoder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TestBase64Command {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("testbase64")
                .then(ClientCommandManager.argument("base64", StringArgumentType.greedyString())
                        .executes(context -> {
                            String base64Input = StringArgumentType.getString(context, "base64");

                            try {
                                String decoded = Base64Decoder.decode(base64Input);

                                context.getSource().sendFeedback(
                                        Text.literal("Input: ").formatted(Formatting.YELLOW)
                                                .append(Text.literal(base64Input).formatted(Formatting.WHITE))
                                );

                                context.getSource().sendFeedback(
                                        Text.literal("Decoded: ").formatted(Formatting.GREEN)
                                                .append(Text.literal(decoded).formatted(Formatting.AQUA))
                                );

                                context.getSource().sendFeedback(
                                        Text.literal("Is valid Base64: ").formatted(Formatting.YELLOW)
                                                .append(Text.literal(String.valueOf(Base64Decoder.isBase64(base64Input)))
                                                        .formatted(Base64Decoder.isBase64(base64Input) ? Formatting.GREEN : Formatting.RED))
                                );

                            } catch (Exception e) {
                                context.getSource().sendError(
                                        Text.literal("Error decoding: " + e.getMessage()).formatted(Formatting.RED)
                                );
                            }

                            return 1;
                        }))
                .executes(context -> {
                    context.getSource().sendFeedback(
                            Text.literal("Usage: /testbase64 <base64_string>").formatted(Formatting.YELLOW)
                    );
                    return 1;
                })
        );

        // Test command with a sample base64 link
        dispatcher.register(ClientCommandManager.literal("testbase64sample")
                .executes(context -> {
                    String sampleBase64 = "aHR0cHM6Ly9pLmltZ3VyLmNvbS90ZXN0LnBuZw==";

                    String decoded = Base64Decoder.decode(sampleBase64);

                    context.getSource().sendFeedback(
                            Text.literal("Sample Base64: ").formatted(Formatting.YELLOW)
                                    .append(Text.literal(sampleBase64).formatted(Formatting.WHITE))
                    );

                    context.getSource().sendFeedback(
                            Text.literal("Decoded: ").formatted(Formatting.GREEN)
                                    .append(Text.literal(decoded).formatted(Formatting.AQUA))
                    );

                    return 1;
                })
        );
    }
}