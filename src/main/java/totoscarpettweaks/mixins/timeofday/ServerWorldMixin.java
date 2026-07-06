package totoscarpettweaks.mixins.timeofday;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import totoscarpettweaks.controllers.TimeOfDayAdvanced;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {
    @Inject(method = "tickTime", at = @At("RETURN"))
    protected void onTimeTick(CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (self.dimension() == Level.OVERWORLD) {
            // The villager schedule logger could be invoked directly here, and normally would be,
            // however this time of day advancement could be a useful hook to have for more than this
            // feature and so instead I've created a "controller" which will handle passing out this value
            // to any feature, albeit hard-coded, which may have use of it.
            TimeOfDayAdvanced.handle((int) (self.getDefaultClockTime() % 24000L));
        }
    }
}
