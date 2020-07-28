package vazkii.patchouli.mixin;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface AccessorRecipeManager {
	@Invoker
	<C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> callGetRecipes(IRecipeType<T> type);
}
