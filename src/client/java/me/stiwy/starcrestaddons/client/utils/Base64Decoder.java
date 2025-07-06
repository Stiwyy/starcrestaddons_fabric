package me.stiwy.starcrestaddons.client.utils;
public class Base64Decoder {

    private static final String KEY_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    public static String decode(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        int chr1, chr2, chr3;
        int enc1, enc2, enc3, enc4;
        int i = 0;

        input = input.replaceAll("[^A-Za-z0-9+/=]", "");

        while (i < input.length()) {
            enc1 = KEY_STR.indexOf(input.charAt(i++));
            enc2 = i < input.length() ? KEY_STR.indexOf(input.charAt(i++)) : 64;
            enc3 = i < input.length() ? KEY_STR.indexOf(input.charAt(i++)) : 64;
            enc4 = i < input.length() ? KEY_STR.indexOf(input.charAt(i++)) : 64;

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

    public static boolean isBase64(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return input.matches("^[A-Za-z0-9+/]*={0,2}$") && input.length() % 4 == 0;
    }
}