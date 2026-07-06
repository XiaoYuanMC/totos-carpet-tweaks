package totoscarpettweaks.commands;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import totoscarpettweaks.TotoCarpetSettings;
import totoscarpettweaks.fakes.ServerPlayerEntityInterface;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.world.level.GameType.SPECTATOR;
import static net.minecraft.world.level.GameType.SURVIVAL;

/***
 * While the mod will remember survival positions when using the standard /gamemode command or F3+N,
 * that command is only available to operators. To open up spectator mode to all players (without
 * access to creative mode), a new command is registered.
 */
public class ToggleSpectatorCommand {
    private static final String POSITION_FORMAT_TEMPLATE = "w %.0f ";
    private static final String DIMENSION_FORMAT_TEMPLATE = "w %s";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("ts")
                        .requires(c -> TotoCarpetSettings.returnSpectators &&
                                CommandHelper.canUseCommand(c, (Object) TotoCarpetSettings.commandToggleSpectator))
                        .executes(c -> toggleSpectator(c.getSource()))
                        .then(
                                literal("info")
                                        .executes(c -> spectatorInfo(c.getSource()))
                        ));
    }

    private static int toggleSpectator(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player instanceof EntityPlayerMPFake) {
            Messenger.m(player, "r Command cannot be used on a fake player.");
            return 0;
        }

        if (player.gameMode.getGameModeForPlayer() == SURVIVAL) {
            player.setGameMode(SPECTATOR);
        } else {
            player.setGameMode(SURVIVAL);
        }
        return 1;
    }

    private static int spectatorInfo(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (!player.isSpectator()) {
            Messenger.m(player, "r You are not in spectator mode.");
            return 0;
        }

        if (player instanceof EntityPlayerMPFake) {
            Messenger.m(player, "r Command cannot be used on a fake player.");
            return 0;
        }

        ServerPlayerEntityInterface totoPlayer = (ServerPlayerEntityInterface) player;

        if (!totoPlayer.toto$hasReturnPosition()) {
            Messenger.m(player, "r Sorry, I can't remember your last position.");
            return 0;
        }

        Vec3 pos = totoPlayer.getSurvivalPosition();
        Messenger.m(player,
                "y X ",
                String.format(POSITION_FORMAT_TEMPLATE, pos.x),
                "y Y ",
                String.format(POSITION_FORMAT_TEMPLATE, pos.y),
                "y Z ",
                String.format(POSITION_FORMAT_TEMPLATE, pos.z));
        Messenger.m(player, "y Dimension ", String.format(DIMENSION_FORMAT_TEMPLATE, totoPlayer.getSurvivalDimensionName()));

        return 1;
    }
}
