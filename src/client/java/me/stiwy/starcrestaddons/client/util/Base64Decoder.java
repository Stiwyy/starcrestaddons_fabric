package me.stiwy.starcrestaddons.client.util;

/**
 * Utility class for base64 decoding functionality.
 * Ported from the original ChatTriggers base64.js implementation.
 */
public class Base64Decoder {
    
    private static final String KEY_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    
    /**
     * Decode a base64 encoded string.
     * This is a direct port of the dc() function from the original ChatTriggers implementation.
     * 
     * @param input Base64 encoded string
     * @return Decoded string
     */
    public static String decode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        StringBuilder output = new StringBuilder();
        int chr1, chr2, chr3;
        int enc1, enc2, enc3, enc4;
        int i = 0;
        
        // Remove any characters that are not base64 valid
        input = input.replaceAll("[^A-Za-z0-9+/=]", "");
        
        while (i < input.length()) {
            enc1 = KEY_STR.indexOf(input.charAt(i++));
            enc2 = i < input.length() ? KEY_STR.indexOf(input.charAt(i++)) : 0;
            enc3 = i < input.length() ? KEY_STR.indexOf(input.charAt(i++)) : 0;
            enc4 = i < input.length() ? KEY_STR.indexOf(input.charAt(i++)) : 0;
            
            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;
            
            output.append((char) chr1);
            
            if (enc3 != 64) {
                output.append((char) chr2);
            }
            if (enc4 != 64) {
                output.append((char) chr3);
            }
        }
        
        return output.toString();
    }
    
    /**
     * Check if a string appears to be base64 encoded.
     * 
     * @param input String to check
     * @return true if the string looks like base64
     */
    public static boolean isBase64(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        // Basic heuristic: must be mostly base64 characters
        String cleaned = input.replaceAll("[^A-Za-z0-9+/=]", "");
        return cleaned.length() > 10 && cleaned.length() >= input.length() * 0.8;
    }
}