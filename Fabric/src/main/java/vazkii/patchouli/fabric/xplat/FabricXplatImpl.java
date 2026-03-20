package vazkii.patchouli.fabric.xplat;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import vazkii.patchouli.api.BookContentsReloadCallback;
import vazkii.patchouli.api.BookDrawScreenCallback;
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
	public void fireDrawBookScreen(Identifier book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphicsExtractor graphics) {
		BookDrawScreenCallback.EVENT.invoker().trigger(book, gui, mouseX, mouseY, partialTicks, graphics);
	}

	@Override
	public void fireBookReload(Identifier book) {
		BookContentsReloadCallback.EVENT.invoker().trigger(book);
	}

	@Override
	public void sendReloadContentsMessage(MinecraftServer server) {
		FabricMessageReloadBookContents.sendToAll(server);
	}

	@Override
	public void sendOpenBookGui(ServerPlayer player, Identifier book, @Nullable Identifier entry, int page) {
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
		/*if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			return ReiCompat.handleRecipeKeybind(keyCode, scanCode, stack);
		}*/
		return false;
	}

	@Override
	public Ingredient createComponentIngredient(ItemStack itemStack) {
		return DefaultCustomIngredients.components(itemStack);
	}

	@Override
	public Ingredient createCompoundIngredient(Ingredient[] ingredients) {
		return DefaultCustomIngredients.any(ingredients);
	}
}
