package vazkii.patchouli.xplat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Cross-modloader abstracted calls
 */
public interface IXplatAbstractions {
	// Events
	void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics);
	void fireBookReload(ResourceLocation book);

	// Networking
	void sendReloadContentsMessage(MinecraftServer server);
	void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page);

	// FML/FabricLoader-related
	Collection<XplatModContainer> getAllMods();
	XplatModContainer getModContainer(String modId);
	boolean isModLoaded(String modId);
	boolean isDevEnvironment();

	// Misc
	boolean isPhysicalClient();

	// Needed because of Forge
	default void signalBooksLoaded() {}

	// JEI/REI compat
	boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack);

	IXplatAbstractions INSTANCE = find();

	private static IXplatAbstractions find() {
		var providers = ServiceLoader.load(IXplatAbstractions.class).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			IllegalStateException exception = new IllegalStateException("There should be exactly one IXplatAbstractions implementation on the classpath, but there are " + providers.size() + ". Found: " + names);

			//TODO: Something is breaking our serviceloader on NeoForge (see https://github.com/VazkiiMods/Patchouli/issues/792).
			// No clue why. Just duct-tape it for now.
			try {
				IXplatAbstractions abs = (IXplatAbstractions) Class.forName("vazkii.patchouli.neoforge.xplat.NeoForgeXplatImpl").getConstructor().newInstance();
				PatchouliAPI.LOGGER.error("Successfully loaded NeoForge backup, but the ServiceLoader wasn't working. Report this.", exception);
				return abs;
			} catch (Exception e) {
				exception.addSuppressed(new RuntimeException("Failed to load NeoForge backup", e));
			}
	
			try {
				IXplatAbstractions abs = (IXplatAbstractions) Class.forName("vazkii.patchouli.fabric.xplat.FabricXplatImpl").getConstructor().newInstance();
				PatchouliAPI.LOGGER.error("Successfully loaded Fabric backup, but the ServiceLoader wasn't working. Report this.", exception);
				return abs;
			} catch (Exception e) {
				exception.addSuppressed(new RuntimeException("Failed to load Fabric backup", e));
			}

			throw exception;
		} else {
			var provider = providers.get(0);
			PatchouliAPI.LOGGER.debug("Instantiating xplat impl: " + provider.type().getName());
			return provider.get();
		}
	}
}
