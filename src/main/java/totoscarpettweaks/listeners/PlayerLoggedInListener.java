package totoscarpettweaks.listeners;

import net.minecraft.server.level.ServerPlayer;
import totoscarpettweaks.TotoCarpetSettings;
import totoscarpettweaks.fakes.ServerPlayerEntityInterface;

import static net.minecraft.world.level.GameType.SURVIVAL;

public class PlayerLoggedInListener {
    public void handle(ServerPlayer player) {
        if (TotoCarpetSettings.returnSpectators && player.gameMode.getGameModeForPlayer() == SURVIVAL) {
            ((ServerPlayerEntityInterface) player).tryReturnToSurvivalPosition();
        }
    }
}
