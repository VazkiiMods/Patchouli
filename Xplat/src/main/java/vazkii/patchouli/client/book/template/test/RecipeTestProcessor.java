package vazkii.patchouli.client.book.template.test;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.*;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.client.base.ClientRecipes;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecipeTestProcessor implements IComponentProcessor {

	private Recipe<?> recipe;

	@Override
	public void setup(Level level, IVariableProvider variables) {
		// TODO probably add a recipe serializer?
		String recipeId = variables.get("recipe", level.registryAccess()).asString();
		RecipeHolder<Recipe<?>> recipe = ClientRecipes.INSTANCE.getRecipeById(ResourceLocation.parse(recipeId));
		if (recipe == null) {
			throw new IllegalArgumentException("No recipe with id " + recipeId + " found");
		}
		this.recipe = recipe.value();
	}

	@Override
	public @Nullable IVariable process(Level level, String key) {
		List<RecipeDisplay> display = recipe.display();
		if (display.isEmpty()) {
			return null;
		}
		if (key.startsWith("item")) {
			int index = Integer.parseInt(key.substring(4)) - 1;
			List<SlotDisplay> ingredients = switch (display.getFirst()) {
			case ShapedCraftingRecipeDisplay shaped -> shaped.ingredients();
			case ShapelessCraftingRecipeDisplay shapeless -> shapeless.ingredients();
			case FurnaceRecipeDisplay furnace -> List.of(furnace.ingredient());
			case SmithingRecipeDisplay smithing -> List.of(smithing.base(), smithing.addition());
			case StonecutterRecipeDisplay stonecutter -> List.of(stonecutter.input());
			default -> throw new IllegalArgumentException("Unsupported recipe display type: " + display.getFirst().getClass().getName());
			};
			SlotDisplay slotDisplay = ingredients.get(index);
			return IVariable.from(slotDisplay.resolveForFirstStack(SlotDisplayContext.fromLevel(level)), level.registryAccess());
		} else if (key.equals("text")) {
			ItemStack out = display.getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level));
			return IVariable.wrap(out.getCount() + "x$(br)" + out.getHoverName(), level.registryAccess());
		} else if (key.equals("icount")) {
			return IVariable.wrap(display.getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level)).getCount(), level.registryAccess());
		} else if (key.equals("iname")) {
			return IVariable.wrap(display.getFirst().result().resolveForFirstStack(SlotDisplayContext.fromLevel(level)).getHoverName().getString(), level.registryAccess());
		}

		return null;
	}

}
