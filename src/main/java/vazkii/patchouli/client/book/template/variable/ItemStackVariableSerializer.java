package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ItemStackVariableSerializer implements IVariableSerializer<ItemStack> {
	@Override
	public ItemStack fromJson(JsonElement json) {
		return ItemStackUtil.loadStackFromJson(json);
	}

	@Override
	public JsonElement toJson(ItemStack stack) {
		// Adapted from net.minecraftforge.common.crafting.StackList::toJson
		JsonObject ret = new JsonObject();
		ret.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
		if (stack.getCount() != 1) {
			ret.addProperty("count", stack.getCount());
		}
		if (stack.getTag() != null) {
			ret.addProperty("nbt", stack.getTag().toString());
		}
		return ret;
	}

}
