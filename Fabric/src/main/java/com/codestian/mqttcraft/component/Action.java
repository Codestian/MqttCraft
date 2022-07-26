package com.codestian.mqttcraft.component;

import com.codestian.mqttcraft.MqttCraft;
import com.codestian.mqttcraft.config.ModConfigs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import net.minecraft.client.MinecraftClient;

import java.nio.charset.StandardCharsets;

import static com.codestian.mqttcraft.MqttCraft.gson;
import static com.codestian.mqttcraft.MqttCraft.player;
import static com.codestian.mqttcraft.MqttCraft.LOGGER;

public class Action {

    private static final String topicPrefix = ModConfigs.DISCOVERY_PREFIX;

    //  Error message template.
    private static void sendErrorMessage(String topic, String errorMessage) {
        JsonObject errorObj = new JsonObject();
        errorObj.add("topic", new JsonPrimitive(topic));
        errorObj.add("error", new JsonPrimitive(errorMessage));
        MqttCraft.publishMessage(topicPrefix + "/error" + topic, gson.toJson(errorObj));
    }

    //  Subscribes to the chat topic and sends chat messages or commands in game.
    public static void subscribeToChat() {
        MqttCraft.subscribeTopic(topicPrefix + "/action/chat", (Mqtt3Publish publish) -> {

            String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

                //  Messages sent to this topic requires the keys `message` and `isCommand`.
                if (jsonObject.has("message") && jsonObject.has("isCommand")) {

                    String text = jsonObject.get("message").getAsString();
                    boolean isCommand = jsonObject.get("isCommand").getAsBoolean();

                    if (isCommand) {
                        //  Send a text as a command through the player without leading slash, similar to command blocks.
                        player.sendCommand(text);
                    } else {
                        //  Send a text as a chat message through the player.
                        player.sendChatMessage(text);
                    }
                } else {
                    String errorMessage = "Keys 'message' and 'isCommand' required";
                    LOGGER.warn(errorMessage);
                    sendErrorMessage("/action/chat", errorMessage);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
                sendErrorMessage("/action/chat", e.getMessage());
            }
        });
    }

    //  Subscribes to option topic and sets client side visual settings.
    public static void subscribeToOptions() {

        MqttCraft.subscribeTopic(topicPrefix + "/action/option/fov", (Mqtt3Publish publish) -> {

            String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

                if (jsonObject.has("fov")) {
                    //  Sets the fov to the specified number. If outside range, nothing happens.
                    Integer fov = jsonObject.get("fov").getAsInt();
                    MinecraftClient.getInstance().options.getFov().setValue(fov);
                } else {
                    String errorMessage = "Keys 'fov' required";
                    LOGGER.warn(errorMessage);
                    sendErrorMessage("/action/option/fov", errorMessage);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
                sendErrorMessage("/action/option/fov", e.getMessage());
            }
        });

        MqttCraft.subscribeTopic(topicPrefix + "/action/option/brightness", (Mqtt3Publish publish) -> {

            String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

                if (jsonObject.has("brightness")) {
                    //  Sets the fov to the specified number. If outside range, nothing happens.
                    Double brightness = jsonObject.get("brightness").getAsDouble();
                    MinecraftClient.getInstance().options.getGamma().setValue(brightness);
                } else {
                    String errorMessage = "Keys 'brightness' required";
                    LOGGER.warn(errorMessage);
                    sendErrorMessage("/action/option/fov", errorMessage);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
                sendErrorMessage("/action/option/brightness", e.getMessage());
            }
        });

    }
}
