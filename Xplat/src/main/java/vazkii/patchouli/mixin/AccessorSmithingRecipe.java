package vazkii.patchouli.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("removal")
@Mixin(LegacyUpgradeRecipe.class)
public interface AccessorSmithingRecipe {
	@Accessor("base")
	Ingredient getBase();

	@Accessor("addition")
	Ingredient getAddition();
}
