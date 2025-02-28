package com.dimensionblocker.mixin;

import com.dimensionblocker.ConfigData;
import com.dimensionblocker.PlayerData;
import com.dimensionblocker.Dimensionblocker;
import com.dimensionblocker.StateSaverAndLoader;
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
        if (configData.getDimensions().contains(destination.getRegistryKey().getValue().toString())){
            player.sendMessage(Text.literal(Dimensionblocker.getTranslation(playerData.getLanguage(),"dimension_cancel")).formatted(Formatting.RED), false);
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
