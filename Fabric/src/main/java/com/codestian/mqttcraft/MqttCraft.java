package com.codestian.mqttcraft;

import com.codestian.mqttcraft.component.Action;
import com.codestian.mqttcraft.command.Command;
import com.codestian.mqttcraft.component.Trigger;
import com.codestian.mqttcraft.config.ModConfigs;
import com.google.gson.Gson;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MqttCraft implements ClientModInitializer {

    public static final String MOD_ID = "mqttcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Gson gson = new Gson();

    public static String CONN_STATUS = "MqttCraft not connected to broker.";
    public static ClientPlayerEntity player;

    private static Mqtt3AsyncClient client;

    @Override
    public void onInitializeClient() {

        LOGGER.info("Attempting to connect to MQTT broker...");

        //  Setup configuration from file.
        ModConfigs.registerConfigs();

        //  Setup MQTT client information.
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(UUID.randomUUID().toString())
                .serverHost(ModConfigs.BROKER)
                .serverPort(ModConfigs.PORT)
                .buildAsync();

        //  Attempt to connect to MQTT Broker.
        client.connect()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        String errorMessage = "Unable to connect to broker " + ModConfigs.BROKER + ":" + ModConfigs.PORT + ". Ensure the configuration is correct and broker is running.";
                        LOGGER.warn(errorMessage);
                        CONN_STATUS = errorMessage;
                    } else {
                        String successMessage = "Client is successfully connected to broker " + ModConfigs.BROKER + ":" + ModConfigs.PORT;
                        LOGGER.info(successMessage);
                        CONN_STATUS = successMessage;
                        Trigger.listenToClientEvents();
                        Action.subscribeToChat();
                        Action.subscribeToOptions();
                    }
                });
        Command.setupCommands();
    }

    //  Callback for subscribed topics.
    public interface Subscribe {
        void runAction(Mqtt3Publish publish);
    }

    //	Function to subscribe to specified MQTT topics and provide callback.
    public static void subscribeTopic(String topic, Subscribe subscribe) {
        client.subscribeWith()
                .topicFilter(topic)
                .callback(subscribe::runAction)
                .send();
    }

    //  Function to publish MQTT messages to specified topic with message.
    public static void publishMessage(String topic, String message) {
        client.publishWith()
                .topic(topic)
                .payload(message.getBytes())
                .qos(MqttQos.EXACTLY_ONCE)
                .send()
                .whenComplete((mqtt3Publish, throwable1) -> {
                    if (throwable1 != null) {
                        //  Handle failure to publish to topic.
                        LOGGER.warn("Unable to publish message on topic: " + topic);
                    }
                });
    }
}
