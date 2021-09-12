package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(IVariableProvider variables) {
		if (Minecraft.getInstance().level == null) {
			return;
		}
		// TODO probably add a recipe serializer?
		String recipeId = variables.get("recipe").asString();
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
		recipe = manager.byKey(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public IVariable process(String key) {
		if (recipe == null) {
			return null;
		}
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getIngredients().get(index);
			ItemStack[] stacks = ingredient.getItems();
			ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

			return IVariable.from(stack);
		} else if (key.equals("text")) {
			ItemStack out = recipe.getResultItem();
			return IVariable.wrap(out.getCount() + "x$(br)" + out.getHoverName());
		} else if (key.equals("icount")) {
			return IVariable.wrap(recipe.getResultItem().getCount());
		} else if (key.equals("iname")) {
			return IVariable.wrap(recipe.getResultItem().getHoverName().getString());
		}

		return null;
	}

}
