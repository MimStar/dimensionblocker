package com.dimensionblocker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    public ConfigData config = new ConfigData();

    public HashMap<UUID,PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt){

        NbtCompound playersNbt = new NbtCompound();
        players.forEach(((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putString("language",playerData.toString());
            playersNbt.put(uuid.toString(),playerNbt);
        }));
        nbt.put("players", playersNbt);

        nbt.putString("config",config.toString());
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag){
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();
            playerData.setLanguage(playersNbt.getCompound(key).getString("language"));
            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        String configString = tag.getString("config");
        if (configString != null && !configString.isEmpty()){
            state.config.setDimensions(configString);
        }
        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player){
        StateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.getServer()));
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }

    public static void resetPlayerState(MinecraftServer server){
        StateSaverAndLoader serverState = getServerState(server);
        serverState.players.forEach(((uuid, playerData) -> playerData.setLanguage("en")));
        saveState(server);
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server){
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        StateSaverAndLoader state = persistentStateManager.getOrCreate(
                StateSaverAndLoader::createFromNbt,
                StateSaverAndLoader::new,
                Dimensionblocker.getModId()
        );

        state.markDirty();

        return state;
    }

    public static ConfigData getConfigState(MinecraftServer server){
        StateSaverAndLoader serverState = getServerState(Objects.requireNonNull(server));
        return serverState.config;
    }

    public static void saveState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        persistentStateManager.save();
    }
}
