package vazkii.patchouli.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Cross-modloader abstracted calls
 */
public interface IXplatAbstractions {
	// Events
	void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, PoseStack ms);
	void fireBookReload(ResourceLocation book);

	// Networking
	void sendReloadContentsMessage(MinecraftServer server);
	void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page);

	// FML/FabricLoader-related
	Collection<XplatModContainer> getAllMods();
	XplatModContainer getModContainer(String modId);
	boolean isDevEnvironment();

	// Misc
	Tag.Named<Block> blockTag(ResourceLocation id);
	boolean isPhysicalClient();

	// Needed because of Forge
	default void signalBooksLoaded() {}

	// JEI/REI compat
	boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack);

	<T extends Recipe<?>, U extends T> RecipeSerializer<U> makeWrapperSerializer(RecipeSerializer<T> inner, BiFunction<T, ResourceLocation, U> converter);

	IXplatAbstractions INSTANCE = find();

	private static IXplatAbstractions find() {
		var providers = ServiceLoader.load(IXplatAbstractions.class).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			throw new IllegalStateException("There should be exactly one IXplatAbstractions implementation on the classpath. Found: " + names);
		} else {
			var provider = providers.get(0);
			PatchouliAPI.LOGGER.debug("Instantiating xplat impl: " + provider.type().getName());
			return provider.get();
		}
	}
}
