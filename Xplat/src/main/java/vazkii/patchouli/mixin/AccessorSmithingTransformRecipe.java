package vazkii.patchouli.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(SmithingTransformRecipe.class)
public interface AccessorSmithingTransformRecipe {
	@Accessor
	Optional<Ingredient> getTemplate();

	@Accessor
	Ingredient getBase();

	@Accessor
	Optional<Ingredient> getAddition();
}
