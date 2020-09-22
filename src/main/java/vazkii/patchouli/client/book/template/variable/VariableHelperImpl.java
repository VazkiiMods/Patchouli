package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import java.util.HashMap;
import java.util.Map;

public class VariableHelperImpl implements VariableHelper {

	public VariableHelperImpl() {
		registerSerializer(new ItemStackVariableSerializer(), ItemStack.class);
		registerSerializer(new IngredientVariableSerializer(), Ingredient.class);
		registerSerializer(new TextComponentVariableSerializer(), ITextComponent.class);
	}

	public Map<Class<?>, IVariableSerializer<?>> serializers = new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> IVariableSerializer<T> serializerForClass(Class<?> clazz) {
		return (IVariableSerializer<T>) serializers.get(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IVariable createFromObject(T object) {
		Class<?> clazz = object.getClass();
		for (Map.Entry<Class<?>, IVariableSerializer<?>> e : serializers.entrySet()) {
			if (e.getKey().isAssignableFrom(clazz)) {
				return create(((IVariableSerializer<T>) e.getValue()).toJson(object), clazz);
			}
		}

		throw new IllegalArgumentException(String.format("Can't serialize object %s of type %s to IVariable", object, clazz));
	}

	@Override
	public IVariable createFromJson(JsonElement elem) {
		return create(elem, null);
	}

	private IVariable create(JsonElement elem, Class<?> originator) {
		return new Variable(elem, originator);
	}

	@Override
	public <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		serializers.put(clazz, serializer);
		return this;
	}

}
