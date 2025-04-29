package com.UnCypher.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class PasswordBreachChecker {

    public static boolean isBreached(String password) {
        try {
            String sha1 = sha1(password);
            String prefix = sha1.substring(0, 5);
            String suffix = sha1.substring(5).toUpperCase();

            URL url = new URL("https://api.pwnedpasswords.com/range/" + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith(suffix)) {
                    return true; // Breached
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace(); // Optional: log to Sentry
            return false; // If HIBP fails, we don’t block
        }
    }

    private static String sha1(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] result = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }
}
