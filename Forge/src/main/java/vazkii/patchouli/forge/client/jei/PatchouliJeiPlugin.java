package vazkii.patchouli.forge.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.mixin.client.AccessorKeyMapping;

import javax.annotation.Nonnull;

import java.util.Map;

@JeiPlugin
public class PatchouliJeiPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(PatchouliAPI.MOD_ID, PatchouliAPI.MOD_ID);

	private static final KeyMapping showRecipe, showUses;

	private static IJeiRuntime jeiRuntime;

	static {
		Map<String, KeyMapping> allKeyMappings = AccessorKeyMapping.getAllKeyMappings();
		showRecipe = allKeyMappings.get("key.jei.showRecipe");
		showUses = allKeyMappings.get("key.jei.showUses");
		if (showRecipe == null || showUses == null) {
			PatchouliAPI.LOGGER.warn("Could not locate JEI keybindings, lookups in books may not work");
		}
	}

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(@Nonnull ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(PatchouliItems.BOOK, (stack, context) -> {
			if (!stack.hasTag() || !stack.getTag().contains(ItemModBook.TAG_BOOK)) {
				return "";
			}
			return stack.getTag().getString(ItemModBook.TAG_BOOK);
		});
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		PatchouliJeiPlugin.jeiRuntime = jeiRuntime;
	}

	public static boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack) {
		if (showRecipe != null && showRecipe.matches(keyCode, scanCode)) {
			var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM, stack);
			jeiRuntime.getRecipesGui().show(focus);
			return true;
		}
		if (showUses != null && showUses.matches(keyCode, scanCode)) {
			var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM, stack);
			jeiRuntime.getRecipesGui().show(focus);
			return true;
		}
		return false;
	}
}
