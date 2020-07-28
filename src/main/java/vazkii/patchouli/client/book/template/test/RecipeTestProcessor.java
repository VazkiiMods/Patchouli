package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(IVariableProvider variables) {
		// TODO probably add a recipe serializer?
		String recipeId = variables.get("recipe").asString();
		RecipeManager manager = MinecraftClient.getInstance().world.getRecipeManager();
		recipe = manager.get(new Identifier(recipeId)).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public IVariable process(String key) {
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getPreviewInputs().get(index);
			ItemStack[] stacks = ingredient.getMatchingStacksClient();
			ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

			return IVariable.from(stack);
		} else if (key.equals("text")) {
			ItemStack out = recipe.getOutput();
			return IVariable.wrap(out.getCount() + "x$(br)" + out.getName());
		} else if (key.equals("icount")) {
			return IVariable.wrap(recipe.getOutput().getCount());
		} else if (key.equals("iname")) {
			return IVariable.wrap(recipe.getOutput().getName().getString());
		}

		return null;
	}

}
