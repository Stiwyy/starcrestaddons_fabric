package me.stiwy.starcrestaddons.client.mixins;

import me.stiwy.starcrestaddons.client.gui.ImageOverlay;
import me.stiwy.starcrestaddons.client.handlers.ChatLinkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.mouse != null) {
            double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();

            // Handle hover detection
            ChatLinkHandler.handleHover((int) mouseX, (int) mouseY);

            // Render image overlay
            ImageOverlay.render(context, (int) mouseX, (int) mouseY);
        }
    }
}