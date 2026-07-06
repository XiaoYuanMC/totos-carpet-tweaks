package totoscarpettweaks.mixins.piglinguarding;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import totoscarpettweaks.TotoCarpetSettings;

@Mixin(PiglinAi.class)
public class PiglinBrainMixin {
    @Inject(method = "angerNearbyPiglins", at = @At("HEAD"), cancellable = true)
    private static void cancelOnGuardedBlockInteracted(ServerLevel level, Player player, boolean blockOpen, CallbackInfo ci) {
        if (TotoCarpetSettings.noPiglinGuarding)
            ci.cancel();
    }
}
