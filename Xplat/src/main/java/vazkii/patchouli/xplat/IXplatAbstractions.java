package vazkii.patchouli.xplat;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Cross-modloader abstracted calls
 */
public interface IXplatAbstractions {
	// Events
	void fireDrawBookScreen(Identifier book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphicsExtractor graphics);
	void fireBookReload(Identifier book);

	// Networking
	void sendReloadContentsMessage(MinecraftServer server);
	void sendOpenBookGui(ServerPlayer player, Identifier book, @Nullable Identifier entry, int page);

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
		var providers = ServiceLoader.load(IXplatAbstractions.class, IXplatAbstractions.class.getClassLoader()).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			throw new IllegalStateException("There should be exactly one IXplatAbstractions implementation on the classpath. Found: " + names);
		} else {
			var provider = providers.getFirst();
			PatchouliAPI.LOGGER.debug("Instantiating xplat impl: {}", provider.type().getName());
			return provider.get();
		}
	}

	Ingredient createComponentIngredient(ItemStack itemStack);

	Ingredient createCompoundIngredient(Ingredient[] ingredients);

	<T extends Item> Holder<Item> registerItem(String name, Function<Item.Properties, T> factory, UnaryOperator<Item.Properties> operator);
}
