package vazkii.patchouli.common.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ingredient.class)
public interface MixinIngredient {
	@Invoker
	void callCacheMatchingStacks();

	@Accessor
	ItemStack[] getMatchingStacks();
}
