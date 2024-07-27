package de.bytestore.pvheating.handler;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HAHandler {
    private static final String BASE_URL = "http://homeassistant.local:8123/api/states/";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI0M2RiM2QyNmU2M2I0Y2IzYjIyOGZlYmZlMjg1MjExMyIsImlhdCI6MTcyMjA4MTE5MiwiZXhwIjoyMDM3NDQxMTkyfQ.ZyAlGHEE6GZ-cx8PKeqgYrYMOS2-kiPTb5jr-ZTRBJs";

    public static JsonObject getSensorState(String sensorId) {
        String response = "";
        try {
            URL url = new URL(BASE_URL + sensorId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                response = content.toString();
            } else {
                response = "Failed to retrieve sensor data: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ConfigHandler.getGson().fromJson(response, JsonObject.class);
    }
}
