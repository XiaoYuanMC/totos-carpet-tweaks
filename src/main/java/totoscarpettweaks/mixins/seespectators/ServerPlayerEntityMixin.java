package totoscarpettweaks.mixins.seespectators;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import totoscarpettweaks.TotoCarpetSettings;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "updateInvisibilityStatus", at = @At("RETURN"))
    private void noInvisibleSpectators(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (TotoCarpetSettings.visibleSpectators && self.isSpectator()) {
            self.setInvisible(false);
        }
    }

    @Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
    private void allowSpectatorsToBeSpectated(ServerPlayer viewer, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (TotoCarpetSettings.visibleSpectators) {
            if (viewer.isSpectator()) cir.setReturnValue(self.getCamera() == self);
            else if (self.isSpectator()) cir.setReturnValue(true);
        }
    }
}
