package vazkii.patchouli.fabric.client.rei;

import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.favorites.FavoriteEntry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliSounds;

public class ReiCompat {
	public static boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack) {
		var instance = ConfigObject.getInstance();
		if (instance.getRecipeKeybind().matchesKey(keyCode, scanCode)) {
			return ViewSearchBuilder.builder().addRecipesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack)).open();
		}
		if (instance.getUsageKeybind().matchesKey(keyCode, scanCode)) {
			return ViewSearchBuilder.builder().addUsagesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack)).open();
		}
		if (instance.getFavoriteKeyCode().matchesKey(keyCode, scanCode)) {
			try { // In case the unstable API changes
				addFavorite(stack);
			} catch (Exception e) {
				PatchouliAPI.LOGGER.error("Failed to favorite item {}", stack.toString(), e);
				return false;
			}
			// Play a sound as some simple feedback that something happened
			Minecraft.getInstance().player.playSound(PatchouliSounds.BOOK_OPEN, 1F, (float) (0.7 + Math.random() * 0.4));
			return true;
		}

		return false;
	}

	@SuppressWarnings("UnstableApiUsage")
	private static void addFavorite(ItemStack stack) {
		var instance = ConfigObject.getInstance();
		FavoriteEntry entry = FavoriteEntry.fromEntryStack(EntryStack.of(VanillaEntryTypes.ITEM, stack));
		instance.getFavoriteEntries().remove(entry);
		instance.getFavoriteEntries().add(entry);
	}
}
