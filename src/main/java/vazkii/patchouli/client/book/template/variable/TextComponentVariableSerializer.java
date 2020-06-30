package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Text.Serializer;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class TextComponentVariableSerializer implements IVariableSerializer<Text> {
	@Override
	public Text fromJson(JsonElement json) {
		if (json.isJsonNull()) {
			return new LiteralText("");
		}
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			return new LiteralText(json.getAsString());
		}
		Text c = Serializer.fromJson(json);
		return c;
	}

	@Override
	public JsonElement toJson(Text stack) {
		return Serializer.toJsonTree(stack);
	}
}
