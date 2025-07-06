package me.stiwy.starcrestaddons.client.gui;

import me.stiwy.starcrestaddons.client.image.ImageCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class ImageOverlay {
    private static ImageCache.CachedImage currentImage = null;
    private static String currentUrl = null;
    private static boolean isVisible = false;
    private static long showTime = 0;
    private static final int FADE_DURATION = 200; // milliseconds
    private static final int MAX_IMAGE_WIDTH = 400;
    private static final int MAX_IMAGE_HEIGHT = 300;
    private static final int BORDER_SIZE = 2;
    private static final int PADDING = 8;

    public static void showImage(String url) {
        if (url == null || url.equals(currentUrl)) return;

        currentUrl = url;
        isVisible = true;
        showTime = System.currentTimeMillis();

        // Process URL (convert imgur links)
        String processedUrl = ImageCache.processImgurUrl(url);

        // Load image asynchronously
        ImageCache.loadImage(processedUrl).thenAccept(image -> {
            currentImage = image;
        });
    }

    public static void hideImage() {
        isVisible = false;
        currentImage = null;
        currentUrl = null;
    }

    public static void render(DrawContext context, int mouseX, int mouseY) {
        if (!isVisible) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return; // Don't show in GUI screens

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Calculate fade
        long currentTime = System.currentTimeMillis();
        long timeSinceShow = currentTime - showTime;
        float alpha = 1.0f;

        if (timeSinceShow < FADE_DURATION) {
            alpha = (float) timeSinceShow / FADE_DURATION;
        }

        alpha = MathHelper.clamp(alpha, 0.0f, 1.0f);
        int alphaInt = (int) (255 * alpha);

        if (currentImage == null) {
            // Show loading indicator
            renderLoadingIndicator(context, screenWidth, screenHeight, alphaInt);
        } else if (currentImage.isLoaded) {
            // Show actual image
            renderImage(context, screenWidth, screenHeight, alphaInt, mouseX, mouseY);
        } else {
            // Show error message
            renderError(context, screenWidth, screenHeight, alphaInt);
        }
    }

    private static void renderLoadingIndicator(DrawContext context, int screenWidth, int screenHeight, int alpha) {
        int boxWidth = 200;
        int boxHeight = 50;
        int x = (screenWidth - boxWidth) / 2;
        int y = (screenHeight - boxHeight) / 2;

        // Background
        context.fill(x - PADDING, y - PADDING, x + boxWidth + PADDING, y + boxHeight + PADDING,
                (alpha << 24) | 0x000000);
        context.fill(x, y, x + boxWidth, y + boxHeight, (alpha << 24) | 0x333333);

        // Loading text
        Text loadingText = Text.literal("Loading image...").formatted(Formatting.WHITE);
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(loadingText);
        context.drawText(MinecraftClient.getInstance().textRenderer, loadingText,
                x + (boxWidth - textWidth) / 2, y + (boxHeight - 9) / 2,
                0xFFFFFF | (alpha << 24), false);
    }

    private static void renderError(DrawContext context, int screenWidth, int screenHeight, int alpha) {
        int boxWidth = 300;
        int boxHeight = 60;
        int x = (screenWidth - boxWidth) / 2;
        int y = (screenHeight - boxHeight) / 2;

        // Background
        context.fill(x - PADDING, y - PADDING, x + boxWidth + PADDING, y + boxHeight + PADDING,
                (alpha << 24) | 0x000000);
        context.fill(x, y, x + boxWidth, y + boxHeight, (alpha << 24) | 0x440000);

        // Error text
        Text errorText = Text.literal("Failed to load image").formatted(Formatting.RED);
        int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(errorText);
        context.drawText(MinecraftClient.getInstance().textRenderer, errorText,
                x + (boxWidth - textWidth) / 2, y + 10,
                0xFF4444 | (alpha << 24), false);

        if (currentImage.error != null) {
            Text detailText = Text.literal(currentImage.error).formatted(Formatting.GRAY);
            int detailWidth = MinecraftClient.getInstance().textRenderer.getWidth(detailText);
            context.drawText(MinecraftClient.getInstance().textRenderer, detailText,
                    x + (boxWidth - detailWidth) / 2, y + 25,
                    0x888888 | (alpha << 24), false);
        }
    }

    private static void renderImage(DrawContext context, int screenWidth, int screenHeight, int alpha, int mouseX, int mouseY) {
        // Calculate scaled dimensions
        float scale = Math.min(
                (float) MAX_IMAGE_WIDTH / currentImage.width,
                (float) MAX_IMAGE_HEIGHT / currentImage.height
        );
        scale = Math.min(scale, 1.0f); // Don't upscale

        int scaledWidth = (int) (currentImage.width * scale);
        int scaledHeight = (int) (currentImage.height * scale);

        // Position near mouse but keep on screen
        int x = mouseX + 15;
        int y = mouseY - scaledHeight - 15;

        // Keep on screen
        if (x + scaledWidth + PADDING * 2 > screenWidth) {
            x = mouseX - scaledWidth - 15 - PADDING * 2;
        }
        if (y - PADDING < 0) {
            y = mouseY + 15;
        }
        if (x < 0) x = 5;
        if (y + scaledHeight + PADDING * 2 > screenHeight) {
            y = screenHeight - scaledHeight - PADDING * 2 - 5;
        }

        // Background with border
        context.fill(x - PADDING - BORDER_SIZE, y - PADDING - BORDER_SIZE,
                x + scaledWidth + PADDING + BORDER_SIZE, y + scaledHeight + PADDING + BORDER_SIZE,
                (alpha << 24) | 0x000000);
        context.fill(x - PADDING, y - PADDING, x + scaledWidth + PADDING, y + scaledHeight + PADDING,
                (alpha << 24) | 0x222222);

        // Image
        context.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255.0f);
        context.drawTexture(currentImage.textureId, x, y, 0, 0, scaledWidth, scaledHeight,
                scaledWidth, scaledHeight);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Image info
        Text infoText = Text.literal(currentImage.width + "x" + currentImage.height)
                .formatted(Formatting.GRAY);
        context.drawText(MinecraftClient.getInstance().textRenderer, infoText,
                x, y + scaledHeight + PADDING / 2,
                0x888888 | (alpha << 24), false);
    }

    public static boolean isVisible() {
        return isVisible;
    }
}