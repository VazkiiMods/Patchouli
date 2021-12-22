package vazkii.patchouli.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * Cross-modloader abstracted calls
 */
public class XplatAbstractions {
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

		<T extends Recipe<?>, U extends T> RecipeSerializer<U> makeWrapperSerializer(RecipeSerializer<T> inner, BiFunction<T, ResourceLocation, U> converter);
	}

	private static IXplatAbstractions instance;

	public static void setInstance(IXplatAbstractions e) {
		if (instance != null) {
			throw new IllegalStateException("Setting xplat instance when it was already set!");
		} else {
			instance = e;
		}
	}

	public static IXplatAbstractions getInstance() {
		return instance;
	}
}
