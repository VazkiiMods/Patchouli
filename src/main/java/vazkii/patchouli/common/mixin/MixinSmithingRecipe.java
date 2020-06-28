package vazkii.patchouli.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;

@Mixin(SmithingRecipe.class)
public interface MixinSmithingRecipe {
	@Accessor
	Ingredient getBase();
	
	@Accessor
	Ingredient getAddition();
}
