package me.stiwy.starcrestaddons.client.image;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageCache {
    private static final Logger LOGGER = LoggerFactory.getLogger("starcrestaddons-image");
    private static final ConcurrentHashMap<String, CachedImage> cache = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static int textureCounter = 0;

    public static class CachedImage {
        public final Identifier textureId;
        public final int width;
        public final int height;
        public final boolean isLoaded;
        public final String error;

        public CachedImage(Identifier textureId, int width, int height) {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
            this.isLoaded = true;
            this.error = null;
        }

        public CachedImage(String error) {
            this.textureId = null;
            this.width = 0;
            this.height = 0;
            this.isLoaded = false;
            this.error = error;
        }
    }

    public static CompletableFuture<CachedImage> loadImage(String url) {
        // Check if already cached
        CachedImage cached = cache.get(url);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.info("Loading image from URL: {}", url);

                // Download image
                URL imageUrl = new URL(url);
                try (InputStream inputStream = imageUrl.openStream()) {
                    NativeImage nativeImage = NativeImage.read(inputStream);

                    // Create texture
                    NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
                    Identifier textureId = new Identifier("starcrestaddons", "dynamic_image_" + (textureCounter++));

                    // Register texture with Minecraft's texture manager
                    net.minecraft.client.MinecraftClient.getInstance().getTextureManager()
                            .registerTexture(textureId, texture);

                    CachedImage result = new CachedImage(textureId, nativeImage.getWidth(), nativeImage.getHeight());
                    cache.put(url, result);

                    LOGGER.info("Successfully loaded image: {}x{}", result.width, result.height);
                    return result;
                }

            } catch (IOException e) {
                LOGGER.error("Failed to load image from URL: {}", url, e);
                CachedImage errorResult = new CachedImage("Failed to load image: " + e.getMessage());
                cache.put(url, errorResult);
                return errorResult;
            }
        }, executor);
    }

    public static void clearCache() {
        cache.clear();
    }

    public static boolean isImageUrl(String url) {
        if (url == null || url.isEmpty()) return false;

        String lowerUrl = url.toLowerCase();
        return (lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://")) &&
                (lowerUrl.contains("imgur.com") ||
                        lowerUrl.endsWith(".png") ||
                        lowerUrl.endsWith(".jpg") ||
                        lowerUrl.endsWith(".jpeg") ||
                        lowerUrl.endsWith(".gif") ||
                        lowerUrl.endsWith(".webp"));
    }

    public static String processImgurUrl(String url) {
        // Convert imgur URLs to direct image URLs
        if (url.contains("imgur.com") && !url.contains("/i.")) {
            // Convert imgur.com/abc123 to i.imgur.com/abc123.png
            String imageId = url.substring(url.lastIndexOf("/") + 1);
            if (!imageId.contains(".")) {
                return "https://i.imgur.com/" + imageId + ".png";
            }
        }
        return url;
    }
}