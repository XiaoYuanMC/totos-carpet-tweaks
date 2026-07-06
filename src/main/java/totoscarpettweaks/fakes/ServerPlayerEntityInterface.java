package totoscarpettweaks.fakes;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ServerPlayerEntityInterface {
    public boolean toto$hasReturnPosition();
    public Vec3 getSurvivalPosition();
    public float getSurvivalYaw();
    public float getSurvivalPitch();
    public ResourceKey<Level> getSurvivalWorldKey();
    public String getSurvivalDimensionName();
    public void rememberSurvivalPosition();
    public boolean tryReturnToSurvivalPosition();
}
