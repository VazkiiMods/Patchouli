package vazkii.patchouli.client.book.template;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.util.ItemStackUtil;

public class TestProcessor implements IComponentProcessor {

	IRecipe recipe;
	
	@Override
	public void setup(IVariableProvider variables) {
		String recipeId = variables.get("recipe");
		recipe = CraftingManager.getRecipe(new ResourceLocation(recipeId));
	}

	@Override
	public String process(String key) {
		if(key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			Ingredient ingredient = recipe.getIngredients().get(index);
			ItemStack stack = ingredient.getMatchingStacks()[0];
			
			return ItemStackUtil.serializeStack(stack);
		}
		
		else if(key.equals("text")) {
			ItemStack out = recipe.getRecipeOutput();
			return "The recipe output is " + out.getCount() + "x " + out.getDisplayName();
		}
		
		return null;
	}

}
