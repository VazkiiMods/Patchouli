package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ItemStackVariableSerializer implements IVariableSerializer<ItemStack> {
	@Override
	public ItemStack fromJson(JsonElement json) {
		if (json.isJsonPrimitive()) {
			return ItemStackUtil.loadStackFromString(json.getAsString());
		}
		if (json.isJsonObject()) {
			return CraftingHelper.getItemStack(json.getAsJsonObject(), true);
		}
		throw new IllegalArgumentException("Can't make an ItemStack from an array!");
	}

	@Override
	public JsonElement toJson(ItemStack stack) {
		// Copied from net.minecraftforge.common.crafting.StackList::toJson
		JsonObject ret = new JsonObject();
		ret.addProperty("item", stack.getItem().getRegistryName().toString());
		if (stack.getCount() != 1) {
			ret.addProperty("count", stack.getCount());
		}
		if (stack.getTag() != null) {
			ret.addProperty("nbt", stack.getTag().toString());
		}
		return ret;
	}
}
