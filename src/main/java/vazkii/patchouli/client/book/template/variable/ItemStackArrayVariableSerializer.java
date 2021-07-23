package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemStackArrayVariableSerializer extends GenericArrayVariableSerializer<ItemStack> {
	public ItemStackArrayVariableSerializer() {
		super(new ItemStackVariableSerializer(), ItemStack.class);
	}

	@Override
	public ItemStack[] fromJson(JsonElement json) {
		if (json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			List<ItemStack> stacks = new ArrayList<>();
			for (JsonElement e : array) {
				stacks.addAll(Arrays.asList(fromNonArray(e)));
			}
			return stacks.toArray(empty);
		}
		return fromNonArray(json);
	}

	public ItemStack[] fromNonArray(JsonElement json) {
		if (json.isJsonNull()) {
			return empty;
		}
		if (json.isJsonPrimitive()) {
			return ItemStackUtil.loadStackListFromString(json.getAsString()).toArray(empty);
		}
		if (json.isJsonObject()) {
			return new ItemStack[] { ItemStackUtil.loadStackFromJson(json.getAsJsonObject()) };
		}
		throw new IllegalArgumentException("Can't make an ItemStack from an array!");
	}

}
