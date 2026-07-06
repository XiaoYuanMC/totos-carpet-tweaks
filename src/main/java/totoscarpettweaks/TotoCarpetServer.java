package totoscarpettweaks;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.logging.Logger;
import carpet.logging.LoggerRegistry;
import carpet.utils.Translations;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import totoscarpettweaks.commands.ToggleSpectatorCommand;
import totoscarpettweaks.listeners.PlayerLoggedInListener;

import java.util.Map;

public class TotoCarpetServer implements CarpetExtension {
	public static void noop() { }

	static {
		CarpetServer.manageExtension(new TotoCarpetServer());
	}

	private PlayerLoggedInListener playerLoggedInListener;

	@Override
	public void onGameStarted() {
		CarpetServer.settingsManager.parseSettingsClass(TotoCarpetSettings.class);
		playerLoggedInListener = new PlayerLoggedInListener();
	}

	@Override
	public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher,
								 final CommandBuildContext commandBuildContext) {
		ToggleSpectatorCommand.register(dispatcher);
	}

	@Override
	public void registerLoggers() {
		try {
			LoggerRegistry.registerLogger("villagerSchedule", new Logger(TotoCarpetServer.class.getField("__villagerSchedule"), "villagerSchedule", null, null, false));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Could not create logger: villagerSchedule");
		}
	}

	@Override
	public void onPlayerLoggedIn(ServerPlayer player) {
		playerLoggedInListener.handle(player);
	}

	@Override
	public String version() {
		return "totos-extras";
	}

	@Override
	public Map<String, String> canHasTranslations(String lang) {
		return Translations.getTranslationFromResourcePath(String.format("assets/totos-carpet-tweaks/lang/%s.json", lang));
	}

	public static boolean __villagerSchedule;
}
