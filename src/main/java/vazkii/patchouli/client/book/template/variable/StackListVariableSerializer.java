package vazkii.patchouli.client.book.template.variable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.StackList;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StackListVariableSerializer implements IVariableSerializer<StackList> {

	public static final StackList EMPTY = new StackList(Collections.emptyList());

	@Override
	public StackList fromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return EMPTY;
		}
		// legacy compat
		if (json.isJsonPrimitive()) {
			return new StackList(ItemStackUtil.loadStackListFromString(json.getAsString()));
		}
		if (json.isJsonArray()) {
			JsonArray arr = json.getAsJsonArray();
			List<ItemStack> deserial = new ArrayList<>(arr.size());
			arr.forEach(j -> deserial.add(VariableHelper.instance().<ItemStack>serializerForClass(ItemStack.class).fromJson(j)));
			return new StackList(deserial);
		}

		// if it's an object, assume a singleton ItemStack obj
		return new StackList(ImmutableList.of(CraftingHelper.getItemStack(json.getAsJsonObject(), true)));
	}

	@Override
	public JsonElement toJson(StackList list) {
		JsonObject elem = list.serialize();
		return elem.has("item") ? elem : elem.get("items");
	}
}
