package vazkii.patchouli.client.book.template.test;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(IVariableProvider variables) {
		// TODO probably add a recipe serializer?
		String recipeId = variables.get("recipe", RecipeUtil.getRegistryAccess().orElseThrow()).asString();
		RecipeManager manager = RecipeUtil.getRecipeManager().orElseThrow();
		recipe = manager.byKey(ResourceKey.create(ResourceKey.createRegistryKey(ResourceLocation.parse(recipeId)), ResourceLocation.parse(recipeId))
		).orElseThrow(IllegalArgumentException::new).value();
	}
	@SuppressWarnings("deprecation")
	public ItemStack[] getItemStacks (){
		ItemStack [] stacks = new ItemStack[0];

		if (recipe instanceof ShapedRecipe){
			List<Optional<Ingredient>> shapedIngredients = ((ShapedRecipe) recipe).getIngredients();
			Optional<Ingredient> ingredient = shapedIngredients.getFirst();
			if (ingredient.isPresent()) {
                stacks = ingredient.get().items().map(ItemStack.class::cast).toArray(ItemStack[]::new);

			}
		}
		else if (recipe instanceof ShapelessRecipe){
			List<Ingredient> shapelessIngredients = RecipeUtil.getShapelessIngredients((ShapelessRecipe) recipe);
			Ingredient ingredient = shapelessIngredients.getFirst();
            stacks = ingredient.items().map(ItemStack.class::cast).toArray(ItemStack[]::new);
		}

        return stacks;
    }

	@Override
	public IVariable process(String key) {
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;


			ItemStack[] stacks = getItemStacks();
			ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

			return IVariable.from(stack, RecipeUtil.getRegistryAccess().orElseThrow());
		} else if (key.equals("text")) {
			ItemStack out = RecipeUtil.getCraftingResult(Arrays.stream(getItemStacks()).toList());
			return IVariable.wrap(out.getCount() + "x$(br)" + out.getHoverName(), RecipeUtil.getRegistryAccess().orElseThrow());
		} else if (key.equals("icount")) {
			return IVariable.wrap(RecipeUtil.getCraftingResult(Arrays.stream(getItemStacks()).toList()).getCount(), RecipeUtil.getRegistryAccess().orElseThrow());
		} else if (key.equals("iname")) {
			return IVariable.wrap(RecipeUtil.getCraftingResult(Arrays.stream(getItemStacks()).toList()).getHoverName().getString(), RecipeUtil.getRegistryAccess().orElseThrow());
		}

		return null;
	}

}