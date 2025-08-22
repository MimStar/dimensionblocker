package com.dimensionblocker.mixin;

import com.dimensionblocker.ConfigData;
import com.dimensionblocker.PlayerData;
import com.dimensionblocker.Dimensionblocker;
import com.dimensionblocker.StateSaverAndLoader;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "moveToWorld", at = @At("HEAD"), cancellable = true)
    private void onMoveToWorld(ServerWorld destination, CallbackInfoReturnable<ServerPlayerEntity> cir){
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ConfigData configData = StateSaverAndLoader.getConfigState(destination.getServer());
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        String permission = "dimensionblocker.allow." + destination.getRegistryKey().getValue().toString().replace(":", ".");
        if (configData.getDimensions().contains(destination.getRegistryKey().getValue().toString()) && !Permissions.check(player.getUuid(),permission).join()){
            player.sendMessage(Text.literal(Dimensionblocker.getTranslation(playerData.getLanguage(),"dimension_cancel")).formatted(Formatting.RED), false);
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
