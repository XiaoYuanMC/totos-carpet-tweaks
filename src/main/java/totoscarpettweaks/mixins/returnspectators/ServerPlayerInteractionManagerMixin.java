package totoscarpettweaks.mixins.returnspectators;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import totoscarpettweaks.TotoCarpetSettings;
import totoscarpettweaks.fakes.ServerPlayerEntityInterface;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerPlayer player;

    @Shadow
    private GameType gameModeForPlayer;

    @Inject(
            method = "changeGameModeForPlayer(Lnet/minecraft/world/level/GameType;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayerGameMode;setGameModeForPlayer(Lnet/minecraft/world/level/GameType;Lnet/minecraft/world/level/GameType;)V",
                    shift = At.Shift.BEFORE))
    private void onGameModeChange(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (TotoCarpetSettings.returnSpectators && !(player instanceof EntityPlayerMPFake)) {
            if (gameMode == GameType.SURVIVAL) {
                ((ServerPlayerEntityInterface) player).tryReturnToSurvivalPosition();
            } else if (this.gameModeForPlayer == GameType.SURVIVAL) {
                ((ServerPlayerEntityInterface) player).rememberSurvivalPosition();
            }
        }
    }
}
