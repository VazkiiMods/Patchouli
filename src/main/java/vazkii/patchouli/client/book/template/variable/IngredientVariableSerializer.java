package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.recipe.Ingredient;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class IngredientVariableSerializer implements IVariableSerializer<Ingredient> {
	public static final IngredientVariableSerializer INSTANCE = new IngredientVariableSerializer();

	@Override
	public Ingredient fromJson(JsonElement json) {
		if (json.isJsonPrimitive()) {
			return Ingredient.ofStacks(ItemStackUtil.loadStackFromJson(json));
		}
		return Ingredient.fromJson(json);
	}

	@Override
	public JsonElement toJson(Ingredient stack) {
		return stack.toJson();
	}
}
