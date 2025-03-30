package com.dimensionblocker;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class Dimensionblocker implements ModInitializer {
	private static final String MOD_ID = "dimensionblocker";

	public static String getMOD_ID(){
		return MOD_ID;
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static JsonObject translations = new JsonObject();

	public static String getModId(){
		return MOD_ID;
	}

	public static Set<String> listTranslations(){
		return translations.keySet();
	}

	public static String getTranslation(String language, String sentence){
		if (!translations.has(language)) return "(Error : there is a problem in the tpa_translations.json, please delete the file and restart the server or correct your translation)";
		return translations.get(language).getAsJsonObject().get(sentence).getAsString();
	}



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("blockdimension")
					.requires(source -> source.hasPermissionLevel(4))
						.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
									.executes(this::blockDimensionExecute)));
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("unblockdimension")
					.requires(source -> source.hasPermissionLevel(4))
						.then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
								.executes(this::unblockDimensionExecute)));
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("languagedimension")
					.then(CommandManager.argument("language", StringArgumentType.string())
							.suggests(new LanguageSuggestionProvider())
							.executes(this::languagedimensionExecute)));
		});
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			translations = JsonHandler.loadTranslations(server);
		});
		LOGGER.info("Dimension Blocker loaded successfully!");
	}

	private int blockDimensionExecute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerWorld dimension = DimensionArgumentType.getDimensionArgument(context,"dimension");
		ConfigData configData = StateSaverAndLoader.getConfigState(context.getSource().getServer());
		PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
		if (!configData.getDimensions().contains(dimension.getRegistryKey().getValue().toString())) {
			configData.getDimensions().add(dimension.getRegistryKey().getValue().toString());
			StateSaverAndLoader.saveState(context.getSource().getServer());
			context.getSource().sendFeedback(() -> Text.literal(getTranslation(playerData.getLanguage(), "blockdimension_success")).formatted(Formatting.GREEN), false);
			context.getSource().getPlayer().playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.0f);
		}
		else{
			context.getSource().sendFeedback(() -> Text.literal(getTranslation(playerData.getLanguage(), "blockdimension_failure")).formatted(Formatting.RED), false);
			context.getSource().getPlayer().playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
		}
		return 1;
	}

	private int unblockDimensionExecute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerWorld dimension = DimensionArgumentType.getDimensionArgument(context,"dimension");
		ConfigData configData = StateSaverAndLoader.getConfigState(context.getSource().getServer());
		PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
		if (configData.getDimensions().contains(dimension.getRegistryKey().getValue().toString())) {
			configData.getDimensions().remove(dimension.getRegistryKey().getValue().toString());
			StateSaverAndLoader.saveState(context.getSource().getServer());
			context.getSource().sendFeedback(() -> Text.literal(getTranslation(playerData.getLanguage(), "unblockdimension_success")).formatted(Formatting.GREEN), false);
			context.getSource().getPlayer().playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.0f);
		}
		else{
			context.getSource().sendFeedback(() -> Text.literal(getTranslation(playerData.getLanguage(), "unblockdimension_failure")).formatted(Formatting.RED), false);
			context.getSource().getPlayer().playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
		}
		return 1;
	}

	private int languagedimensionExecute(CommandContext<ServerCommandSource> context){
		PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
		String new_language = StringArgumentType.getString(context,"language");
		if (translations.has(new_language)){
			playerData.setLanguage(new_language);
			StateSaverAndLoader.saveState(Objects.requireNonNull(context.getSource().getServer()));
			context.getSource().sendFeedback(() -> Text.literal(getTranslation(playerData.getLanguage(),"dimensionlanguage_success")).formatted(Formatting.GREEN), false);
			context.getSource().getPlayer().playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.0f);
		}
		else{
			context.getSource().sendFeedback(() -> Text.literal(getTranslation(playerData.getLanguage(),"dimensionlanguage_failure")).formatted(Formatting.RED), false);
			context.getSource().getPlayer().playSoundToPlayer(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
		}
		return 1;
	}
}