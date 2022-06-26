package vazkii.patchouli.fabric.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.BookContentsReloadCallback;
import vazkii.patchouli.api.BookDrawScreenCallback;
import vazkii.patchouli.fabric.client.rei.ReiCompat;
import vazkii.patchouli.fabric.network.FabricMessageOpenBookGui;
import vazkii.patchouli.fabric.network.FabricMessageReloadBookContents;
import vazkii.patchouli.xplat.IXplatAbstractions;
import vazkii.patchouli.xplat.XplatModContainer;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public boolean isPhysicalClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public boolean handleRecipeKeybind(int keyCode, int scanCode, @Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			return ReiCompat.handleRecipeKeybind(keyCode, scanCode, stack);
		}
		return false;
	}
}
