package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

import vazkii.patchouli.api.IVariableSerializer;

public class TextComponentVariableSerializer implements IVariableSerializer<Component> {
	@Override
	public Component fromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return Component.literal("");
		}
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			return Component.literal(json.getAsString());
		}
		return Serializer.fromJson(json);
	}

	@Override
	public JsonElement toJson(Component stack) {
		return Serializer.toJsonTree(stack);
	}
}
