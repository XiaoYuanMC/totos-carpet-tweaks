package totoscarpettweaks.mixins.globalmajorpositive;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import totoscarpettweaks.TotoCarpetSettings;

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.gossip.GossipType.MAJOR_POSITIVE;

@Mixin(GossipContainer.class)
public abstract class VillagerGossipsMixin {
    @Shadow
    public abstract Map<UUID, Object2IntMap<GossipType>> getGossipEntries();

    @Inject(method = "getReputation(Ljava/util/UUID;Ljava/util/function/Predicate;)I", at = @At("HEAD"), cancellable = true)
    public void overrideReputation(UUID target, Predicate<GossipType> filter, CallbackInfoReturnable<Integer> cir) {
        if (TotoCarpetSettings.sharedVillagerDiscounts && filter.test(MAJOR_POSITIVE)) {
            Map<UUID, Object2IntMap<GossipType>> gossipEntries = getGossipEntries();
            Object2IntMap<GossipType> targetReputation = gossipEntries.get(target);
            int otherRep = targetReputation == null
                    ? 0
                    : weightedValue(targetReputation, vgt -> filter.test(vgt) && vgt != MAJOR_POSITIVE);
            int majorPositiveRep = gossipEntries.values()
                    .stream()
                    .mapToInt(r -> weightedValue(r, vgt -> vgt == MAJOR_POSITIVE))
                    .sum();
            cir.setReturnValue(otherRep + Math.min(majorPositiveRep, MAJOR_POSITIVE.max * MAJOR_POSITIVE.weight));
        }
    }

    private static int weightedValue(Object2IntMap<GossipType> gossip, Predicate<GossipType> filter) {
        return gossip.object2IntEntrySet()
                .stream()
                .filter(entry -> filter.test(entry.getKey()))
                .mapToInt(entry -> entry.getIntValue() * entry.getKey().weight)
                .sum();
    }
}
