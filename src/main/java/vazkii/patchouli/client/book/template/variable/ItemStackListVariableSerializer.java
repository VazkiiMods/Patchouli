package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemStackListVariableSerializer implements IVariableSerializer<List> {
	@Override
	public List fromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return new ArrayList<>();
		}
		if (json.isJsonPrimitive()) {
			return ItemStackUtil.loadStackListFromString(json.getAsString());
		}
		if (json.isJsonObject()) {
			return ItemStackUtil.loadStackListFromJson(json.getAsJsonObject());
		}
		return ItemStackUtil.loadStackListFromJson(json.getAsJsonArray());
	}

	@Override
	public JsonElement toJson(List stacks) {
		JsonArray ret = new JsonArray();
		for (Object obj : stacks) {
			ItemStack stack = (ItemStack) obj;
			JsonObject item = new JsonObject();
			item.addProperty("item", stack.getItem().getName().toString());
			if (stack.getCount() != 1) {
				item.addProperty("count", stack.getCount());
			}
			if (stack.getTag() != null) {
				item.addProperty("nbt", stack.getTag().toString());
			}
			ret.add(item);
		}
		return ret;
	}

}
