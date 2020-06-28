package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraft.util.text.StringTextComponent;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class TextComponentVariableSerializer implements IVariableSerializer<ITextComponent> {
	@Override
	public ITextComponent fromJson(JsonElement json) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			return new StringTextComponent(json.getAsString());
		}
		ITextComponent c = Serializer.func_240641_a_(json);
		return c;
	}

	@Override
	public JsonElement toJson(ITextComponent stack) {
		return Serializer.toJsonTree(stack);
	}
}
