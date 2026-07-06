package totoscarpettweaks.mixins.returnspectators;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import totoscarpettweaks.TotoCarpetSettings;
import totoscarpettweaks.fakes.ServerPlayerEntityInterface;

import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityInterface {
    private static final String NBT_PREFIX = "CarpetTotosExtras_";
    private static final String NBT_SURVIVALX = getTagKey("SurvivalX");
    private static final String NBT_SURVIVALY = getTagKey("SurvivalY");
    private static final String NBT_SURVIVALZ = getTagKey("SurvivalZ");
    private static final String NBT_SURVIVALYAW = getTagKey("SurvivalYaw");
    private static final String NBT_SURVIVALPITCH = getTagKey("SurvivalPitch");
    private static final String NBT_SURVIVALWORLD = getTagKey("SurvivalWorld");
    private static final String UNKNOWN = "Unknown";

    private Vec3 survivalPos;
    private float survivalYaw;
    private float survivalPitch;
    private ResourceKey<Level> survivalWorldKey;

    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Shadow
    public abstract boolean teleportTo(ServerLevel targetWorld, double x, double y, double z, Set<Relative> relativeSet, float yaw, float pitch, boolean dismount);

    @Shadow
    public abstract ServerLevel level();

    @Override
    public boolean toto$hasReturnPosition() {
        return survivalPos != null && survivalWorldKey != null;
    }

    public Vec3 getSurvivalPosition() {
        return survivalPos;
    }

    public float getSurvivalYaw() {
        return survivalYaw;
    }

    public float getSurvivalPitch() {
        return survivalPitch;
    }

    public ResourceKey<Level> getSurvivalWorldKey() {
        return survivalWorldKey;
    }

    @Override
    public String getSurvivalDimensionName() {
        if (survivalWorldKey == null)
            return UNKNOWN;
        return survivalWorldKey.identifier().getPath();
    }

    @Override
    public void rememberSurvivalPosition() {
        if (!isPlayerAlive())
            return;
        ServerPlayer self = (ServerPlayer) (Object) this;
        setSurvivalPosition(self.getX(), self.getY(), self.getZ());
        survivalYaw = self.getYRot(1);
        survivalPitch = self.getXRot(1);
        survivalWorldKey = level().dimension();
    }

    @Override
    public boolean tryReturnToSurvivalPosition() {
        if (toto$hasReturnPosition() && connection != null && ((ServerPlayer) (Object) this).isAlive()) {
            ServerLevel world = level().getServer().getLevel(survivalWorldKey);
            if (world != null) {
                teleportTo(world, survivalPos.x, survivalPos.y, survivalPos.z, Set.of(), survivalYaw, survivalPitch, true);
                clearSurvivalPosition();
                return true;
            }

        }
        clearSurvivalPosition();
        return false;
    }

    private void clearSurvivalPosition() {
        survivalPos = null;
        survivalWorldKey = null;
        survivalYaw = 0;
        survivalPitch = 0;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writeSurvivalPosition(ValueOutput tag, CallbackInfo ci) {
        if (TotoCarpetSettings.returnSpectators && toto$hasReturnPosition()) {
            tag.putDouble(NBT_SURVIVALX, survivalPos.x);
            tag.putDouble(NBT_SURVIVALY, survivalPos.y);
            tag.putDouble(NBT_SURVIVALZ, survivalPos.z);
            tag.putFloat(NBT_SURVIVALYAW, survivalYaw);
            tag.putFloat(NBT_SURVIVALPITCH, survivalPitch);
            tag.putString(NBT_SURVIVALWORLD, survivalWorldKey.identifier().toString());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readSurvivalPosition(ValueInput tag, CallbackInfo ci) {
        if (TotoCarpetSettings.returnSpectators) {
            tag.getString(NBT_SURVIVALWORLD).ifPresent(worldName -> {
                setSurvivalPosition(
                        tag.getDoubleOr(NBT_SURVIVALX, 0),
                        tag.getDoubleOr(NBT_SURVIVALY, 0),
                        tag.getDoubleOr(NBT_SURVIVALZ, 0));
                survivalYaw = tag.getFloatOr(NBT_SURVIVALYAW, 0);
                survivalPitch = tag.getFloatOr(NBT_SURVIVALPITCH, 0);

                Identifier worldId = Identifier.tryParse(worldName);
                if (worldId != null)
                    survivalWorldKey = ResourceKey.create(Registries.DIMENSION, worldId);
            });
        }
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntityInterface oldServerPlayer = (ServerPlayerEntityInterface) oldPlayer;
        survivalPos = oldServerPlayer.getSurvivalPosition();
        survivalPitch = oldServerPlayer.getSurvivalPitch();
        survivalYaw = oldServerPlayer.getSurvivalYaw();
        survivalWorldKey = oldServerPlayer.getSurvivalWorldKey();
    }

    private void setSurvivalPosition(double x, double y, double z) {
        survivalPos = new Vec3(x, y, z);
    }

    private boolean isPlayerAlive() {
        return connection != null && ((ServerPlayer) (Object) this).isAlive();
    }

    private static String getTagKey(String key) {
        return NBT_PREFIX + key;
    }
}
