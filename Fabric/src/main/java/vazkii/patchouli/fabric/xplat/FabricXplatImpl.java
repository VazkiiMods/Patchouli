package vazkii.patchouli.fabric.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import vazkii.patchouli.api.BookContentsReloadCallback;
import vazkii.patchouli.api.BookDrawScreenCallback;
import vazkii.patchouli.fabric.common.FabricRecipeSerializerWrapper;
import vazkii.patchouli.fabric.network.FabricMessageOpenBookGui;
import vazkii.patchouli.fabric.network.FabricMessageReloadBookContents;
import vazkii.patchouli.xplat.IXplatAbstractions;
import vazkii.patchouli.xplat.XplatModContainer;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class FabricXplatImpl implements IXplatAbstractions {
	@Override
	public void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, PoseStack ms) {
		BookDrawScreenCallback.EVENT.invoker().trigger(book, gui, mouseX, mouseY, partialTicks, ms);
	}

	@Override
	public void fireBookReload(ResourceLocation book) {
		BookContentsReloadCallback.EVENT.invoker().trigger(book);
	}

	@Override
	public void sendReloadContentsMessage(MinecraftServer server) {
		FabricMessageReloadBookContents.sendToAll(server);
	}

	@Override
	public void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		FabricMessageOpenBookGui.send(player, book, entry, page);
	}

	@Override
	public Collection<XplatModContainer> getAllMods() {
		List<XplatModContainer> ret = new ArrayList<>();
		for (var mod : FabricLoader.getInstance().getAllMods()) {
			ret.add(new FabricXplatModContainer(mod));
		}
		return ret;
	}

	@Override
	public XplatModContainer getModContainer(String modId) {
		return new FabricXplatModContainer(FabricLoader.getInstance().getModContainer(modId).get());
	}

	@Override
	public boolean isDevEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public Tag.Named<Block> blockTag(ResourceLocation id) {
		return TagFactory.BLOCK.create(id);
	}

	@Override
	public boolean isPhysicalClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public <T extends Recipe<?>, U extends T> RecipeSerializer<U> makeWrapperSerializer(RecipeSerializer<T> inner, BiFunction<T, ResourceLocation, U> converter) {
		return new FabricRecipeSerializerWrapper<>(inner, converter);
	}
}
