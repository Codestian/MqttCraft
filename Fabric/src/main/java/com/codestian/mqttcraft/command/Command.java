package com.codestian.mqttcraft.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.codestian.mqttcraft.MqttCraft.player;
import static com.codestian.mqttcraft.MqttCraft.CONN_STATUS;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Command {

    private static final LiteralArgumentBuilder<FabricClientCommandSource> commandPrefix = literal("mqtt");

    //  Setup all commands.
    public static void setupCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(commandInfo());
            dispatcher.register(commandStatus());
        });
    }

    // Command to show the mod version and author.
    private static LiteralArgumentBuilder<FabricClientCommandSource> commandInfo() {
        return commandPrefix.then(literal("info")
                .executes(context -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.of("MqttCraft 1.0.0 by Codestian"));
                    return 1;
                })
        );
    }

    // Command to show the current status of the MQTT connection.
    private static LiteralArgumentBuilder<FabricClientCommandSource> commandStatus() {
        return commandPrefix.then(literal("status")
                .executes(context -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.of(CONN_STATUS));
                    return 1;
                })
        );
    }

}
