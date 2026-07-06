package totoscarpettweaks.mixins.villagecats;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.CatSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import totoscarpettweaks.TotoCarpetSettings;

@Mixin(CatSpawner.class)
public class CatSpawnerMixin {
    private static int lastSpawnChance;
    private static float spawnChance;

    @Inject(method = "spawnCat", at = @At(value = "HEAD"), cancellable = true)
    private void onSpawnCat(BlockPos pos, ServerLevel world, boolean persistent, CallbackInfo ci) {
        // Fallback to NMS
        if (TotoCarpetSettings.catSpawnChance == 100) return;
        if (TotoCarpetSettings.catSpawnChance == 0 || world.getRandom().nextFloat() >= getSpawnChance())
            ci.cancel();
    }

    // Calculates and stores the spawn chance. Although it's cheap to calculate, the setting is unlikely to change
    // and therefore the output will be the same.
    private static float getSpawnChance() {
        if (TotoCarpetSettings.catSpawnChance != lastSpawnChance) {
            spawnChance = (float)TotoCarpetSettings.catSpawnChance / 100;
            lastSpawnChance = TotoCarpetSettings.catSpawnChance;
        }
        return spawnChance;
    }
}
