package de.bytestore.pvheating.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HAHandler {
    private static final Logger log1 = LoggerFactory.getLogger(HAHandler.class);
    private static final Logger log = LoggerFactory.getLogger(HAHandler.class);
    private static final String BASE_URL = "http://homeassistant.local:8123/api/states";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJkZGI0NWU2NDVhNWM0NzQ1YjM4YTEzNWQxYWRkYTdjYiIsImlhdCI6MTcyMzAyMjY3NywiZXhwIjoyMDM4MzgyNjc3fQ.6JhL3JA5pzS1qIycqUqbxqzJuNXujVdMaUW3wwthW8s";

    /**
     * Retrieves the state of a sensor identified by the given sensorId.
     *
     * @param sensorId the identifier of the sensor
     * @return the state of the sensor as a JsonObject
     */
    public static JsonObject getSensorState(String sensorId) {
        String response = "";
        try {
            URL url = new URL(BASE_URL + "/" + sensorId);
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

    /**
     * Publishes the value of a sensor identified by the given entityIO and valueIO.
     * The method sends a HTTP POST request to the BASE_URL with the sensor value as a JSON payload.
     * The request is authenticated using the provided TOKEN.
     *
     * @param entityIO the entity identifier of the sensor
     * @param valueIO  the value of the sensor
     */
    public static void publishSensorValue(String entityIO, double valueIO, String prefixIO) {
        try {
            URL url = new URL(BASE_URL + "/" + entityIO);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{"
                    + "\"state\": \"" + valueIO + "\","
                    + "\"attributes\": {"
                    + "\"unit_of_measurement\": \""+ prefixIO +"\","
                    + "\"friendly_name\": \"My Sensor\""
                    + "}"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            log.debug("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                log.debug("Sensor value updated successfully.");
            } else {
                InputStream errorStream = conn.getErrorStream();
                Scanner s = new Scanner(errorStream).useDelimiter("\\A");
                String errorResponse = s.hasNext() ? s.next() : "";

                log.error("Failed to update sensor value. Response Code: " + responseCode);
                log.error("Error Response: " + errorResponse);
            }

        } catch (Exception e) {
            log.error("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of all sensors from the Home Assistant API.
     *
     * @return a list of all sensors as a List of Strings
     * @throws Exception if there is an error retrieving the sensor data or parsing the response
     */
    public static List<String> getAllSensors()  {
        List<String> sensors = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonArray jsonArray = ConfigHandler.getGson().fromJson(response.toString(), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject entity = jsonArray.get(i).getAsJsonObject();

                    String entityId = entity.get("entity_id").getAsString();

                    if (entityId.startsWith("sensor.")) {
                        sensors.add(entityId);
                    }
                }
            } else {
                log.error("Failed : HTTP error code : " + responseCode);
            }
        } catch (Exception exceptionIO) {
            log.error("Unable to query sensors.", exceptionIO);
        }

        return sensors;
    }
//    public static List<JsonObject> getAllSensors()  {
//        List<JsonObject> sensors = new ArrayList<>();
//
//        try {
//            URL url = new URL(BASE_URL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                JsonArray jsonArray = ConfigHandler.getGson().fromJson(response.toString(), JsonArray.class);
//                for (int i = 0; i < jsonArray.size(); i++) {
//                    JsonObject entity = jsonArray.get(i).getAsJsonObject();
//
//                    String entityId = entity.get("entity_id").getAsString();
//
//                    if (entityId.startsWith("sensor.")) {
//                        sensors.add(entity);
//                    }
//                }
//            } else {
//                log.error("Failed : HTTP error code : " + responseCode);
//            }
//        } catch (Exception exceptionIO) {
//            log.error("Unable to query sensors.", exceptionIO);
//        }
//
//        return sensors;
//    }
}
