package me.stiwy.starcrestaddons.client.handlers;

import me.stiwy.starcrestaddons.client.gui.ImageOverlay;
import me.stiwy.starcrestaddons.client.image.ImageCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ChatLinkHandler {

    public static void handleHover(int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Only work in chat or when no screen is open
        if (client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)) {
            ImageOverlay.hideImage();
            return;
        }

        // Get the text component under the mouse
        if (client.currentScreen instanceof ChatScreen chatScreen) {
            Style style = chatScreen.getTextStyleAt(mouseX, mouseY);
            if (style != null) {
                handleStyleHover(style);
                return;
            }
        }

        // If we're not hovering over anything, hide the overlay
        ImageOverlay.hideImage();
    }

    private static void handleStyleHover(Style style) {
        // Check click event for URLs
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            String url = clickEvent.getValue();
            if (ImageCache.isImageUrl(url)) {
                ImageOverlay.showImage(url);
                return;
            }
        }

        // Check hover event for text content
        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            Text hoverText = (Text) hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
            if (hoverText != null) {
                String textContent = hoverText.getString();
                if (ImageCache.isImageUrl(textContent)) {
                    ImageOverlay.showImage(textContent);
                    return;
                }
            }
        }

        ImageOverlay.hideImage();
    }
}