package com.codestian.mqttcraft.component;

import com.codestian.mqttcraft.config.ModConfigs;
import com.codestian.mqttcraft.util.TriggerUpdate;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.world.LightType;

import static com.codestian.mqttcraft.MqttCraft.LOGGER;
import static com.codestian.mqttcraft.MqttCraft.player;

public class Trigger {

    private static final String topicPrefix = ModConfigs.DISCOVERY_PREFIX;

    public static void listenToClientEvents() {

        //  PLAYER
        TriggerUpdate playerUsernameUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/profile", "username", "uuid");

        TriggerUpdate playerHealthUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/health", "health");
        TriggerUpdate playerHungerUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/hunger", "hunger");
        TriggerUpdate playerArmorUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/armor", "armor");
        TriggerUpdate playerExperienceUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/experience", "experience");

        TriggerUpdate playerItemMainHandUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/inventory/main_hand", "mainHand");
        TriggerUpdate playerItemOffHandUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/inventory/off_hand", "offHand");

        TriggerUpdate playerIsSleepingUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/condition/is_sleeping", "isSleeping");
        TriggerUpdate playerIsOnFireUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/condition/is_on_fire", "isOnFire");

        TriggerUpdate playerBlockUnderUpdate = new TriggerUpdate(topicPrefix + "/trigger/player/other/block_under", "blockUnder");

        //  WORLD
        TriggerUpdate worldDimensionUpdate = new TriggerUpdate(topicPrefix + "/trigger/world/environment/dimension", "dimension");
        TriggerUpdate worldLightLevelBlockUpdate = new TriggerUpdate(topicPrefix + "/trigger/world/environment/light_level_block", "lightLevel");
        TriggerUpdate worldLightLevelSkyUpdate = new TriggerUpdate(topicPrefix + "/trigger/world/environment/light_level_sky", "lightLevel");

        TriggerUpdate worldBiomeUpdate = new TriggerUpdate(topicPrefix + "/trigger/world/biome", "name", "type");

        //  Listen to any events occurring on the client side.
        ClientTickEvents.START_WORLD_TICK.register((world) -> {

            //  Get client player entity.
            player = MinecraftClient.getInstance().player;

            //  Get player username and UUID info.
            String playerUsername = player.getGameProfile().getName();
            String playerUUID = player.getUuidAsString();

            //  Get player data related to HUD.
            float playerHealth = player.getHealth();
            int playerHunger = player.getHungerManager().getFoodLevel();
            int playerArmor = player.getArmor();
            int playerExperience = player.experienceLevel;

            //  Get the name of items player is holding on both main and off hands.
            String playerItemMainHand = player.getStackInHand(Hand.MAIN_HAND).getItem().toString();
            String playerItemOffHand = player.getStackInHand(Hand.OFF_HAND).getItem().toString();

            //  Booleans that check certain player conditions.
            boolean playerIsSleeping = player.isSleeping();
            boolean playerIsOnFire = player.isOnFire();

            //  Get the name of the block below the player, even if flying/swimming.
            String playerBlockUnder = world.getBlockState(player.getBlockPos().down()).getRegistryEntry().getKey().get().getValue().toString();
            playerHealthUpdate.update(Float.toString(playerHealth));
            playerHungerUpdate.update(Integer.toString(playerHunger));
            playerArmorUpdate.update(Integer.toString(playerArmor));
            playerExperienceUpdate.update(Integer.toString(playerExperience));

            playerItemMainHandUpdate.update(playerItemMainHand);
            playerItemOffHandUpdate.update(playerItemOffHand);

            playerUsernameUpdate.update(playerUsername, playerUUID);

            playerIsSleepingUpdate.update(Boolean.toString(playerIsSleeping));
            playerIsOnFireUpdate.update(Boolean.toString(playerIsOnFire));

            playerBlockUnderUpdate.update(playerBlockUnder);

            //  Get current dimension player is in.
            String worldDimension = world.getRegistryKey().getValue().toString();

            //  Get the light level from player position based from emitting light sources.
            int worldLightLevelBlock = world.getLightLevel(LightType.BLOCK, player.getBlockPos());

            //  Get the light level from player position based from the sky
            int worldLightLevelSky = world.getLightLevel(LightType.SKY, player.getBlockPos());

            //  Get the biome name and type player is currently in.
            String worldBiome = world.getBiome(player.getBlockPos()).getKey().get().getValue().toString();

            worldDimensionUpdate.update(worldDimension);

            worldLightLevelBlockUpdate.update(Integer.toString(worldLightLevelBlock));
            worldLightLevelSkyUpdate.update(Integer.toString(worldLightLevelSky));

            worldBiomeUpdate.update(worldBiome);

        });
    }
}
