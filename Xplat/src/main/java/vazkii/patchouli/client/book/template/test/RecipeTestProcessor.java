package vazkii.patchouli.client.book.template.test;

import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class RecipeTestProcessor implements IComponentProcessor {

	@Override
	public void setup(Level level, IVariableProvider variables) {
		//  Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'setup'");
	}

	@Override
	public IVariable process(Level level, String key) {
		//  Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'process'");
	}

	// private RecipeHolder<?> recipe;

	// @Override
	// public void setup(Level level, IVariableProvider variables) {
	// 	//  probably add a recipe serializer?
	// 	String recipeId = variables.get("recipe", level.registryAccess()).asString();
	// 	RecipeManager manager = level.getServer().getRecipeManager();
	// 	ResourceLocation rL = ResourceLocation.fromNamespaceAndPath("patchouli", recipeId);
	// 	Optional<RecipeHolder<?>> recipeHolder = manager.byKey(ResourceKey.create(ResourceKey.createRegistryKey(rL), rL));

		

	// 	if (recipeHolder.isPresent()) {
	// 		recipe = recipeHolder.get();
	// 		Recipe<?> result = null;

	// 		if (recipe.value() instanceof Recipe<?> recipe2) {
	// 			result = recipe2;
	// 		}
	// 	} else {
	// 		throw new IllegalArgumentException("Could not find recipe: " + recipeId);
	// 	}
	// }

	// @Override
	// public IVariable process(Level level, String key) {
	// 	if (key.startsWith("item")) {
	// 		int index = Integer.parseInt(key.substring(4)) - 1;
	// 		Ingredient ingredient = recipe().get(index);
	// 		ItemStack[] stacks = ingredient.getItems();
	// 		ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

	// 		return IVariable.from(stack, level.registryAccess());
	// 	} else if (key.equals("text")) {
	// 		return IVariable.wrap(result.getCount() + "x$(br)" + result.getHoverName().getString(), level.registryAccess());
	// 	} else if (key.equals("icount")) {
	// 		return IVariable.wrap(result.getCount(), level.registryAccess());
	// 	} else if (key.equals("iname")) {
	// 		return IVariable.wrap(result.getHoverName().getString(), level.registryAccess());
	// 	}

	// 	return null;
	// }
	

}
