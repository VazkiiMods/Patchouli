package vazkii.patchouli.xplat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

	IXplatAbstractions INSTANCE = ServiceUtil.findService(IXplatAbstractions.class);
}
