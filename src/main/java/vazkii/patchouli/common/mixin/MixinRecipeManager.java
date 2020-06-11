package vazkii.patchouli.common.mixin;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface MixinRecipeManager {
	@Invoker
	Map<Identifier, Recipe<?>> callGetAllOfType(RecipeType<?> type);
}
