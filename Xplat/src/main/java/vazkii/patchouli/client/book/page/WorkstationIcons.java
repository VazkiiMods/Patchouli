package vazkii.patchouli.client.book.page;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Map;

public final class WorkstationIcons {

	public static final Map<RecipeType<?>, ItemStack> ICONS = Map.of(
			RecipeType.CRAFTING, new ItemStack(net.minecraft.world.item.Items.CRAFTING_TABLE),
			RecipeType.SMELTING, new ItemStack(net.minecraft.world.item.Items.FURNACE),
			RecipeType.BLASTING, new ItemStack(net.minecraft.world.item.Items.BLAST_FURNACE),
			RecipeType.SMOKING, new ItemStack(net.minecraft.world.item.Items.SMOKER),
			RecipeType.CAMPFIRE_COOKING, new ItemStack(net.minecraft.world.item.Items.CAMPFIRE),
			RecipeType.STONECUTTING, new ItemStack(net.minecraft.world.item.Items.STONECUTTER),
			RecipeType.SMITHING, new ItemStack(net.minecraft.world.item.Items.SMITHING_TABLE)

	);

	public static ItemStack iconFor(RecipeType<?> type) {
		return ICONS.getOrDefault(type, ItemStack.EMPTY);
	}
}
