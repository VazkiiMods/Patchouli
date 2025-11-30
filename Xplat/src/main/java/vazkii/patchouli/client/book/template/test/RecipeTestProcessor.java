package vazkii.patchouli.client.book.template.test;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.List;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(Level level, IVariableProvider variables) {
		// TODO probably add a recipe serializer?
		String recipeId = variables.get("recipe", level.registryAccess()).asString();
		RecipeManager manager = level.getRecipeManager();
		recipe = manager.byKey(ResourceLocation.tryParse(recipeId)).orElseThrow(IllegalArgumentException::new).value();
	}

	@Override
	public IVariable process(Level level, String key) {
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getIngredients().get(index);
			List<ItemStack> stacks = ItemStackUtil.getStacksFromIngredient(ingredient, level);
			ItemStack stack = stacks.isEmpty() ? ItemStack.EMPTY : stacks.getFirst();

			return IVariable.from(stack, level.registryAccess());
		} else if (key.equals("text")) {
			ItemStack out = recipe.getResultItem(level.registryAccess());
			return IVariable.wrap(out.getCount() + "x$(br)" + out.getHoverName(), level.registryAccess());
		} else if (key.equals("icount")) {
			return IVariable.wrap(recipe.getResultItem(level.registryAccess()).getCount(), level.registryAccess());
		} else if (key.equals("iname")) {
			return IVariable.wrap(recipe.getResultItem(level.registryAccess()).getHoverName().getString(), level.registryAccess());
		}

		return null;
	}

}
