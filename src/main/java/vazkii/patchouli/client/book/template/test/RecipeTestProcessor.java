package vazkii.patchouli.client.book.template.test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

public class RecipeTestProcessor implements IComponentProcessor {

	IRecipe recipe;
	
	@Override
	public void setup(IVariableProvider variables) {
		String recipeId = variables.get("recipe");
		recipe = CraftingManager.getRecipe(new ResourceLocation(recipeId));
	}

	@Override
	public String process(String key) {
		System.out.println("Processing " + key);
		
		if(key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getIngredients().get(index);
			ItemStack[] stacks = ingredient.getMatchingStacks();
			ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
			
			return ItemStackUtil.serializeStack(stack);
		}
		
		else if(key.equals("text")) {
			ItemStack out = recipe.getRecipeOutput();
			return out.getCount() + "x$(br)" + out.getDisplayName();
		}
		
		else if(key.equals("icount"))
			return Integer.toString(recipe.getRecipeOutput().getCount());
		
		else if(key.equals("iname"))
			return recipe.getRecipeOutput().getDisplayName();
		
		return null;
	}

}
