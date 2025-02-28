package com.dimensionblocker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dimensionblocker_translations.json");

    public static JsonObject loadTranslations(MinecraftServer server){

        if (!CONFIG_PATH.toFile().exists()){
            createDefaultConfig(CONFIG_PATH.toFile());
            StateSaverAndLoader.resetPlayerState(server);
        }

        try(FileReader reader = new FileReader(CONFIG_PATH.toFile())){
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e){
            e.printStackTrace();
            return new JsonObject();
        }
    }

    private static void createDefaultConfig(File configFile){
        JsonObject defaultConfig = new JsonObject();
        JsonObject en = new JsonObject();

        en.addProperty("dimension_cancel","You can't teleport to this dimension.");
        en.addProperty("blockdimension_success","Dimension is now blocked!");
        en.addProperty("blockdimension_failure","Dimension is already blocked.");
        en.addProperty("unblockdimension_success","Dimension is no longer blocked!");
        en.addProperty("unblockdimension_failure","Dimension is not blocked.");
        en.addProperty("dimensionlanguage_success","DIMENSION language changed!");
        en.addProperty("dimensionlanguage_failure","Error : The language provided is invalid.");
        en.addProperty("version", "1");

        defaultConfig.add("en",en);

        try (FileWriter writer = new FileWriter(configFile)){
            GSON.toJson(defaultConfig,writer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
