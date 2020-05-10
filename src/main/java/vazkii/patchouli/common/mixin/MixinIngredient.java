package vazkii.patchouli.common.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface MixinIngredient {
	@Accessor
	ItemStack[] getMatchingStacks();
}
