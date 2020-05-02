package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public class RecipeTestProcessor implements IComponentProcessor {

	private IRecipe<?> recipe;

	@Override
	public void setup(IVariableProvider variables) {
		// TODO probably add a recipe serializer?
		String recipeId = variables.get("recipe").asString();
		RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
		recipe = manager.getRecipe(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public IVariable process(String key) {
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getIngredients().get(index);
			ItemStack[] stacks = ingredient.getMatchingStacks();
			ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

			return IVariable.from(stack);
		} else if (key.equals("text")) {
			ItemStack out = recipe.getRecipeOutput();
			return IVariable.wrap(out.getCount() + "x$(br)" + out.getDisplayName());
		} else if (key.equals("icount")) {
			return IVariable.wrap(recipe.getRecipeOutput().getCount());
		} else if (key.equals("iname")) {
			return IVariable.wrap(recipe.getRecipeOutput().getDisplayName().getString());
		}

		return null;
	}

}
