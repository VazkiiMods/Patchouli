package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.StackList;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import java.util.Map;
import java.util.HashMap;

public class VariableHelperImpl implements VariableHelper {

	public VariableHelperImpl() {
		registerSerializer(new ItemStackVariableSerializer(), ItemStack.class);
		registerSerializer(new StackListVariableSerializer(), StackList.class);
		registerSerializer(new IngredientVariableSerializer(), Ingredient.class);
	}

	public Map<Class<?>, IVariableSerializer<?>> serializers = new HashMap<>();

	@Override
	public boolean hasSerializerFor(Class<?> clazz) {
		return serializers.containsKey(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IVariableSerializer<T> serializerForClass(Class<?> clazz) {
		return (IVariableSerializer<T>) serializers.get(clazz);
	}

	@Override
	public <T> IVariable createFromObject(T object) {
		Class<?> clazz = object.getClass();
		for (Class<?> c : serializers.keySet()) {
			if (c.isAssignableFrom(clazz)) {
				return createFromJson(serializerForClass(c).toJson(object));
			}
		}

		throw new IllegalArgumentException(String.format("Can't serialize object %s of type %s to IVariable", object, clazz));
	}

	@Override
	public IVariable createFromJson(JsonElement elem) {
		return new Variable(elem);
	}

	@Override
	public <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		serializers.put(clazz, serializer);
		return this;
	}

}
