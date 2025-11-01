package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.RecipeUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(IVariableProvider variables) {
		String recipeIdStr = variables.get("recipe", RecipeUtil.getRegistryAccess().orElseThrow()).asString();
		if (recipeIdStr == null || recipeIdStr.isEmpty()) {
			this.recipe = null;
			return;
		}

		RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().getSingleplayerServer()).getRecipeManager();
		ResourceLocation loc = ResourceLocation.parse(recipeIdStr);
		ResourceKey key = ResourceKey.create(ResourceKey.createRegistryKey(loc), loc);
		Optional<? extends Recipe<?>> recipeOpt = manager.byKey(key);

        this.recipe = recipeOpt.orElse(null);
	}

	@Override
	public IVariable process(String key) {
		if (recipe == null) {
			return IVariable.wrap("Recipe not found!");
		}

		ItemStack result = recipe.assemble(null, RecipeUtil.getRegistryAccess().orElseThrow());

		if (key.equals("output")) {
			return IVariable.from(result, null);
		}
		if (key.equals("iname")) {
			return IVariable.wrap(result.getHoverName().getString());
		}
		if (key.equals("icount")) {
			return IVariable.wrap(result.getCount());
		}
		if (key.equals("text")) {
			return IVariable.wrap(result.getCount() + "x$(br)" + result.getHoverName().getString());
		}
		if (key.startsWith("item")) {
			try {
				int index = Integer.parseInt(key.substring(4)) - 1;
				List<Ingredient> ingredients = recipe.placementInfo().ingredients();
				if (index >= 0 && index < ingredients.size()) {
					Ingredient ingredient = ingredients.get(index);
					ItemStack[] stacks = ingredient.items().toList().toArray(new ItemStack[0]);
					ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
					return IVariable.from(stack, null);
				}
			} catch (NumberFormatException e) {
				// Not a valid item key, fall through
			}
		}

		return null;
	}
}
