package com.dimensionblocker.mixin;

import com.dimensionblocker.ConfigData;
import com.dimensionblocker.PlayerData;
import com.dimensionblocker.Dimensionblocker;
import com.dimensionblocker.StateSaverAndLoader;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ExecutionException;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "teleportTo(Lnet/minecraft/world/TeleportTarget;)Lnet/minecraft/server/network/ServerPlayerEntity;", at = @At("HEAD"), cancellable = true)
    private void onTeleport(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ConfigData configData = StateSaverAndLoader.getConfigState(teleportTarget.world().getServer());
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        String permission = "dimensionblocker.allow." + teleportTarget.world().getRegistryKey().getValue().toString().replace(":", ".");
        if (configData.getDimensions().contains(teleportTarget.world().getRegistryKey().getValue().toString()) && !Permissions.check(player.getUuid(),permission).join()){
            player.sendMessage(Text.literal(Dimensionblocker.getTranslation(playerData.getLanguage(),"dimension_cancel")).formatted(Formatting.RED), false);
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
