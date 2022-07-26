package com.codestian.mqttcraft.config;
import com.codestian.mqttcraft.MqttCraft;
import com.mojang.datafixers.util.Pair;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String BROKER;
    public static int PORT;
    public static String DISCOVERY_PREFIX;

    private static final String defaultBroker = "localhost";
    private static final int defaultPort = 1883;
    private static final String defaultDiscoveryPrefix = "minecraft";

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();
        CONFIG = SimpleConfig.of(MqttCraft.MOD_ID + "config").provider(configs).request();
        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("broker", defaultBroker), "Url of the broker");
        configs.addKeyValuePair(new Pair<>("port", defaultPort), "Port number of the broker");
        configs.addKeyValuePair(new Pair<>("discoveryPrefix", defaultDiscoveryPrefix), "Root prefix for all topics");
    }

    private static void assignConfigs() {
        BROKER = CONFIG.getOrDefault("broker", defaultBroker);
        PORT = CONFIG.getOrDefault("port", defaultPort);
        DISCOVERY_PREFIX = CONFIG.getOrDefault("discoveryPrefix", defaultDiscoveryPrefix);

        MqttCraft.LOGGER.info("All " + configs.getConfigsList().size() + " keys have been set properly!");
    }
}