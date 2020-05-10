package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(IVariableProvider<String> variables) {
		String recipeId = variables.get("recipe");
		RecipeManager manager = MinecraftClient.getInstance().world.getRecipeManager();
		recipe = manager.get(new Identifier(recipeId)).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public String process(String key) {
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getPreviewInputs().get(index);
			ItemStack[] stacks = ingredient.getMatchingStacksClient();
			ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

			return ItemStackUtil.serializeStack(stack);
		}

		else if (key.equals("text")) {
			ItemStack out = recipe.getOutput();
			return out.getCount() + "x$(br)" + out.getName();
		}

		else if (key.equals("icount"))
			return Integer.toString(recipe.getOutput().getCount());

		else if (key.equals("iname"))
			return recipe.getOutput().getName().getString();

		return null;
	}

}
